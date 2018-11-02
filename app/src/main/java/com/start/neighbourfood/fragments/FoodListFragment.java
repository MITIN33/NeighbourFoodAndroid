package com.start.neighbourfood.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.RecyclerTouchListener;
import com.start.neighbourfood.adapters.FoodItemsRecyclerViewAdapter;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.v1.response.FoodItem;
import com.start.neighbourfood.models.v1.response.SellerItemDetail;
import com.start.neighbourfood.pages.OrderSummaryActivity;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class FoodListFragment extends BaseFragment implements TaskHandler, ElegantNumberButton.OnValueChangeListener {

    protected List<FoodItem> mDataset;
    private FoodItemsRecyclerViewAdapter mAdapter;
    private Button goToCartButton;
    private String flatNumber;
    private List<FoodItem> orderItems;
    private FoodItem selectedItem;
    private HashMap<FoodItem, String> orderQuantityMap;
    private SellerItemDetail sellerItemDetail;
    private TextView sellerName;
    private TextView flatName;

    public FoodListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        try {
            super.onCreateView(inflater,container,savedInstanceState);
            View rootView = inflater.inflate(R.layout.content_food_list, container, false);
            RecyclerView mRecyclerView = rootView.findViewById(R.id.detail_food_items_recycleView);
            flatName = rootView.findViewById(R.id.flatName);
            sellerName = rootView.findViewById(R.id.sellerName);
            mAdapter = new FoodItemsRecyclerViewAdapter(this);
            orderQuantityMap = new HashMap<>();
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    loadFoodItems();
                }
            });
            //Set On Click listner
            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    selectedItem = mDataset.get(position);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            goToCartButton = rootView.findViewById(R.id.goToCart);
            goToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), OrderSummaryActivity.class);
                    orderItems = new ArrayList<>();
                    boolean flag = false;
                    for (Map.Entry<FoodItem, String> entry : orderQuantityMap.entrySet()) {
                        if (Integer.parseInt(entry.getValue()) != 0) {
                            FoodItem foodItemDetails = entry.getKey();
                            foodItemDetails.setQuantity(entry.getValue());
                            flag = true;
                            orderItems.add(foodItemDetails);
                        }
                    }
                    if (flag == false) {
                        Toast.makeText(getActivity(), "Please select some items to add.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    saveOrderItems(orderItems);
                    getActivity().startActivity(i);
                    //getActivity().finish();
                }
            });

            return rootView;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void loadFoodItems() {
        showProgressDialog();
        String sellerId = getArguments().getString("sellerId");
        flatNumber = getArguments().getString("flatNumber");
        ServiceManager.getInstance(getActivity()).fetchFoodItemsForFlat(sellerId, this);
    }

    @Override
    public void onTaskCompleted(JSONObject result) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            sellerItemDetail = objectMapper.readValue(result.getJSONObject("Result").toString(), SellerItemDetail.class);
            mDataset = sellerItemDetail.getFoodItemDetail();
            sellerName.setText(String.format("Seller: %s %s", sellerItemDetail.getfName(), sellerItemDetail.getlName()));
            flatName.setText("Flat: " + sellerItemDetail.getFlatNumber());
            mAdapter.setmDataSet(mDataset);
            mAdapter.notifyDataSetChanged();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        hideProgressDialog();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        hideProgressDialog();
    }

    private void saveOrderItems(List<FoodItem> orderItems) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = null;

        SellerItemDetail orderedItems = new SellerItemDetail();
        orderedItems.setFlatNumber(sellerItemDetail.getFlatNumber());
        orderedItems.setfName(sellerItemDetail.getfName());
        orderedItems.setlName(sellerItemDetail.getlName());
        orderedItems.setSellerId(sellerItemDetail.getSellerId());
        orderedItems.setFoodItemDetail(orderItems);

        saveFromSharedPreference("orderedItem", null);
        try {
            jsonInString = mapper.writeValueAsString(orderedItems);
            saveFromSharedPreference("orderedItem", jsonInString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
        orderQuantityMap.put(selectedItem, String.valueOf(newValue));
    }
}
