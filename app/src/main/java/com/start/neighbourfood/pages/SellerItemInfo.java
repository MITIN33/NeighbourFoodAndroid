package com.start.neighbourfood.pages;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.start.neighbourfood.R;
import com.start.neighbourfood.adapters.SellerItemAdapter;
import com.start.neighbourfood.models.FlatsInfo;
import com.start.neighbourfood.models.RecyclerItemTouchHelper;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SellerItemInfo extends BaseActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final String TAG = "SellerItemInfoActivity";
    private RecyclerView recyclerView;
    private List<FlatsInfo> cartList;
    private SellerItemAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_seller_item_list);

        recyclerView = findViewById(R.id.recycler_view);
        coordinatorLayout = findViewById(R.id.coordinateLayout);
        cartList = new ArrayList<>();
        mAdapter = new SellerItemAdapter(cartList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        prepareCart();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof SellerItemAdapter.SellerItemHolder) {
            // get the removed item name to display it in snack bar
            String name = cartList.get(viewHolder.getAdapterPosition()).getFlatNumber();

            // backup of removed item for undo purpose
            final FlatsInfo deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void prepareCart() {
        showProgressDialog();
        String URL = "http://nfservice.azurewebsites.net/api/fooditem";

        final JsonObjectRequest request = new JsonObjectRequest(URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = "";
                        try {
                            result = response.getJSONArray("Result").toString();
                            if (result == null) {
                                Toast.makeText(getApplicationContext(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            List<com.start.neighbourfood.models.FlatsInfo> items = null;
                            items = new Gson().fromJson(result, new TypeToken<List<FlatsInfo>>() {
                            }.getType());

                            // adding items to cart list
                            cartList.clear();
                            cartList.addAll(items);

                            // refreshing recycler view
                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (Exception ex){
                            ex.printStackTrace();
                        }
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.d(TAG, "Error: " + error.getMessage());
                //Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });

        ServiceManager.getInstance(this).

                addToRequestQueue(request);
    }
}
