package com.start.neighbourfood.pages;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
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
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.start.neighbourfood.R;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements TaskHandler {

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


    private ServiceManager serviceManager;

    Handler handler = new Handler();
    int count = 60;
    // Define the code block to be executed

    // Start the initial runnable task by posting through the handler
    private Button buttonCode;
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            //Log.d("Handlers", "Called on main thread");
            buttonCode.setText(String.format("Wait(%s)", count--));

            // Repeat this the same runnable code block again another 1 seconds
            if (count > 0) {
                handler.postDelayed(runnableCode, 1000);
            } else {
                count = 60;
                buttonCode.setText("Resend");
                buttonCode.setEnabled(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_login_vertical);
        } else {
            setContentView(R.layout.activity_login_horizontal);
        }

        serviceManager = ServiceManager.getInstance(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        LoginButton loginButton = findViewById(R.id.button_facebook_login);
        bindLoginActionButton(loginButton);
        buttonCode = findViewById(R.id.buttonGetVerificationCode);

        editTextCode = findViewById(R.id.editTextCode);
        editTextPhone = findViewById(R.id.editTextPhone);

        findViewById(R.id.buttonGetVerificationCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkConnected()) {
                    Toast.makeText(LoginActivity.this, "No internet connection!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(editTextPhone.getText()) || editTextPhone.length() != 10 || !isNetworkConnected()) {
                    Toast.makeText(LoginActivity.this, "Incorrect Number!", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    sendVerificationCode();
                    handler.post(runnableCode);
                }


            }
        });


        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkConnected()) {
                    Toast.makeText(LoginActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editTextCode.getText())) {
                    Toast.makeText(LoginActivity.this, "Incorrect code !", Toast.LENGTH_SHORT).show();
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

        if (phone.length() != 10) {
            editTextPhone.setError("Please enter a valid phone, without country code !");
            editTextPhone.requestFocus();
            return;
        }


        phone = "+91" + phone;


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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser mUser = mAuth.getCurrentUser();
                            mUser.getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                sharedPreferenceUtils.setValue(ServiceConstants.authToken, task.getResult().getToken());
                                                sharedPreferenceUtils.setValue(ServiceConstants.authTokenExpirationTime, task.getResult().getExpirationTimestamp());
                                                serviceManager.fetchUserfromUid(mUser.getUid(), LoginActivity.this);
                                            } else {
                                                hideProgressDialog();
                                                navigateToLoginPage();
                                                Toast.makeText(LoginActivity.this, "Unable to fetch auth token", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
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

    @Override
    public void onTaskCompleted(JSONObject request, JSONObject result) {
        try {
            sharedPreferenceUtils.setValue(ServiceConstants.IS_SIGNED_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
            sharedPreferenceUtils.setValue(ServiceConstants.USER_INFO, result.getJSONObject("Result").toString());
            navigateToHome();
        } catch (Exception e) {
            LoginManager.getInstance().logOut();
        }
        hideProgressDialog();
    }

    @Override
    public void onErrorResponse(JSONObject request, VolleyError error) {
        hideProgressDialog();
        if (error.networkResponse.statusCode == 404) {
            navigateToSignUpPage(TextUtils.isEmpty(editTextPhone.getText().toString()) ? FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() : editTextPhone.getText().toString());
        }
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
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Code Sent ", Toast.LENGTH_SHORT).show();
            codeSent = s;
        }
    };
}

