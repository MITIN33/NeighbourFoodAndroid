package com.start.neighbourfood.auth;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface TaskHandler {

    void onTaskCompleted(JSONObject result);

    void onErrorResponse(VolleyError error);


}
