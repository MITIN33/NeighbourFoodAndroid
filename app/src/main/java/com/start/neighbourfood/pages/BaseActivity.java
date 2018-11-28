package com.start.neighbourfood.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.start.neighbourfood.NFApplication;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.SharedPreferenceUtils;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.services.ServiceManager;

public class BaseActivity extends AppCompatActivity {


    public SharedPreferenceUtils sharedPreferenceUtils;

    public ServiceManager serviceManager;

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceUtils = NFApplication.getSharedPreferenceUtils();
        serviceManager = NFApplication.getServiceManager();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(BaseActivity.this);
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
            mProgressDialog = null;
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

    public void navigateToProfilePage() {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }


    public void navigateToHome() {
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        hideProgressDialog();
    }

    public void navigateToOrderHistory() {
        Intent i = new Intent(getApplicationContext(), OrderHistoryActivity.class);
        startActivity(i);
    }

    public void navigateToActivity(Class clazz) {
        Intent i = new Intent(getApplicationContext(), clazz);
        startActivity(i);
    }

    public void navigateToBuyerTrackOrder(String orderID, String flatNumber){
        Intent i = new Intent(getApplicationContext(), OrderTrackBuyerActivity.class);
        i.putExtra(ServiceConstants.ORDER_ID,orderID);
        i.putExtra(ServiceConstants.FLAT_NUMBER,flatNumber);
        startActivity(i);
    }

    public void navigateToSellerTrackOrder(String orderID, String flatNumber){
        Intent i = new Intent(getApplicationContext(), OrderTrackSellerActivity.class);
        i.putExtra(ServiceConstants.ORDER_ID,orderID);
        i.putExtra(ServiceConstants.FLAT_NUMBER,flatNumber);
        startActivity(i);
    }

    public void navigateToLoginPage() {
        Intent i = new Intent(this, LoginActivity.class);
        // Closing all the Activities
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        startActivity(i);
    }

    public void signOut() {
        showProgressDialog();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        sharedPreferenceUtils.setValue(ServiceConstants.IS_SIGNED_KEY, null);
        sharedPreferenceUtils.setValue(ServiceConstants.DEVICE_REGISTERED, "false");
        hideProgressDialog();
        navigateToLoginPage();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public String getProfileImageName(String userID){
        return userID+ ".jpg";
    }
}
