package com.start.neighbourfood.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.RecyclerTouchListener;
import com.start.neighbourfood.adapters.SellerItemAdapter;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.FoodItemDetails;
import com.start.neighbourfood.models.RecyclerItemTouchHelper;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class SellerFoodFragment extends BaseFragment implements TaskHandler, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    protected List<FoodItemDetails> mDataset;
    protected SellerItemAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;

    public SellerFoodFragment(){}

    @SuppressLint("ValidFragment")
    public SellerFoodFragment(List<FoodItemDetails> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        try {
            super.onCreateView(inflater,container,savedInstanceState);
            View rootView = inflater.inflate(R.layout.content_seller_item_list, container, false);
            RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view);
            coordinatorLayout = (CoordinatorLayout) rootView;
            mAdapter = new SellerItemAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    loadFoodItems();
                }
            });
            //Set On Click listner
            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {

                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

            return rootView;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private void loadFoodItems() {
        showProgressDialog();
        String sellerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ServiceManager.getInstance(getActivity()).fetchFoodItemsForFlat(sellerId, this);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof SellerItemAdapter.SellerItemHolder) {
            // get the removed item name to display it in snack bar
            String name = mDataset.get(viewHolder.getAdapterPosition()).getSellerID();

            // backup of removed item for undo purpose
            final FoodItemDetails deletedItem = mDataset.get(viewHolder.getAdapterPosition());
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

    @Override
    public void onTaskCompleted(JSONObject result) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<FoodItemDetails> foodItemDetails = objectMapper.readValue(result.getJSONArray("Result").toString(), new TypeReference<List<FoodItemDetails>>() {
            });
            mDataset = foodItemDetails;
            mAdapter.setDataset(foodItemDetails);
            mAdapter.notifyDataSetChanged();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        hideProgressDialog();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }
}
