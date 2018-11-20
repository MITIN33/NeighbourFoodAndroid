package com.start.neighbourfood.auth;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface TaskHandler {

    void onTaskCompleted(JSONObject request, JSONObject result);

    void onErrorResponse(JSONObject request, VolleyError error);


}
