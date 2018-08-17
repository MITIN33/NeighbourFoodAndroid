package com.start.neighbourfood.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.start.neighbourfood.R;
import com.start.neighbourfood.models.ServiceConstants;

public class BaseActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("nfService", MODE_PRIVATE);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public void navigateToSignUpPage(String phone) {
        Intent i = new Intent(this, SignupActivity.class);
        i.putExtra("phoneNumber", phone);
        startActivity(i);
        hideProgressDialog();
    }

    public void navigateToHome() {
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        hideProgressDialog();
    }

    public void navigateToLoginPage() {
        Intent i = new Intent(this, LoginActivity.class);
        // Closing all the Activities
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        startActivity(i);
    }

    public void saveInSharedPreference(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getFromSharedPreference(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void signOut() {
        showProgressDialog();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        saveInSharedPreference(ServiceConstants.signedInKey, null);
        hideProgressDialog();
        navigateToLoginPage();
    }
}
