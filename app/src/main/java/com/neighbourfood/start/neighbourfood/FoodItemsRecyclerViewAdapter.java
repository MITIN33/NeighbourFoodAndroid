package com.neighbourfood.start.neighbourfood;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.neighbourfood.start.neighbourfood.fragments.RecyclerViewClickListener;
import com.neighbourfood.start.neighbourfood.fragments.RowViewHolder;
import com.start.neighbourfood.pages.ApartmentsActivity;


public class FoodItemsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private String[] mDataSet;
    private RecyclerViewClickListener mListener;
    //private Context mContext;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public FoodItemsRecyclerViewAdapter(RecyclerViewClickListener listener ,String[] dataSet) {
        // For now, giving the dummy food items
        dataSet = new String[]{"Flat #101","Flat #102","Flat #103"};
        mDataSet = dataSet;
        //mContext = context;
        mListener = listener;

    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        // Create a new view.
        View v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_food_items, viewGroup, false);

        return new RowViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RowViewHolder) {
            RowViewHolder rowHolder = (RowViewHolder) holder;
            //set values of data here
            // Get element from your dataset at this position and replace the contents of the view
            // with that element
            rowHolder.getTextView().setText(mDataSet[position]);
            rowHolder.getImageView().setImageResource(R.drawable.food_icon);
            rowHolder.getListView().setText("Aaloo Parantha, Gobi Parantha, Pav Bhaji");
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

}
