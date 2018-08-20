package com.start.neighbourfood.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.RecyclerTouchListener;
import com.start.neighbourfood.adapters.FoodItemsRecyclerViewAdapter;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.FoodItemDetails;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class FoodListFragment extends Fragment implements TaskHandler {

    protected List<FoodItemDetails> mDataset;
    private FoodItemsRecyclerViewAdapter mAdapter;

    public FoodListFragment() {
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
            View rootView = inflater.inflate(R.layout.content_food_list, container, false);
            RecyclerView mRecyclerView = rootView.findViewById(R.id.detail_food_items_recycleView);
            mAdapter = new FoodItemsRecyclerViewAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
            loadFoodItems();
            //Set On Click listner
            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    FoodItemDetails flats = mDataset.get(position);
                    Toast.makeText(getContext(), flats.getItemName() + " is selected!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
            return rootView;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void loadFoodItems() {

        String sellerId = getArguments().getString("sellerId");
        ServiceManager.getInstance(getActivity()).fetchFoodItemsForFlat(sellerId, this);
    }

    @Override
    public void onTaskCompleted(JSONObject result) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<FoodItemDetails> foodItemDetails = objectMapper.readValue(result.getJSONArray("Result").toString(), new TypeReference<List<FoodItemDetails>>() {
            });
            mDataset = foodItemDetails;
            mAdapter.setmDataSet(foodItemDetails);
            mAdapter.notifyDataSetChanged();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }
}
