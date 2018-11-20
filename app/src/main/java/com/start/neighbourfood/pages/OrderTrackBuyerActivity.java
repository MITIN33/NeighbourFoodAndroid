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
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.NFUtils;
import com.start.neighbourfood.adapters.FoodItemsRecyclerViewAdapter;
import com.start.neighbourfood.adapters.OrderItemsAdapter;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.OrderProgress;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.services.Config;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OrderTrackBuyerActivity extends BaseActivity {

    private TextView timer;
    private long startTime, endTime;
    private Handler handler;
    private OrderProgress orderProgress;
    public Runnable runnable = new Runnable() {

        public void run() {

            int Seconds, Minutes;

            long endTime = orderProgress.getOrderStatus().equals(OrderProgress.OrderStatus.COMPLETED) ? orderProgress.getEndTime() : System.currentTimeMillis();
            long updateTime = endTime - orderProgress.getCreateTime();


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
    private StateProgressBar stateProgressBar;
    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra(ServiceConstants.NOTIFICATION_MESSAGE);


            if (NFUtils.isOrderAcceptanceNotification(message)) {
                orderProgress.setOrderStatus(OrderProgress.OrderStatus.PREPARING);
            }
            else if (NFUtils.isFoodPrepared(message)) {
                orderProgress.setOrderStatus(OrderProgress.OrderStatus.PREPARED);
                orderProgress.setEndTime(System.currentTimeMillis());
            }
            else if (NFUtils.isOrderCollectedNotification(message)) {
                orderProgress.setOrderStatus(OrderProgress.OrderStatus.COMPLETED);
            }
            updateUI(orderProgress.getOrderStatus());
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        handler = new Handler();
        stateProgressBar = findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(NFUtils.getBuyerDataForOrderPlaced());

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

        //came for the firs time on this page
        if (orderProgress == null) {
            orderProgress = new OrderProgress();
            orderProgress.setOrderId(orderId);
        }

        fetchOrderDetail(orderId);
        registerReceiver(mMessageReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    private boolean mStopHandler() {
        return OrderProgress.OrderStatus.COMPLETED.equals(orderProgress.getOrderStatus());
    }


    private void fetchOrderDetail(final String orderID) {
        if (orderID == null) {
            return;
        }
        showProgressDialog();
        ServiceManager.getInstance(this).fetchOrderDetail(orderID, new TaskHandler() {
            @Override
            public void onTaskCompleted(JSONObject request, JSONObject result) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    orderProgress = objectMapper.readValue(result.getJSONObject("Result").toString(), OrderProgress.class);
                    orderProgress.setOrderId(orderID);
                    mRecyclerView.setAdapter(new OrderItemsAdapter(orderProgress.getSellerItems()));
                    ((TextView) findViewById(R.id.orderPlacedTo)).setText(String.format("Order Placed to %s (Flat: %s)", orderProgress.getUserPlacedTo().getfName(), orderProgress.getUserPlacedTo().getFlatNumber()));
                    handler.post(runnable);
                    updateUI(orderProgress.getOrderStatus());
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onErrorResponse(JSONObject request, VolleyError error) {
                hideProgressDialog();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateUI(OrderProgress.OrderStatus orderStatus) {
        switch (orderStatus) {
            case PENDING_CONFIRMATION:
                stateProgressBar.setStateDescriptionData(NFUtils.getBuyerDataForOrderPlaced());
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                break;
            case PREPARING:
                //setUIForOrderConfirmation();
                stateProgressBar.setStateDescriptionData(NFUtils.getDataForOrderConfirmed());
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                break;
            case PREPARED:
                stateProgressBar.setStateDescriptionData(NFUtils.getBuyerDataForFoodPrepared());
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                break;
            case COMPLETED:
                stateProgressBar.setStateDescriptionData(NFUtils.getDataForCollected());
                stateProgressBar.setAllStatesCompleted(true);
                break;
        }
    }
}
