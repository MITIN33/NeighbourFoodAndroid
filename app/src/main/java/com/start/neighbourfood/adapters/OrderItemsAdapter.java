package com.start.neighbourfood.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.start.neighbourfood.R;
import com.start.neighbourfood.models.v1.response.FoodItem;

import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.OrderListViewHolder> {


    private List<FoodItem> foodItemDetails;

    public OrderItemsAdapter(List<FoodItem> foodItems) {
        foodItemDetails = foodItems;
    }

    @NonNull
    @Override
    public OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_summary_list, viewGroup, false);

        return new OrderListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListViewHolder viewHolder, int i) {
        OrderListViewHolder orderListViewHolder = viewHolder;
        FoodItem foodItemDetail = foodItemDetails.get(i);
        orderListViewHolder.description.setText("2 delicious samosa served with green chatni.");
        orderListViewHolder.itemPrice.setText(String.format("\u20B9 %s", foodItemDetail.getPrice()));
        orderListViewHolder.itemName.setText(foodItemDetail.getItemName());
        orderListViewHolder.quantity.setText(String.format("x%s", foodItemDetail.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return foodItemDetails.size();
    }

    private String getPrice(String price, String quantity) {
        int pr = Integer.parseInt(price);
        int q = Integer.parseInt(quantity);

        return String.valueOf(pr * q);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class OrderListViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView itemName;
        public TextView description;
        public TextView itemPrice;
        public TextView quantity;
        public TextView servedFor;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public OrderListViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            itemName = itemView.findViewById(R.id.food_item_name);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            description = itemView.findViewById(R.id.description);
            quantity = itemView.findViewById(R.id.quantity);
            servedFor = itemView.findViewById(R.id.servedFor);
        }
    }
}

