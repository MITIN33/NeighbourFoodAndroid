package com.start.neighbourfood.Utils;

import android.app.Activity;
import android.content.Intent;

import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.pages.HomeActivity;
import com.start.neighbourfood.pages.LoginActivity;
import com.start.neighbourfood.pages.OrderHistoryActivity;
import com.start.neighbourfood.pages.OrderTrackBuyerActivity;
import com.start.neighbourfood.pages.OrderTrackSellerActivity;
import com.start.neighbourfood.pages.SignupActivity;

public class NavigationHelper {

    private Activity activity;

    public NavigationHelper(Activity mActivity){
        activity = mActivity;
    }

    public void navigateToSignUpPage(String phone) {
        Intent i = new Intent(activity, SignupActivity.class);
        i.putExtra(ServiceConstants.PHONE_NUMBER, phone);
        activity.startActivity(i);
    }

    public void navigateToHome() {
        Intent i = new Intent(activity, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(i);
    }

    public void navigateToOrderHistory() {
        Intent i = new Intent(activity, OrderHistoryActivity.class);
        activity.startActivity(i);
    }

    public void navigateToBuyerTrackOrder(String orderID, String flatNumber){
        Intent i = new Intent(activity, OrderTrackBuyerActivity.class);
        i.putExtra(ServiceConstants.ORDER_ID,orderID);
        i.putExtra(ServiceConstants.FLAT_NUMBER,flatNumber);
        activity.startActivity(i);
    }

    public void navigateToSellerTrackOrder(String orderID, String flatNumber){
        Intent i = new Intent(activity, OrderTrackSellerActivity.class);
        i.putExtra(ServiceConstants.ORDER_ID,orderID);
        i.putExtra(ServiceConstants.FLAT_NUMBER,flatNumber);
        activity.startActivity(i);
    }

    public void navigateToLoginPage() {
        Intent i = new Intent(activity, LoginActivity.class);
        // Closing all the Activities
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        activity.startActivity(i);
    }
}
