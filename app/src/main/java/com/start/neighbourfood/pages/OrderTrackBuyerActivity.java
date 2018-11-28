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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.NFUtils;
import com.start.neighbourfood.Utils.NotificationUtils;
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
            if (orderProgress!=null &&  orderProgress.getOrderStatus() == OrderProgress.OrderStatus.PENDING_CONFIRMATION){
                timer.setText("CANCEL ORDER");
            }

            if (orderProgress!=null &&  orderProgress.getOrderStatus() == OrderProgress.OrderStatus.CANCELLED){
                timer.setText("Cancelled");
            }
            long endTime = orderProgress.getOrderStatus().equals(OrderProgress.OrderStatus.COMPLETED) ? orderProgress.getEndTime() : System.currentTimeMillis();
            long updateTime = endTime - orderProgress.getCreateTime();


            Seconds = (int) (updateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            timer.setText(String.format("%02d:%02d", Minutes, Seconds));
            if (orderProgress!=null &&  orderProgress.getOrderStatus() == OrderProgress.OrderStatus.PENDING_CONFIRMATION){
                timer.setText("CANCEL ORDER");
            }

            if (orderProgress!=null &&  orderProgress.getOrderStatus() == OrderProgress.OrderStatus.CANCELLED){
                timer.setText("Cancelled");
            }
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
    private String buyerTokenId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.track, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_help:
                navigateToActivity(HelpActivity.class);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_track);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        timer = findViewById(R.id.timer);
        handler = new Handler();
        stateProgressBar = findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(NFUtils.getBuyerDataForOrderPlaced());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.ordered_buyer_Item_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderProgress != null && orderProgress.getOrderStatus() == OrderProgress.OrderStatus.PENDING_CONFIRMATION){
                    updateCancelRequest();
                }
            }
        });

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
            case CANCELLED:
                stateProgressBar.setStateDescriptionData(NFUtils.getSellerDataForCancelled());
                stateProgressBar.setAllStatesCompleted(true);
                timer.setText("--:--");
                break;
        }
    }

    public void updateCancelRequest() {
        showProgressDialog();
        if (buyerTokenId == null) {
            ServiceManager.getInstance(getApplicationContext()).getUserNotification(orderProgress.getUserPlacedBy().getUserUid(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject request, JSONObject result) {
                    try {
                        buyerTokenId = result.getJSONObject("Result").getString("data");

                        ServiceManager.getInstance(OrderTrackBuyerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.CANCELLED.toString(), new TaskHandler() {
                            @Override
                            public void onTaskCompleted(JSONObject request, JSONObject result) {
                                NotificationUtils.sendNotificationTo(OrderTrackBuyerActivity.this, buyerTokenId, NFUtils.constrctOrderCancelled(orderProgress.getOrderId()));
                                orderProgress.setOrderStatus(OrderProgress.OrderStatus.CANCELLED);
                                updateUI(orderProgress.getOrderStatus());
                            }

                            @Override
                            public void onErrorResponse(JSONObject request, VolleyError error) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hideProgressDialog();
                }

                @Override
                public void onErrorResponse(JSONObject request, VolleyError error) {
                    hideProgressDialog();
                }
            });
        } else {
            ServiceManager.getInstance(OrderTrackBuyerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.CANCELLED.toString(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject request, JSONObject result) {
                    NotificationUtils.sendNotificationTo(OrderTrackBuyerActivity.this, buyerTokenId, NFUtils.constrctOrderCancelled(orderProgress.getOrderId()));
                    orderProgress.setOrderStatus(OrderProgress.OrderStatus.CANCELLED);
                    updateUI(orderProgress.getOrderStatus());
                }

                @Override
                public void onErrorResponse(JSONObject request, VolleyError error) {

                }
            });
        }
    }
}
