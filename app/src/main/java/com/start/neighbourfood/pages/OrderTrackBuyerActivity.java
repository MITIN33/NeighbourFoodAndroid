package com.start.neighbourfood.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.NFUtils;
import com.start.neighbourfood.adapters.FoodItemsRecyclerViewAdapter;
import com.start.neighbourfood.adapters.OrderItemsAdapter;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.FoodItemDetails;
import com.start.neighbourfood.models.OrderProgress;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.services.Config;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class OrderTrackBuyerActivity extends BaseActivity {

    private TextView timer;
    private long startTime, endTime;
    private Handler handler;
    private String flatNumber;
    private OrderProgress orderProgress;
    private ImageView orderConfirmImg, foodPreparedImg;
    private TextView preparedFoodText, orderConfirmText;
    public Runnable runnable = new Runnable() {

        public void run() {

            int Seconds, Minutes;

            long endTime = orderProgress.getOrderStatus().equals(OrderProgress.OrderStatus.COMPLETED) ? orderProgress.getEndTime() : System.currentTimeMillis();
            long updateTime = endTime - orderProgress.getStartTime();


            Seconds = (int) (updateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            timer.setText(String.format("%02d:%02d", Minutes, Seconds));

            if (!mStopHandler()) {
                handler.postDelayed(this, 1000);
            }
        }

    };
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FoodItemsRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView finalMsg;
    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");


            if (NFUtils.isOrderAcceptanceNotification(message)) {
                orderProgress.setOrderStatus(OrderProgress.OrderStatus.PREPARING);
                setUIForOrderConfirmation();
            } else if (NFUtils.isFoodPrepared(message)) {
                orderProgress.setOrderStatus(OrderProgress.OrderStatus.COMPLETED);
                orderProgress.setEndTime(System.currentTimeMillis());
                setUIForFoodPrepared();
            }
            //do other stuff here
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_track);
        Toolbar toolbar = findViewById(R.id.toolbar);
        timer = findViewById(R.id.timer);
        setSupportActionBar(toolbar);
        orderConfirmImg = findViewById(R.id.confirm_img);
        foodPreparedImg = findViewById(R.id.foodPrepared_img);
        preparedFoodText = findViewById(R.id.foodPrepared_txt);
        orderConfirmText = findViewById(R.id.confirm_text);
        finalMsg = findViewById(R.id.final_msg_textView);
        handler = new Handler();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.ordered_buyer_Item_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mSwipeRefreshLayout = findViewById(R.id.order_buyer_swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchOrderDetail(orderProgress.getOrderId());
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                fetchOrderDetail(orderProgress.getOrderId());
            }
        });

        //Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        String orderId = getIntent().getExtras().getString("orderID");
        flatNumber = getFromSharedPreference("flatNumber");
        if (flatNumber == null){
            flatNumber = getIntent().getExtras().getString(ServiceConstants.orderFlatNumberLabel);
        }

        //came for the firs time on this page
        if (orderProgress == null) {
            orderProgress = new OrderProgress();
            orderProgress.setOrderId(orderId);
        }

        fetchOrderDetail(orderId);
        registerReceiver(mMessageReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    private void setUIForFoodPrepared() {
        setUIForOrderConfirmation();
        findViewById(R.id.finish_layout).setVisibility(View.VISIBLE);
        foodPreparedImg.setImageResource(R.drawable.green_tick);
        preparedFoodText.setText("Food prepared");
    }

    private void setUIForOrderConfirmation() {
        orderConfirmImg.setImageResource(R.drawable.green_tick);
        orderConfirmText.setText("Member accepted the food order");
        findViewById(R.id.food_prepared_layout).setVisibility(View.VISIBLE);
        foodPreparedImg.setImageResource(R.drawable.wait);
        finalMsg.setText("Go get your food from flat #"+flatNumber);
        preparedFoodText.setText("Food is being prepared");
    }

    private boolean mStopHandler() {
        return OrderProgress.OrderStatus.COMPLETED.equals(orderProgress.getOrderStatus());
    }


    private void fetchOrderDetail(String orderID) {
        if (orderID == null) {
            return;
        }
        showProgressDialog();
        ServiceManager.getInstance(this).fetchOrderDetail(orderID, new TaskHandler() {
            @Override
            public void onTaskCompleted(JSONObject result) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    orderProgress.setOrderStatus(OrderProgress.OrderStatus.valueOf(result.getJSONObject("Result").getString("orderStatus")));
                    orderProgress.setId(result.getJSONObject("Result").getString("id"));
                    orderProgress.setStartTime(Long.parseLong(result.getJSONObject("Result").getString("createTime")));
                    if (result.getJSONObject("Result").has("endTime")) {
                        orderProgress.setEndTime(Long.parseLong(result.getJSONObject("Result").getString("endTime")));
                    }
                    List<FoodItemDetails> foodItemDetails = objectMapper.readValue(result.getJSONObject("Result").getJSONArray("sellerItems").toString(), new TypeReference<List<FoodItemDetails>>() {
                    });
                    mRecyclerView.setAdapter(new OrderItemsAdapter(foodItemDetails));
                    handler.post(runnable);
                    updateUI();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateUI() {
        switch (orderProgress.getOrderStatus()) {
            case PENDING_CONFIRMATION:
                break;
            case PREPARING:
                setUIForOrderConfirmation();
                break;
            case COMPLETED:
                setUIForFoodPrepared();
                break;
        }
    }
}
