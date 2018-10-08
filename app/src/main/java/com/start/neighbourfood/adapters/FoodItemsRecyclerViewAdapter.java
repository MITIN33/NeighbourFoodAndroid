package com.start.neighbourfood.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.start.neighbourfood.R;
import com.start.neighbourfood.models.FoodItemDetails;

import java.util.ArrayList;
import java.util.List;


public class FoodItemsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "FoodItemsRecyclerViewAdapter";

    private List<FoodItemDetails> mDataSet;
    private ElegantNumberButton.OnValueChangeListener mListener;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     */
    public FoodItemsRecyclerViewAdapter(ElegantNumberButton.OnValueChangeListener listener) {
        // For now, giving the dummy food items
        mDataSet = new ArrayList<>();
        //mContext = context;
        mListener = listener;

    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public FoodItemRowViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        // Create a new view.
        View v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_food_items, viewGroup, false);

        return new FoodItemRowViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FoodItemRowViewHolder rowHolder = (FoodItemRowViewHolder) holder;
        rowHolder.getFoodNameView().setText(mDataSet.get(position).getItemName());
        rowHolder.getServingView().setText(mDataSet.get(position).getItemDesc());
        rowHolder.getPriceView().setText(String.format("\u20B9 %s", mDataSet.get(position).getPrice()));
        rowHolder.getQtyButton().setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                mListener.onValueChange(view, oldValue, newValue);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setmDataSet(List<FoodItemDetails> dataSet) {
        mDataSet = dataSet;
    }

    // END_INCLUDE(recyclerViewOnBindViewHolder)

    public class FoodItemRowViewHolder extends RecyclerView.ViewHolder {

        private final TextView foodNameView;
        private final TextView servingView;
        private final TextView priceView;
        private final ElegantNumberButton qtyButton;
        //private final TextView flatNumber;

        public FoodItemRowViewHolder(View v) {
            super(v);
            foodNameView = v.findViewById(R.id.food_item_name);
            servingView = v.findViewById(R.id.serving_string);
            priceView = v.findViewById(R.id.food_item_price);
            qtyButton = v.findViewById(R.id.quantity_button);
            // set the initial and the final Number for the quantity_button
            Integer minQty = 0;
            Integer maxQty = 10;        // Read this from the DB later
            qtyButton.setRange(minQty, maxQty);
        }

        public TextView getFoodNameView() {
            return foodNameView;
        }

        public TextView getServingView() {
            return servingView;
        }

        public TextView getPriceView() {
            return priceView;
        }

        public ElegantNumberButton getQtyButton() {
            return qtyButton;
        }
    }
}
