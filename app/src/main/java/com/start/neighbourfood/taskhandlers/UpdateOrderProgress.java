package com.start.neighbourfood.taskhandlers;

import android.app.Activity;

import com.android.volley.VolleyError;
import com.start.neighbourfood.auth.TaskHandler;

import org.json.JSONObject;

public class UpdateOrderProgress implements TaskHandler{

    public UpdateOrderProgress(Activity activity){

    }

    @Override
    public void onTaskCompleted(JSONObject request, JSONObject result) {

    }

    @Override
    public void onErrorResponse(JSONObject request, VolleyError error) {

    }
}
