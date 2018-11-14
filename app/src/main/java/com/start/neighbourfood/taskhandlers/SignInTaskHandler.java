package com.start.neighbourfood.taskhandlers;

import android.app.Activity;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.login.LoginManager;
import com.start.neighbourfood.NFApplication;
import com.start.neighbourfood.Utils.SharedPreferenceUtils;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.ServiceConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInTaskHandler implements TaskHandler {

    private SharedPreferenceUtils sharedPreferenceUtils;
    private Activity activity;

    public SignInTaskHandler(Activity activity){
        this.activity = activity;
        sharedPreferenceUtils = NFApplication.getSharedPreferenceUtils();
    }

    @Override
    public void onTaskCompleted(JSONObject request, JSONObject result) {

        try {
            sharedPreferenceUtils.setValue(ServiceConstants.IS_SIGNED_KEY, request.getString("userUid"));
            sharedPreferenceUtils.setValue(ServiceConstants.USER_INFO, request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //navigateToHome();
    }

    @Override
    public void onErrorResponse(JSONObject request, VolleyError error) {
        //hideProgressDialog();
        Toast.makeText(activity, "Something went wrong. Please try again later.", Toast.LENGTH_LONG);
        LoginManager.getInstance().logOut();
    }
}
