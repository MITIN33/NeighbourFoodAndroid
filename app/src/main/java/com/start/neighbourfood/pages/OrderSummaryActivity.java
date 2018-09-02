package com.start.neighbourfood.pages;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.neighbourfood.R;
import com.start.neighbourfood.adapters.OrderItemsAdapter;
import com.start.neighbourfood.models.FoodItemDetails;

import java.io.IOException;
import java.util.List;

public class OrderSummaryActivity extends BaseActivity {

    private List<FoodItemDetails> foodItemDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
