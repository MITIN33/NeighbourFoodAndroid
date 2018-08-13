package com.neighbourfood.start.neighbourfood.fragments;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.neighbourfood.start.neighbourfood.R;


public class RowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private RecyclerViewClickListener mListener;
    private final TextView textView;
    private final ImageView imageView;
    private final TextView foodListView;

     public RowViewHolder(View v, RecyclerViewClickListener listener) {
        super(v);
        mListener = listener;
        v.setOnClickListener(this);
        textView = (TextView) v.findViewById(R.id.flat_name);
        imageView = (ImageView) v.findViewById(R.id.food_image);
        foodListView = (TextView) v.findViewById(R.id.food_items_list);
    }

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getListView() {
        return foodListView;
    }

    @Override
    public void onClick(View view) {
        mListener.onClick(view, getAdapterPosition());
    }


}
