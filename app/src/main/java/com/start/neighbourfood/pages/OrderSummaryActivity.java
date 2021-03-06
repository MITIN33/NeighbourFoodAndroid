package com.start.neighbourfood.pages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.start.neighbourfood.NFApplication;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.NFUtils;
import com.start.neighbourfood.adapters.OrderItemsAdapter;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.NfMessageNotification;
import com.start.neighbourfood.models.OrderDetail;
import com.start.neighbourfood.models.OrderProgress;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.models.v1.UserBaseInfo;
import com.start.neighbourfood.models.v1.response.SellerItemDetail;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

public class OrderSummaryActivity extends BaseActivity {

    private SellerItemDetail sellerItemDetail;
    private UserBaseInfo user;
    private String sellerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Button placeOrderBtn = findViewById(R.id.placeOrderBtn);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        user = NFApplication.getSharedPreferenceUtils().getUserBaseInfo();
        ((TextView)findViewById(R.id.flat_number)).setText(NFApplication.getSharedPreferenceUtils().getStringValue("flatNumber",null));
        RecyclerView mRecyclerView = findViewById(R.id.ordered_Item_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        String list = NFApplication.getSharedPreferenceUtils().getStringValue("orderedItem", null);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            sellerItemDetail = objectMapper.readValue(list, SellerItemDetail.class);
            sellerID = sellerItemDetail.getSellerId();
            OrderItemsAdapter adapter = new OrderItemsAdapter(sellerItemDetail.getFoodItemDetail());
            mRecyclerView.setAdapter(adapter);
            ((TextView)findViewById(R.id.userName)).setText(sellerItemDetail.getfName());
            ((TextView)findViewById(R.id.flat_number)).setText("Flat #" +sellerItemDetail.getFlatNumber());
            ((ImageView)findViewById(R.id.hoodIcon)).setImageResource(R.drawable.food_icon);
            ((TextView) findViewById(R.id.total_bill)).setText(String.format("₹ %s", NFUtils.getTotalPrice(sellerItemDetail.getFoodItemDetail())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder();
                sendNotification(sellerID);
            }
        });

    }

    private void sendNotification(String sellerID) {
        if (sellerID == null)
            return;
        ServiceManager.getInstance(getApplicationContext()).getUserNotification(sellerID, new TaskHandler() {
            @Override
            public void onTaskCompleted(JSONObject request, JSONObject result) {
                try {
                    String targetToken = result.getJSONObject("Result").getString("data");
                    sendNotificationTo(targetToken, NFUtils.sendNotificationToSeller(user.getfName(), user.getFlatNumber()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(JSONObject request, VolleyError error) {

            }
        });
    }

    private void sendNotificationTo(String targetToken, String message) {
        NfMessageNotification messageNotification = new NfMessageNotification(targetToken, message);
        ServiceManager.getInstance(this).sendNotification(messageNotification);
    }

    private void placeOrder() {
        Date date = new Date();
        showProgressDialog();
        long epochTime = date.getTime();
        try {
            final String orderID = NFUtils.getOrderID(epochTime , user.getUserUid());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setCreateTime(String.valueOf(epochTime));
            orderDetail.setFoodItems(sellerItemDetail.getFoodItemDetail());
            orderDetail.setOrderId(orderID);
            orderDetail.setUserPlacedBy(user.getUserUid());
            orderDetail.setOrderStatus(OrderProgress.OrderStatus.PENDING_CONFIRMATION.toString());
            orderDetail.setUserPlacedTo(sellerItemDetail.getSellerId());

            JSONObject jsonObject = new JSONObject(new Gson().toJson(orderDetail));

            ServiceManager.getInstance(this).placeOrder(jsonObject, new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject request, JSONObject result) {
                    Intent i = new Intent(getApplicationContext(), OrderTrackBuyerActivity.class);
                    i.putExtra(ServiceConstants.ORDER_ID,orderID);
                    startActivity(i);
                    finish();
                    hideProgressDialog();
                }

                @Override
                public void onErrorResponse(JSONObject request, VolleyError error) {
                    Toast.makeText(OrderSummaryActivity.this, "Failed to place order. Try Again !", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
