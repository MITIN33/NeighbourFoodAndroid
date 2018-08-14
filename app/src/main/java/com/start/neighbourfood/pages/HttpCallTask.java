package com.start.neighbourfood.pages;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpCallTask extends AsyncTask<String, Integer, String> {

    private final String TAG = "HTTPCALL_TASK";
    private Context context;
    private TaskHandler taskHandler;

    public HttpCallTask(TaskHandler taskHandler) {
        this.context = taskHandler.context;
        this.taskHandler = taskHandler;

    }

    @Override
    protected String doInBackground(String... strings) {
        JSONObject jsonObject = new JSONObject();
        String url = "http://nfservice.azurewebsites.net/api/apartment";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    taskHandler.onTaskCompleted(new JSONObject(response.get(0).toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: Call Failed", error);
                taskHandler.onError();
            }
        });
        // String url = "http://nfservice.azurewebsites.net/api/user";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        /*try {
            jsonObject.put("userUid", user.getUid());
            jsonObject.put("apartmentId", "849eff55-e94f-444a-9bd2-8b963d863428");
            jsonObject.put("flatID", "102");
            jsonObject.put("userName", user.getDisplayName());
        } catch (JSONException ex) {

        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                taskHandler.onTaskCompleted(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    if (response.data.length == 0) {
                        byte[] responseData = "{}".getBytes("UTF8");
                        response = new NetworkResponse(response.statusCode, responseData, response.headers, response.notModified);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };
        */
        ServiceManager.getInstance(context).addToRequestQueue(request);
        return "success";
    }
}
