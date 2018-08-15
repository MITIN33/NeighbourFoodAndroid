package com.start.neighbourfood.auth;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface TaskHandler {

    public abstract void onTaskCompleted(JSONObject result);

    public abstract void onErrorResponse(VolleyError error);


}
