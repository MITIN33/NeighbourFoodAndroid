package com.start.neighbourfood.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.start.neighbourfood.R;
import com.start.neighbourfood.models.FlatsInfo;

import java.util.List;

public class SellerItemAdapter extends RecyclerView.Adapter<SellerItemAdapter.SellerItemHolder> {
    private List<FlatsInfo> cartList;

    public SellerItemAdapter(List<FlatsInfo> cartList) {
        this.cartList = cartList;
    }

    @Override
    public SellerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_seller_item, parent, false);

        return new SellerItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SellerItemHolder holder, final int position) {
        final FlatsInfo item = cartList.get(position);
        /*holder.name.setText(item.getItemName());
        holder.description.setText(item.getItemName());
        holder.price.setText("₹" + item.getItemID());*/

        /*Glide.with(context)
                .load(item.getThumbnail())
                .into(holder.thumbnail);*/
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void removeItem(int position) {
        cartList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(FlatsInfo item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public class SellerItemHolder extends RecyclerView.ViewHolder {
        public TextView name, description, price;
        public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;

        public SellerItemHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.description);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }
}
