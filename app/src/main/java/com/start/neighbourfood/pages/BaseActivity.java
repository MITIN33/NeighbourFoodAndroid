package com.start.neighbourfood.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.facebook.login.LoginManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.start.neighbourfood.R;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.models.v1.UserBaseInfo;
import com.start.neighbourfood.services.Config;

import java.io.IOException;

public class BaseActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
    }

    private SharedPreferences getLocalSharedPreference() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences("nfService", MODE_PRIVATE);
        }
        return sharedPreferences;
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

    public void navigateToOrderHistory() {
        Intent i = new Intent(getApplicationContext(), BuyerOrderActivity.class);
        startActivity(i);
    }

    public void navigateToBuyerTrackOrder(String orderID, String flatNumber){
        Intent i = new Intent(getApplicationContext(), OrderTrackBuyerActivity.class);
        i.putExtra(ServiceConstants.orderIdLabel,orderID);
        i.putExtra(ServiceConstants.orderFlatNumberLabel,flatNumber);
        startActivity(i);
    }

    public void navigateToSellerTrackOrder(String orderID, String flatNumber){
        Intent i = new Intent(getApplicationContext(), OrderTrackSellerActivity.class);
        i.putExtra(ServiceConstants.orderIdLabel,orderID);
        i.putExtra(ServiceConstants.orderFlatNumberLabel,flatNumber);
        startActivity(i);
    }

    public void navigateToLoginPage() {
        Intent i = new Intent(this, LoginActivity.class);
        // Closing all the Activities
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        startActivity(i);
    }

    public boolean saveStringInSharedPreference(String key, String value) {
        try {
            SharedPreferences.Editor editor = getLocalSharedPreference().edit();
            editor.putString(key, value);
            editor.commit();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getFromSharedPreference(String key) {
        try {
            return getLocalSharedPreference().getString(key, null);
        }
        catch (Exception ex){
            return null;
        }
    }

    public void signOut() {
        showProgressDialog();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        saveStringInSharedPreference(ServiceConstants.signedInKey, null);
        saveStringInSharedPreference("deviceRegistered", "false");
        hideProgressDialog();
        navigateToLoginPage();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public UserBaseInfo getUserBaseInfo() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String result = getFromSharedPreference(ServiceConstants.userDetail);
            if (result != null) {
                return objectMapper.readValue(result, UserBaseInfo.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
