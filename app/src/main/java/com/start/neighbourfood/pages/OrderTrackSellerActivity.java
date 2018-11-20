package com.start.neighbourfood.pages;

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
import com.kofigyan.stateprogressbar.components.StateItem;
import com.kofigyan.stateprogressbar.listeners.OnStateItemClickListener;
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
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String buyerTokenId;
    private RecyclerView mRecyclerView;
    private StateProgressBar stateProgressBar;

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
        timer = findViewById(R.id.seller_timer);
        handler = new Handler();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        stateProgressBar = findViewById(R.id.seller_progress_bar_id);
        stateProgressBar.setOnStateItemClickListener(new OnStateItemClickListener() {
            @Override
            public void onStateItemClick(StateProgressBar stateProgressBar, StateItem stateItem, int stateNumber, boolean isCurrentState) {
                if (orderProgress.getOrderStatus() == OrderProgress.OrderStatus.COMPLETED){
                    return;
                }
                switch (stateNumber){
                    case 1:
                        updateRequestAccepted();
                        break;
                    case 2:
                        updateFoodPrepared();
                        break;
                    case 3:
                        updateFoodCollected();
                        break;
                }
            }
        });
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
                    ((TextView) findViewById(R.id.orderSellerPlacedTo)).setText(String.format("Order Placed by %s (Flat: %s)", orderProgress.getUserPlacedBy().getfName(), orderProgress.getUserPlacedBy().getFlatNumber()));
                    mRecyclerView.setAdapter(new OrderItemsAdapter(orderProgress.getSellerItems()));
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
                stateProgressBar.setStateDescriptionData(NFUtils.getSellerDataForOrderPlaced());
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                stateProgressBar.setAllStatesCompleted(false);
                break;
            case PREPARING:
                stateProgressBar.setStateDescriptionData(NFUtils.getSellerDataForFoodPrepared());
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                stateProgressBar.setAllStatesCompleted(false);
                break;
            case PREPARED:
                stateProgressBar.setStateDescriptionData(NFUtils.getSellerDataForFoodCollect());
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                stateProgressBar.setAllStatesCompleted(false);
                break;
            case COMPLETED:
                stateProgressBar.setStateDescriptionData(NFUtils.getDataForCollected());
                stateProgressBar.setAllStatesCompleted(true);
                break;
        }
    }

    public void updateRequestAccepted() {
        showProgressDialog();
        if (buyerTokenId == null) {
            ServiceManager.getInstance(getApplicationContext()).getUserNotification(orderProgress.getUserPlacedBy().getUserUid(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject request, JSONObject result) {
                    try {
                        buyerTokenId = result.getJSONObject("Result").getString("data");

                        ServiceManager.getInstance(OrderTrackSellerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.PREPARING.toString(), new TaskHandler() {
                            @Override
                            public void onTaskCompleted(JSONObject request, JSONObject result) {
                                //NotificationUtils.sendNotificationTo(OrderTrackSellerActivity.this, buyerTokenId, NFUtils.constructOrderAcceptedMessage());
                                orderProgress.setOrderStatus(OrderProgress.OrderStatus.PREPARING);
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
            ServiceManager.getInstance(OrderTrackSellerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.PREPARING.toString(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject request, JSONObject result) {
                    NotificationUtils.sendNotificationTo(OrderTrackSellerActivity.this, buyerTokenId, NFUtils.constructOrderAcceptedMessage(orderProgress.getOrderId()));
                    orderProgress.setOrderStatus(OrderProgress.OrderStatus.PREPARING);
                    updateUI(orderProgress.getOrderStatus());
                }

                @Override
                public void onErrorResponse(JSONObject request, VolleyError error) {

                }
            });
        }
    }


    public void updateFoodPrepared() {
        showProgressDialog();
        if (buyerTokenId == null) {
            ServiceManager.getInstance(getApplicationContext()).getUserNotification(orderProgress.getUserPlacedBy().getUserUid(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject request, JSONObject result) {
                    try {
                        buyerTokenId = result.getJSONObject("Result").getString("data");
                        ServiceManager.getInstance(OrderTrackSellerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.PREPARED.toString(), new TaskHandler() {
                            @Override
                            public void onTaskCompleted(JSONObject request, JSONObject result) {
                                NotificationUtils.sendNotificationTo(OrderTrackSellerActivity.this, buyerTokenId, NFUtils.constructFoodPreparedMessage(orderProgress.getUserPlacedTo().getFlatNumber()));
                                orderProgress.setOrderStatus(OrderProgress.OrderStatus.PREPARED);
                                orderProgress.setEndTime(System.currentTimeMillis());
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
            ServiceManager.getInstance(OrderTrackSellerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.PREPARED.toString(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject request, JSONObject result) {
                    NotificationUtils.sendNotificationTo(OrderTrackSellerActivity.this, buyerTokenId, NFUtils.constructFoodPreparedMessage(orderProgress.getUserPlacedTo().getFlatNumber()));
                    orderProgress.setOrderStatus(OrderProgress.OrderStatus.PREPARED);
                    orderProgress.setEndTime(System.currentTimeMillis());
                    updateUI(orderProgress.getOrderStatus());
                }

                @Override
                public void onErrorResponse(JSONObject request, VolleyError error) {

                }
            });
            hideProgressDialog();
        }
    }


    public void updateFoodCollected() {
        showProgressDialog();
        if (buyerTokenId == null) {
            ServiceManager.getInstance(getApplicationContext()).getUserNotification(orderProgress.getUserPlacedBy().getUserUid(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject request, JSONObject result) {
                    try {
                        buyerTokenId = result.getJSONObject("Result").getString("data");
                        ServiceManager.getInstance(OrderTrackSellerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.COMPLETED.toString(), new TaskHandler() {
                            @Override
                            public void onTaskCompleted(JSONObject request, JSONObject result) {
                                NotificationUtils.sendNotificationTo(OrderTrackSellerActivity.this, buyerTokenId, NFUtils.constructFoodCollectedMessage());
                                orderProgress.setOrderStatus(OrderProgress.OrderStatus.COMPLETED);
                                orderProgress.setEndTime(System.currentTimeMillis());
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
            ServiceManager.getInstance(OrderTrackSellerActivity.this).updateOrderStatus(orderProgress.getOrderId(), OrderProgress.OrderStatus.COMPLETED.toString(), new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject request, JSONObject result) {
                    NotificationUtils.sendNotificationTo(OrderTrackSellerActivity.this, buyerTokenId, NFUtils.constructFoodCollectedMessage());
                    orderProgress.setOrderStatus(OrderProgress.OrderStatus.COMPLETED);
                    orderProgress.setEndTime(System.currentTimeMillis());
                    updateUI(orderProgress.getOrderStatus());
                }

                @Override
                public void onErrorResponse(JSONObject request, VolleyError error) {

                }
            });
            hideProgressDialog();
        }
    }

}
