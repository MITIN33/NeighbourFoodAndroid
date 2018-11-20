package com.start.neighbourfood.Utils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {

    private static Gson gson;

    public static Gson getInstance() {
        if (gson == null){
            gson = new Gson();
        }
        return gson;
    }

    public static JSONObject toJSONObject(Object object){
        String jsonString = getInstance().toJson(object);
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
