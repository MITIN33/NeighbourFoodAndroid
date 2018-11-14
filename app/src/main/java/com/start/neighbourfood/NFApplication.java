package com.start.neighbourfood;

import android.app.Application;
import android.content.Context;

import com.start.neighbourfood.Utils.NotificationUtils;
import com.start.neighbourfood.Utils.SharedPreferenceUtils;
import com.start.neighbourfood.services.ServiceManager;

public class NFApplication extends Application {

    private static SharedPreferenceUtils sharedPreferenceUtils;
    private static ServiceManager serviceManager;
    private static Context context;
    private NotificationUtils notificationUtils;

    public static Context getAppContext() {
        return context;
    }

    public static SharedPreferenceUtils getSharedPreferenceUtils() {
        return sharedPreferenceUtils;
    }

    public static ServiceManager getServiceManager() {
        return serviceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        notificationUtils = new NotificationUtils(getApplicationContext());
        sharedPreferenceUtils = SharedPreferenceUtils.getInstance(getApplicationContext());
        serviceManager = ServiceManager.getInstance(getApplicationContext());
    }

    public NotificationUtils getNotificationUtils() {
        return notificationUtils;
    }
}
