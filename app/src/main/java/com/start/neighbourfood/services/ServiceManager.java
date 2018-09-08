package com.start.neighbourfood.services;


import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.start.neighbourfood.BuildConfig;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.ServiceConstants;

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
        if (req != null) {
            int socketTimeout = 10000;//10 seconds
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
        }
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
        createGetRequest(taskHandler, url);
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


    public void fetchAvailableHoods(JSONObject userBaseInfo, final TaskHandler taskHandler) throws IllegalAccessException {
        String url = getFullUrl(ServiceConstants.apartmentApiPath) + "/" + getValue(userBaseInfo, "apartmentID") + "/user/" + getValue(userBaseInfo, "userUid");
        createGetRequest(taskHandler, url);
    }

    public void fetchFoodItemsForFlat(String flatId, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.selleritemApiPath) + "/details/" + flatId;
        createGetRequest(taskHandler, url);
    }


    public void fetchAllFoodItem(final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.foodApiPAth);
        createGetRequest(taskHandler, url);
    }

    public void addSellerItem(JSONObject jsonObject, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.selleritemApiPath);
        createPostRequest(taskHandler, url, jsonObject);
    }

    public void removeSellerItem(String sellerITemID, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.selleritemApiPath) + "/" + sellerITemID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
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

    public void updateSellerItem(String id, JSONObject sellerITem, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.selleritemApiPath) + "/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, sellerITem, new Response.Listener<JSONObject>() {
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

    public void fetchAllApartments(final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.apartmentApiPath);
        createGetRequest(taskHandler, url);
    }

    public void fetchAllFlatsInApartment(String apartmetnID, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.flatApiPAth) + "/apartments/" + apartmetnID;
        createGetRequest(taskHandler, url);
    }

    private String getValue(JSONObject object, String key) {
        try {
            return object.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createGetRequest(final TaskHandler taskHandler, String url) {
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

    private void createPostRequest(final TaskHandler taskHandler, String url, JSONObject jsonObject) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
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
}
