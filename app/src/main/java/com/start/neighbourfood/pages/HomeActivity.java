package com.start.neighbourfood.pages;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.start.neighbourfood.R;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.fragments.FoodItemsFragment;
import com.start.neighbourfood.models.FlatsInfo;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "HOME_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Check for login
        if (user == null || getFromSharedPreference(ServiceConstants.signedInKey) == null) {
            navigateToLoginPage();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        showProgressDialog();
        ServiceManager.getInstance(this).fetchAvailableHoods(new PopulateHoodTaskHandler());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            // Handle the camera action
        } else if (id == R.id.nav_trackOrder) {

        } else if (id == R.id.nav_previousOrders) {

        } else if (id == R.id.nav_logout) {
            signOut();
        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class PopulateHoodTaskHandler implements TaskHandler {

        @Override
        public void onTaskCompleted(JSONObject result) {

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<FlatsInfo> flatsInfos = objectMapper.readValue(result.getJSONArray("Result").toString(), new TypeReference<List<FlatsInfo>>() {
                });
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                FoodItemsFragment fragment = new FoodItemsFragment(flatsInfos);
                transaction.replace(R.id.food_content_fragment, fragment);
                transaction.commit();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hideProgressDialog();
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "onErrorResponse: Unable to load", error);
        }
    }

}
