package com.start.neighbourfood.services;


import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.start.neighbourfood.BuildConfig;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.NfMessageNotification;
import com.start.neighbourfood.models.ServiceConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServiceManager {

    private static final String TAG = ServiceManager.class.getSimpleName();
    private static ServiceManager mInstance;
    private RequestQueue mRequestQueue;

    private ServiceManager(Context context) {
        mRequestQueue = getRequestQueue(context);
    }

    public static synchronized ServiceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ServiceManager(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue(Context mCtx) {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req) {
        if (req == null) {
            Log.i(TAG, "Request cannot be null");
        }

        int socketTimeout = 10000;//10 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);

        mRequestQueue.add(req);
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
        createRequest(Request.Method.GET, taskHandler, url, null);
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

    public void updateProfilePhoto(String id, String photoUrl,JSONObject jsonObject,final TaskHandler taskHandler){
        String url = getFullUrl(ServiceConstants.userApiPath) + "/" + id ;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
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
        createRequest(Request.Method.GET, taskHandler, url, null);
    }

    public void fetchFoodItemsForFlat(String flatId, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.selleritemApiPath) + "/details/" + flatId;
        createRequest(Request.Method.GET, taskHandler, url, null);
    }


    public void fetchAllFoodItem(final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.foodApiPAth);
        createRequest(Request.Method.GET, taskHandler, url, null);
    }

    public void addSellerItem(JSONObject jsonObject, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.selleritemApiPath);
        createRequest(Request.Method.POST, taskHandler, url, jsonObject);
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
        createRequest(Request.Method.GET, taskHandler, url, null);
    }

    public void fetchAllFlatsInApartment(String apartmetnID, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.flatApiPAth) + "/apartments/" + apartmetnID;
        createRequest(Request.Method.GET, taskHandler, url, null);
    }

    public void placeOrder(JSONObject jsonObject, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.orderApiPath);
        createRequest(Request.Method.POST, taskHandler, url, jsonObject);
    }

    public void addUserTokenInfo(JSONObject jsonObject, TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.deviceToken);
        createRequest(Request.Method.PUT,taskHandler, url, jsonObject);
    }

    public void updateOrderStatus(String orderID,String status, TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.orderApiPath) + "/" + orderID + "/" + status;
        createRequest(Request.Method.PUT,taskHandler, url, null);
    }

    public void getUserNotification(String userUid, TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.deviceToken) + "/" + userUid;
        createRequest(Request.Method.GET, taskHandler, url, null);
    }

    public void sendNotification(NfMessageNotification messageNotification) {
        Gson gson = new Gson();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(gson.toJson(messageNotification));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(ServiceConstants.fcmUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "key=" + ServiceConstants.fcmServerKey);
                return header;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        addToRequestQueue(request);
    }

    public void fetchOrderDetail(String orderID, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.orderApiPath) + "/" + orderID;
        createRequest(Request.Method.GET,taskHandler, url, null);
    }

    public void fetchAllPastOrderForBuyer(String userId, final TaskHandler taskHandler) {
        String url = getFullUrl(ServiceConstants.orderApiPath) + "/user/" + userId;
        createRequest(Request.Method.GET,taskHandler, url, null);
    }

    private String getValue(JSONObject object, String key) {
        try {
            return object.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createRequest(int method, final TaskHandler taskHandler, String url, JSONObject jsonObject) {

        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonObject, new Response.Listener<JSONObject>() {
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
        } catch (Exception ex) {
            Log.i(TAG, String.format("Error in network call. Exception :%s", ex.getMessage()));
        }
    }
}
