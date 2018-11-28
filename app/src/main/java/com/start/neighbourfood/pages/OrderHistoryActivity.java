package com.start.neighbourfood.pages;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.RecyclerTouchListener;
import com.start.neighbourfood.adapters.TrackOrderAdapter;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.response.ResponseOrderHistory;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class OrderHistoryActivity extends BaseActivity {

    private String userUid;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<ResponseOrderHistory> orderDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.emptyMessage).setVisibility(View.GONE);
        userUid = FirebaseAuth.getInstance().getUid();
        mSwipeRefreshLayout = findViewById(R.id.orderListSwipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchAllOrdersByUser();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView = findViewById(R.id.buyerOrderList_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(OrderHistoryActivity.this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(OrderHistoryActivity.this, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if ("PLACED".equals(orderDetails.get(position).getOrderType())) {
                    navigateToBuyerTrackOrder(orderDetails.get(position).getOrderId(), orderDetails.get(position).getFlatNumber());
                } else {
                    navigateToSellerTrackOrder(orderDetails.get(position).getOrderId(), orderDetails.get(position).getFlatNumber());
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    protected void onResume() {
        fetchAllOrdersByUser();
        super.onResume();
    }

    private void fetchAllOrdersByUser() {
        //showProgressDialog();
        mSwipeRefreshLayout.setRefreshing(true);
        boolean flag = true;
        ServiceManager.getInstance(this).fetchAllPastOrderForBuyer(userUid, new TaskHandler() {
            @Override
            public void onTaskCompleted(JSONObject request, JSONObject result) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    orderDetails = objectMapper.readValue(result.getJSONArray("Result").toString(), new TypeReference<List<ResponseOrderHistory>>() {
                    });
                    mRecyclerView.setAdapter(new TrackOrderAdapter(orderDetails));
                    if (orderDetails.size() == 0) {
                        findViewById(R.id.emptyMessage).setVisibility(View.VISIBLE);
                    }


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                //hideProgressDialog();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onErrorResponse(JSONObject request, VolleyError error) {
                hideProgressDialog();
            }
        });
    }
}
