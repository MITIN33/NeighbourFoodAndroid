package com.start.neighbourfood.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.models.v1.UserBaseInfo;
import com.start.neighbourfood.services.Config;

import java.io.IOException;

public class SharedPreferenceUtils {
    private static SharedPreferenceUtils mSharedPreferenceUtils;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;

    private SharedPreferenceUtils(Context context) {
        mSharedPreferences = context.getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
    }

    /**
     * Creates single instance of SharedPreferenceUtils
     *
     * @param context context of Activity or Service
     * @return Returns instance of SharedPreferenceUtils
     */
    public static synchronized SharedPreferenceUtils getInstance(Context context) {

        if (mSharedPreferenceUtils == null) {
            mSharedPreferenceUtils = new SharedPreferenceUtils(context.getApplicationContext());
        }
        return mSharedPreferenceUtils;
    }

    /**
     * Stores String value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, String value) {
        mSharedPreferencesEditor.putString(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Stores int value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, int value) {
        mSharedPreferencesEditor.putInt(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Stores Double value in String format in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, double value) {
        setValue(key, Double.toString(value));
    }

    /**
     * Stores long value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, long value) {
        mSharedPreferencesEditor.putLong(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Stores boolean value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, boolean value) {
        mSharedPreferencesEditor.putBoolean(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Stores boolean value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, Object value) {
        Gson gson = new Gson();
        setValue(key, gson.toJson(value));
    }

    /**
     * Retrieves String value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    public String getStringValue(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    /**
     * Retrieves int value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    public int getIntValue(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Retrieves long value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    public long getLongValue(String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    /**
     * Retrieves boolean value from preference
     *
     * @param keyFlag      key of preference
     * @param defaultValue default value if no key found
     */
    public boolean getBoolanValue(String keyFlag, boolean defaultValue) {
        return mSharedPreferences.getBoolean(keyFlag, defaultValue);
    }

    /**
     * Removes key from preference
     *
     * @param key key of preference that is to be deleted
     */
    public void removeKey(String key) {
        if (mSharedPreferencesEditor != null) {
            mSharedPreferencesEditor.remove(key);
            mSharedPreferencesEditor.commit();
        }
    }


    /**
     * Clears all the preferences stored
     */
    public void clear() {
        mSharedPreferencesEditor.clear().commit();
    }

    public UserBaseInfo getUserBaseInfo(){
         ObjectMapper objectMapper = new ObjectMapper();
        try {
            String result = getStringValue(ServiceConstants.USER_INFO, null);
            if (result != null) {
                return objectMapper.readValue(result, UserBaseInfo.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}