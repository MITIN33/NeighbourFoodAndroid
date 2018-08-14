package com.start.neighbourfood.auth;

import android.content.Context;

import org.json.JSONObject;

public abstract class TaskHandler {
    public Context context;

    public TaskHandler(Context context) {
        this.context = context;
    }

    public abstract void onTaskCompleted(JSONObject result);

    public void onError() {
    }

}
