package com.start.neighbourfood.services;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ServiceManager {
    private static ServiceManager mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    private ServiceManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized ServiceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ServiceManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


}
