package com.start.neighbourfood.services;


import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.start.neighbourfood.BuildConfig;
import com.start.neighbourfood.Utils.SharedPreferenceUtils;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.models.UserBaseInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceManager {
    private static ServiceManager mInstance;
    private static Context mCtx;
    private ProgressDialog mProgressDialog;
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

    private String getFullUrl(String url) {
        return BuildConfig.SERVER_URL + url;
    }


    /***********
     /
     /Call to fetch user for the uid
     /
     /***********/
    public void fetchUserfromUid(String uid, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.userApiPath) + "/" + uid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                taskHandler.onTaskCompleted(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                taskHandler.onErrorResponse(error);
            }
        });
        addToRequestQueue(jsonObjectRequest);
    }

    /***********
     /
     /Call to fetch user for the uid
     /
     /***********/

    public void createUser(JSONObject userBaseInfo, final TaskHandler taskHandler) {

        String url = getFullUrl(ServiceConstants.userApiPath);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, userBaseInfo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                taskHandler.onTaskCompleted(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                taskHandler.onErrorResponse(error);
            }
        });
        addToRequestQueue(jsonObjectRequest);
    }


    public void fetchAvailableHoods(JSONObject userBaseInfo,final TaskHandler taskHandler) throws IllegalAccessException {
        String url = getFullUrl(ServiceConstants.apartmentApiPath) +"/" + getValue(userBaseInfo,"apartmentID") + "/user/" + getValue(userBaseInfo,"userUid");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                taskHandler.onTaskCompleted(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                taskHandler.onErrorResponse(error);
            }
        });
        addToRequestQueue(jsonObjectRequest);
    }

    public void fetchFoodItemForFlat(String flatId,final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.foodApiPAth)  + "/" + flatId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                taskHandler.onTaskCompleted(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                taskHandler.onErrorResponse(error);
            }
        });
        addToRequestQueue(jsonObjectRequest);
    }

    private String getValue(JSONObject object, String key){
        try {
            return object.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
