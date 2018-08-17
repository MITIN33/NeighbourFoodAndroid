package com.start.neighbourfood.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.neighbourfood.start.neighbourfood.R;

import org.w3c.dom.Text;


public class FoodItemRowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView foodNameView;
    private final TextView servingView;
    private final TextView priceView;
    private final ElegantNumberButton qtyButton;
    private RecyclerViewClickListener mListener;

    public FoodItemRowViewHolder(View v, RecyclerViewClickListener listener) {
        super(v);
        mListener = listener;
        v.setOnClickListener(this);
        foodNameView = (TextView) v.findViewById(R.id.food_item_name);
        servingView = (TextView) v.findViewById(R.id.serving_string);
        priceView = (TextView) v.findViewById(R.id.food_item_price);
        qtyButton = (ElegantNumberButton) v.findViewById(R.id.quantity_button);
        qtyButton.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantitySelected = qtyButton.getNumber();
            }
        });

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

    @Override
    public void onClick(View view) {
        mListener.onClick(view, getAdapterPosition());
    }
}
