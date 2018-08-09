package com.start.neighbourfood.auth;

import android.content.Context;
import android.content.Intent;

import com.facebook.login.widget.LoginButton;
import com.start.neighbourfood.pages.LoginActivity;

public class SessionManager {
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    private FacebookLoginManager facebookLoginManager;
    // Constructor
    public SessionManager(Context context) {
        this._context = context.getApplicationContext();
        facebookLoginManager = new FacebookLoginManager(context);
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        facebookLoginManager.signOut();
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return facebookLoginManager.getUser() != null;
    }

    public void startLoginAction(LoginButton loginButton) {
        facebookLoginManager.loginActionButton(loginButton);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookLoginManager.onActivityResult(requestCode, resultCode, data);
    }
}
