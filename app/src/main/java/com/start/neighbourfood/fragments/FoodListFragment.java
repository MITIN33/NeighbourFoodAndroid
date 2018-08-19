package com.start.neighbourfood.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.start.neighbourfood.Utils.RecyclerTouchListener;
import com.start.neighbourfood.adapters.FoodItemsRecyclerViewAdapter;
import com.start.neighbourfood.R;
import com.start.neighbourfood.models.FoodItemDetails;

import java.util.List;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class FoodListFragment extends Fragment {

    protected List<FoodItemDetails> mDataset;

    public FoodListFragment() {
    }

    @SuppressLint("ValidFragment")
    public FoodListFragment(List<FoodItemDetails> dataSet) {
        mDataset = dataSet;
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
            FoodItemsRecyclerViewAdapter adapter = new FoodItemsRecyclerViewAdapter(mDataset);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
}
