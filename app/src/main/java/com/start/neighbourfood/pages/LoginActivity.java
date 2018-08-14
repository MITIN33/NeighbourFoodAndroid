package com.start.neighbourfood.pages;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.neighbourfood.start.neighbourfood.R;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String TAG = "FacebookLogin";
    EditText editTextPhone, editTextCode;
    private String codeSent;
    private CallbackManager mCallbackManager;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            Toast.makeText(getApplicationContext(),
                    "Verification success ", Toast.LENGTH_LONG).show();
            signInWithCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e);
            hideProgressDialog();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }
            findViewById(R.id.buttonGetVerificationCode).setEnabled(true);
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            findViewById(R.id.buttonGetVerificationCode).setEnabled(false);
            ((Button) findViewById(R.id.buttonGetVerificationCode)).setText("Wait (60s)");
            Toast.makeText(getApplicationContext(),
                    "Code Sent ", Toast.LENGTH_LONG).show();
            codeSent = s;
            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            findViewById(R.id.buttonGetVerificationCode).setEnabled(true);
                            ((Button) findViewById(R.id.buttonGetVerificationCode)).setText("Resend");
                        }
                    });
                }
            }, 60000);
        }
    };
    private ServiceManager serviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        serviceManager = ServiceManager.getInstance(this);
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        LoginButton loginButton = findViewById(R.id.button_facebook_login);
        bindLoginActionButton(loginButton);

        editTextCode = findViewById(R.id.editTextCode);
        editTextPhone = findViewById(R.id.editTextPhone);

        findViewById(R.id.buttonGetVerificationCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextPhone.getText()) || editTextPhone.length() != 10) {
                    return;
                } else {
                    sendVerificationCode();
                }
            }
        });


        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextCode.getText())) {
                    return;
                } else {
                    verifySignInCode();
                }
            }
        });
        // Set up the login form.

    }

    protected void onPostResume() {
        super.onPostResume();
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
    }

    private void sendVerificationCode() {

        String phone = editTextPhone.getText().toString();

        if (phone.isEmpty()) {
            editTextPhone.setError("Phone number is required");
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            editTextPhone.setError("Please enter a valid phone");
            editTextPhone.requestFocus();
            return;
        }

        if (!phone.startsWith("+")) {
            phone = "+91" + phone;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        signInWithCredential(credential);
    }

    private void verifySignInCode() {
        try {
            String code = editTextCode.getText().toString();
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(codeSent, code);
            signInWithCredential(phoneAuthCredential);
        } catch (Exception e) {
            Log.e("ANDROID_LOGS", e.getMessage());
            Toast.makeText(getApplicationContext(),
                    "Something did not go right !", Toast.LENGTH_LONG).show();
        }
    }

    private void signInWithCredential(AuthCredential credential) {
        showProgressDialog();
        final String url = "http://nfservice.azurewebsites.net/api/user/";
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            ServiceManager.getInstance(getApplicationContext()).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, url + user.getUid(), null, onSucess(), onError()) {
                                @Override
                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                    //if (response.statusCode == 200){
                                    try {
                                        return Response.success(new JSONObject(new String(response.data)), HttpHeaderParser.parseCacheHeaders(response));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                    //}
                                }
                            });

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this,
                                        "Incorrect Information Provided.", Toast.LENGTH_LONG).show();
                            }
                            findViewById(R.id.buttonGetVerificationCode).setEnabled(true);
                        }
                    }
                });
    }

    private Response.Listener<JSONObject> onSucess() {
        return new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i("VOLLEY", response.toString());
                try {
                    if ("200".equals(response.getString("statusCode"))) {
                        saveInSharedPreference(ServiceConstants.signedInKey, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        navigateToHome();
                    } else {
                        navigateToSignUpPage(editTextPhone.getText().toString());
                    }
                    hideProgressDialog();
                } catch (JSONException e) {
                    e.printStackTrace();

                    // logout if logged in facebook
                    LoginManager.getInstance().logOut();
                    hideProgressDialog();
                }
            }
        };
    }

    private Response.ErrorListener onError() {
        return new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                hideProgressDialog();
                Log.i("VOLLEY", error.toString());
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void bindLoginActionButton(LoginButton loginButton) {

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }
}

