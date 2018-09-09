package com.start.neighbourfood.pages;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.start.neighbourfood.R;
import com.start.neighbourfood.fragments.FlatListFragment;
import com.start.neighbourfood.fragments.SellerFoodFragment;
import com.start.neighbourfood.models.ServiceConstants;

import java.util.List;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "HOME_ACTIVITY";
    private List<String> lastSearches;
    private SearchView searchView;
    private FlatListFragment fragment;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = FirebaseAuth.getInstance().getCurrentUser();
        // Check for login
        if (user == null || getFromSharedPreference(ServiceConstants.signedInKey) == null) {
            navigateToLoginPage();
            return;
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
        if (isNetworkConnected()) {
            loadFragment(new FlatListFragment());
        } else {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_LONG).show();
        }
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
        if (!isNetworkConnected()) {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_LONG).show();
        } else {
            if (id == R.id.nav_settings) {

            } else if (id == R.id.nav_trackOrder) {

            } else if (id == R.id.nav_home) {
                loadFragment(new FlatListFragment());
            } else if (id == R.id.nav_logout) {
                signOut();
            } else if (id == R.id.nav_share) {

            } else if (id == R.id.seller_page) {
                loadFragment(new SellerFoodFragment());
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {

        super.onStart();

    }
}
