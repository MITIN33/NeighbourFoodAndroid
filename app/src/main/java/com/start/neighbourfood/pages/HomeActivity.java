package com.start.neighbourfood.pages;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.start.neighbourfood.R;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.fragments.FlatsInfoFragment;
import com.start.neighbourfood.models.FlatsInfo;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "HOME_ACTIVITY";
    private List<String> lastSearches;

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

        //REFERENCE MATERIALSEARCHBAR AND LISTVIEW
        ListView lv= (ListView) findViewById(R.id.mListView);

        MaterialSearchBar searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        searchBar.setHint("Search for the dish name ...");
        searchBar.setSpeechMode(true);
       /* //enable searchbar callbacks
        searchBar.setOnSearchActionListener(this);
        //restore last queries from disk
        lastSearches = loadSearchSuggestionFromDisk();
        searchBar.setLastSuggestions(list);
        //Inflate menu and setup OnMenuItemClickListener
        searchBar.inflateMenu(R.menu.main);
        searchBar.getMenu().setOnMenuItemClickListener(this);*/


        List<String> food_items=new ArrayList<>();
       /* food_items.add("Burger");
        food_items.add("Pizza");
        food_items.add("PavBhaji");
        food_items.add("PapdiChaat");
        food_items.add("CholeBhature");*/


        //ADAPTER
        final ArrayAdapter adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,food_items);
        lv.setAdapter(adapter);

        //SEARCHBAR TEXT CHANGE LISTENER
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //SEARCH FILTER
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //LISTVIEW ITEM CLICKED
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(HomeActivity.this, adapter.getItem(i).toString(), Toast.LENGTH_SHORT).show();
            }
        });


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
                FlatsInfoFragment fragment = new FlatsInfoFragment(flatsInfos);
                transaction.replace(R.id.flat_content_fragment, fragment);
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
