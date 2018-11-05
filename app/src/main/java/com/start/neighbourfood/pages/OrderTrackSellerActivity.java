package com.start.neighbourfood.pages;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.NFUtils;
import com.start.neighbourfood.Utils.NotificationUtils;
import com.start.neighbourfood.adapters.OrderItemsAdapter;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.OrderProgress;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OrderTrackSellerActivity extends BaseActivity {


    private TextView timer;
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
    private ImageView orderConfirmImg, foodPreparedImg;
    private TextView preparedFoodText, orderConfirmText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String buyerTokenId;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_ordertrack);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Customer code
        timer = findViewById(R.id.seller_timer);
        orderConfirmImg = findViewById(R.id.confirm_img);
        foodPreparedImg = findViewById(R.id.foodPrepared_img);
        preparedFoodText = findViewById(R.id.foodPrepared_txt);
        orderConfirmText = findViewById(R.id.confirm_text);
        handler = new Handler();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.ordered_seller_Item_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mSwipeRefreshLayout = findViewById(R.id.seller_swipe_layout);

        String orderId = getIntent().getExtras().getString("orderID");

        //came for the firs time on this page
        if (orderProgress == null) {
            orderProgress = new OrderProgress();
            orderProgress.setOrderId(orderId);
        }
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

        orderConfirmImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequestAcceptNotification();
            }
        });

        foodPreparedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFoodPreparedNotification();
            }
        });

    }

    private void setUIForFoodPrepared() {
        setUIForOrderConfirmation();
        findViewById(R.id.finish_layout).setVisibility(View.VISIBLE);
        foodPreparedImg.setImageResource(R.drawable.green_tick);
        preparedFoodText.setText("Food prepared");
    }

    private void setUIForOrderConfirmation() {
        orderConfirmImg.setImageResource(R.drawable.green_tick);
        findViewById(R.id.finish_layout).setVisibility(View.INVISIBLE);
        orderConfirmText.setText("You accepted the order.");
        findViewById(R.id.food_prepared_layout).setVisibility(View.VISIBLE);
        foodPreparedImg.setImageResource(R.drawable.wait);
        preparedFoodText.setText("Tap to confirm if the food is ready.");
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
            public void onTaskCompleted(JSONObject result) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    orderProgress = objectMapper.readValue(result.getJSONObject("Result").toString(), OrderProgress.class);
                    orderProgress.setOrderId(orderID);
                    ((TextView)findViewById(R.id.seller_track_buyerName)).setText(orderProgress.getUserPlacedBy().getfName());
                    ((TextView)findViewById(R.id.seller_track_buyerFlatName)).setText(orderProgress.getUserPlacedBy().getFlatNumber());


                    mRecyclerView.setAdapter(new OrderItemsAdapter(orderProgress.getSellerItems()));
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

    public void sendRequestAcceptNotification() {
        showProgressDialog();
        if (buyerTokenId == null) {
            ServiceManager.getInstance(getApplicationContext()).getUserNotification(orderProgress.getUserPlacedBy().getUserUid(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject result) {
                    try {
                        buyerTokenId = result.getJSONObject("Result").getString("data");

                        ServiceManager.getInstance(OrderTrackSellerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.PREPARING.toString(), new TaskHandler() {
                            @Override
                            public void onTaskCompleted(JSONObject result) {
                                //NotificationUtils.sendNotificationTo(OrderTrackSellerActivity.this, buyerTokenId, NFUtils.constructOrderAcceptedMessage());
                                orderProgress.setOrderStatus(OrderProgress.OrderStatus.PREPARING);
                                setUIForOrderConfirmation();
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hideProgressDialog();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                }
            });
        } else {
            ServiceManager.getInstance(OrderTrackSellerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.PREPARING.toString(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject result) {
                    NotificationUtils.sendNotificationTo(OrderTrackSellerActivity.this, buyerTokenId, NFUtils.constructOrderAcceptedMessage());
                    orderProgress.setOrderStatus(OrderProgress.OrderStatus.PREPARING);
                    setUIForOrderConfirmation();
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }


    public void sendFoodPreparedNotification() {
        showProgressDialog();
        if (buyerTokenId == null) {
            ServiceManager.getInstance(getApplicationContext()).getUserNotification(orderProgress.getUserPlacedBy().getUserUid(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject result) {
                    try {
                        buyerTokenId = result.getJSONObject("Result").getString("data");
                        ServiceManager.getInstance(OrderTrackSellerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.COMPLETED.toString(), new TaskHandler() {
                            @Override
                            public void onTaskCompleted(JSONObject result) {
                                NotificationUtils.sendNotificationTo(OrderTrackSellerActivity.this, buyerTokenId, NFUtils.constructFoodPreparedMessage());
                                orderProgress.setOrderStatus(OrderProgress.OrderStatus.COMPLETED);
                                orderProgress.setEndTime(System.currentTimeMillis());
                                setUIForFoodPrepared();
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hideProgressDialog();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                }
            });
        } else {
            ServiceManager.getInstance(OrderTrackSellerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.COMPLETED.toString(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject result) {
                    NotificationUtils.sendNotificationTo(OrderTrackSellerActivity.this, buyerTokenId, NFUtils.constructFoodPreparedMessage());
                    orderProgress.setOrderStatus(OrderProgress.OrderStatus.COMPLETED);
                    orderProgress.setEndTime(System.currentTimeMillis());
                    setUIForFoodPrepared();
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            hideProgressDialog();
        }
    }

}
