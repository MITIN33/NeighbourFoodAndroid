package com.start.neighbourfood.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.NFUtils;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.OrderProgress;
import com.start.neighbourfood.services.Config;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderTrackBuyerActivity extends BaseActivity {

    private TextView timer;
    private long startTime, endTime;
    private Handler handler;
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
    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");


            if (NFUtils.isOrderAcceptanceNotification(message)) {
                setUIForOrderConfirmation();
            } else if (NFUtils.isFoodPrepared(message)) {
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
        handler = new Handler();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
        orderProgress.setOrderStatus(OrderProgress.OrderStatus.COMPLETED);
    }

    private void setUIForOrderConfirmation() {
        orderConfirmImg.setImageResource(R.drawable.green_tick);
        orderConfirmText.setText("Member accepted the food order");
        findViewById(R.id.food_prepared_layout).setVisibility(View.VISIBLE);
        foodPreparedImg.setImageResource(R.drawable.wait);
        preparedFoodText.setText("Food is being prepared");
        orderProgress.setOrderStatus(OrderProgress.OrderStatus.PREPARING);
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
                    orderProgress.setOrderStatus(OrderProgress.OrderStatus.valueOf(result.getJSONObject("Result").getString("orderStatus")));
                    orderProgress.setId(result.getJSONObject("Result").getString("id"));
                    orderProgress.setStartTime(Long.parseLong(result.getJSONObject("Result").getString("createTime")));
                    if (result.getJSONObject("Result").has("endTime")) {
                        orderProgress.setEndTime(Long.parseLong(result.getJSONObject("Result").getString("endTime")));
                    }
                    handler.post(runnable);
                    updateUI();
                } catch (JSONException e) {
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
