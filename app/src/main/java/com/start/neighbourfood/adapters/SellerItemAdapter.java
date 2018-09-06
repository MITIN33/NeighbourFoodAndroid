package com.start.neighbourfood.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.start.neighbourfood.R;
import com.start.neighbourfood.models.FoodItemDetails;

import java.util.ArrayList;
import java.util.List;

public class SellerItemAdapter extends RecyclerView.Adapter<SellerItemAdapter.SellerItemHolder> {
    private List<FoodItemDetails> mDataset;
    private View.OnClickListener mListener;

    public SellerItemAdapter(View.OnClickListener listener) {
        this.mDataset = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public SellerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_seller_item, parent, false);

        return new SellerItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SellerItemHolder holder, final int position) {
        final FoodItemDetails item = mDataset.get(position);
        holder.name.setText(item.getItemName());
        holder.description.setText(item.getItemName());
        holder.price.setText(String.format("₹%s", item.getPrice()));
        holder.editButton.setOnClickListener(mListener);
        holder.servedFor.setText(String.format("Served For %s", item.getServedFor()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setDataset(List<FoodItemDetails> sellerItemInfos) {
        mDataset = sellerItemInfos;
    }

    public void removeItem(int position) {
        mDataset.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(FoodItemDetails item, int position) {
        mDataset.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public class SellerItemHolder extends RecyclerView.ViewHolder {
        public TextView name, description, price, servedFor;
        public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;
        public Button editButton;

        public SellerItemHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.description);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
            editButton = view.findViewById(R.id.editButton);
            servedFor = view.findViewById(R.id.servedForText);
        }
    }
}
