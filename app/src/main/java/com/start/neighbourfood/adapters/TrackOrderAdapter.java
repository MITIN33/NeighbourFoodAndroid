package com.start.neighbourfood.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.start.neighbourfood.R;
import com.start.neighbourfood.response.ResponseOrderHistory;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackOrderAdapter extends RecyclerView.Adapter<TrackOrderAdapter.TrackOrderItemHolder>{

    private List<ResponseOrderHistory> dataset;
    public TrackOrderAdapter(List<ResponseOrderHistory> list){
        dataset = list;
    }

    @NonNull
    @Override
    public TrackOrderItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_track_item, viewGroup , false);

        return new TrackOrderItemHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull TrackOrderItemHolder trackOrderItemHolder, int i) {
        ResponseOrderHistory orderDetail = dataset.get(i);
        java.text.SimpleDateFormat simpleDateFormat1 = new java.text.SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        java.text.SimpleDateFormat simpleDateFormat2 = new java.text.SimpleDateFormat("hh:mm a", Locale.getDefault());
        trackOrderItemHolder.orderStatus.setText(orderDetail.getOrderStatus());
        trackOrderItemHolder.dateTime.setText(String.valueOf(simpleDateFormat1.format(new Date(Long.parseLong(orderDetail.getCreateTime())))));
        trackOrderItemHolder.flatName.setText(String.format("(%s)", orderDetail.getFlatNumber()));
        trackOrderItemHolder.userName.setText(orderDetail.getSellerName());
        trackOrderItemHolder.bill.setText(String.format("₹ %s", orderDetail.getTotalBill()));
        trackOrderItemHolder.orderType.setText(String.format("ORDER: %s", orderDetail.getOrderType()));
        trackOrderItemHolder.txtTime.setText(simpleDateFormat2.format(new Date(Long.parseLong(orderDetail.getCreateTime()))));
        if (orderDetail.getOrderType().equals("RECEIVED")){
            trackOrderItemHolder.orderType.setTextColor(Color.MAGENTA);
        }
        else    {
            trackOrderItemHolder.orderType.setTextColor(Color.BLUE);
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class TrackOrderItemHolder extends RecyclerView.ViewHolder {
        public TextView userName, flatName, bill, orderStatus, dateTime, orderType, txtTime;

        public TrackOrderItemHolder(View view) {
            super(view);
            userName= view.findViewById(R.id.txtUserName);
            flatName = view.findViewById(R.id.txtFlatNumber);
            bill = view.findViewById(R.id.txtBill);
            dateTime= view.findViewById(R.id.txtDate);
            orderStatus = view.findViewById(R.id.txtStatus2);
            orderType = view.findViewById(R.id.orderType);
            txtTime= view.findViewById(R.id.txtTime);
        }
    }



}
