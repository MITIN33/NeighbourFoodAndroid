package com.start.neighbourfood.pages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.start.neighbourfood.R;
import com.start.neighbourfood.adapters.OrderItemsAdapter;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.FoodItemDetails;
import com.start.neighbourfood.models.OrderDetail;
import com.start.neighbourfood.models.UserBaseInfo;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderSummaryActivity extends BaseActivity {

    private List<FoodItemDetails> foodItemDetailsList;
    private Button placeOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.ordered_Item_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        String list = getFromSharedPreference("orderedItem");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            foodItemDetailsList = objectMapper.readValue(list, new TypeReference<List<FoodItemDetails>>() {
            });
            OrderItemsAdapter adapter = new OrderItemsAdapter(foodItemDetailsList);
            mRecyclerView.setAdapter(adapter);
            ((TextView) findViewById(R.id.total_bill)).setText("\u20B9 " + getTotalPrice());
        } catch (IOException e) {
            e.printStackTrace();
        }

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder();
            }
        });

    }

    private void placeOrder() {
        Date date = new Date();
        showProgressDialog();
        long epochTime = date.getTime();
        try {
            UserBaseInfo user = getUserBaseInfo();
            String orderID = String.format("ORD%s%s", epochTime, user.getUserUid().substring(0, 4)).toUpperCase();
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setCreateTime(String.valueOf(epochTime));
            List<FoodItemDetails> list = new ArrayList<>();
            for (FoodItemDetails item : foodItemDetailsList) {
                list.add(item);
            }
            orderDetail.setSellerItemId(list);
            orderDetail.setOrderId(orderID);
            orderDetail.setUserPlacedBy(user.getUserUid());
            orderDetail.setOrderStatus("Confirmation");
            orderDetail.setUserPlacedTo(foodItemDetailsList.get(0).getSellerID());

            JSONObject jsonObject = new JSONObject(new Gson().toJson(orderDetail).toString());

            ServiceManager.getInstance(this).placeOrder(jsonObject, new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject result) {
                    Intent i = new Intent(getApplicationContext(), OrderTrackActivity.class);
                    startActivity(i);
                    finish();
                    hideProgressDialog();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(OrderSummaryActivity.this, "Failed to place order. Try Again !", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getTotalPrice() {
        int sum = 0;
        for (FoodItemDetails itemDetails : foodItemDetailsList) {
            int pr = Integer.parseInt(itemDetails.getPrice());
            int q = Integer.parseInt(itemDetails.getQuantity());
            sum += pr * q;
        }

        return String.valueOf(sum);
    }

}
