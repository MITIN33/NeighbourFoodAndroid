package com.start.neighbourfood.services;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

public class ObjectRequest<T> extends JsonRequest<T> {

    private static final Gson gson = new Gson();
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String TAG = "ObjectRequest";
    private final Class clazz;
    private final Response.Listener<T> listener;
    private int mMethod;
    private String mUrl;
    private String mRequestBody;
    private Map<String, String> mHeaders;
    private Map<String, Object> mParams;

    public ObjectRequest(int method, String url, String jsonObject, Class clazz,
                         Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonObject, listener, errorListener);
        this.clazz = clazz;
        this.listener = listener;
        mRequestBody = jsonObject;
        this.mMethod = method;
        this.mUrl = url;
    }

    @Override
    public String getUrl() {
        if (mMethod == Request.Method.GET) {
            StringBuilder stringBuilder = new StringBuilder(mUrl);
            if (mParams != null) {
                Iterator<Map.Entry<String, Object>> iterator = mParams.entrySet().iterator();
                int i = 1;
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if (i == 1) {
                        stringBuilder.append("?" + entry.getKey() + "=" + entry.getValue());
                    } else {
                        stringBuilder.append("&" + entry.getKey() + "=" + entry.getValue());
                    }
                    iterator.remove(); // avoids a ConcurrentModificationException
                    i++;
                }
                mUrl = stringBuilder.toString();
            }
        }
        return mUrl;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Unsupported Encoding while trying to get the bytes of " + mRequestBody +
                    "using " + PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {

            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            if (clazz.getName().equals(JSONObject.class.getName())) {
                try {
                    JSONObject object = new JSONObject(json);
                    return Response.success(object, HttpHeaderParser.parseCacheHeaders(response));
                } catch (JSONException ex) {
                    return Response.error(new ParseError(ex));
                }

            } else if (clazz.getName().equals(JSONArray.class.getName())) {
                try {
                    JSONArray object = new JSONArray(json);
                    return Response.success(object, HttpHeaderParser.parseCacheHeaders(response));
                } catch (JSONException ex) {
                    return Response.error(new ParseError(ex));
                }
            } else {
                return Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mHeaders.isEmpty()) {
            return super.getHeaders();
        } else {
            return mHeaders;
        }
    }

    public void setHeaders(Map<String, String> headers) {
        mHeaders = headers;
    }
}