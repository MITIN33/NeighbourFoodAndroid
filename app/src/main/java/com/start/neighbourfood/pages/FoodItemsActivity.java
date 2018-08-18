package com.start.neighbourfood.pages;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neighbourfood.start.neighbourfood.R;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.fragments.FoodItemsDetailFragment;
import com.start.neighbourfood.models.FlatsInfo;
import com.start.neighbourfood.models.FoodItemDetails;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class FoodItemsActivity extends BaseActivity {

    private static final String TAG = "FOOD_ITEM_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_items);

        /*final ElegantNumberButton button = (ElegantNumberButton) findViewById(R.id.quantity_button);
        button.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantitySelected = button.getNumber();
            }
        });

        // set the initial and the final Number for the quantity_button
        Integer minQty = 0;
        Integer maxQty = 10;        // Read this from the DB later
        button.setRange(minQty, maxQty);*/

        String flatID = "";
        ServiceManager.getInstance(this).fetchFoodItemForFlat(flatID,new PopulateFoodItemsTaskHandler());
    }
    private class PopulateFoodItemsTaskHandler implements TaskHandler{

        @Override
        public void onTaskCompleted(JSONObject result) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<FoodItemDetails> foodDetails = objectMapper.readValue(result.getJSONArray("Result").toString(), new TypeReference<List<FlatsInfo>>() {
                });
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                FoodItemsDetailFragment fragment = new FoodItemsDetailFragment(foodDetails);
                transaction.replace(R.id.food_items_fragment, fragment);
                transaction.commit();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hideProgressDialog();
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "onErrorResponse: Unable to load", error);
        }
    }
}
