package com.start.neighbourfood.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.RecyclerTouchListener;
import com.start.neighbourfood.adapters.SellerItemAdapter;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.FoodItem;
import com.start.neighbourfood.models.FoodItemDetails;
import com.start.neighbourfood.models.RecyclerItemTouchHelper;
import com.start.neighbourfood.models.SellerItemInfo;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SellerFoodFragment extends BaseFragment implements TaskHandler, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    protected List<FoodItemDetails> mDataset;
    protected SellerItemAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton mPlusOneButton;
    private FoodItemDetails selectedItem;

    public SellerFoodFragment() {
    }

    @SuppressLint("ValidFragment")
    public SellerFoodFragment(List<FoodItemDetails> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            super.onCreateView(inflater, container, savedInstanceState);
            View rootView = inflater.inflate(R.layout.content_seller_item_list, container, false);
            RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view);
            final Switch switchOne = (Switch) rootView.findViewById(R.id.switch_one);

            switchOne.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                switchOne.setText("Available");
                                switchOne.setTextColor(ContextCompat.getColor(getContext(), R.color.green_dark));
                            } else {
                                switchOne.setText("Not Available");
                                switchOne.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_500));
                            }
                        }
                    });
            coordinatorLayout = (CoordinatorLayout) rootView;
            mAdapter = new SellerItemAdapter(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogueBox();
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

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

            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

            setFloatingButtonAction(rootView);
            return rootView;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setFloatingButtonAction(View rootView) {
        mPlusOneButton = rootView.findViewById(R.id.add_food_item_button);
        mPlusOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open popup window
                showDialogueBox();
            }
        });
    }

    private void fetchFoodItem(final View promptsView) {

        ServiceManager.getInstance(getActivity()).fetchAllFoodItem(new TaskHandler() {
            @Override
            public void onTaskCompleted(JSONObject result) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    List<FoodItem> arrayList = mapper.readValue(result.getJSONArray("Result").toString(), new TypeReference<List<FoodItem>>() {
                    });
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (getActivity(), android.R.layout.select_dialog_item, getListFoodItem(arrayList));
                    //Getting the instance of AutoCompleteTextView
                    AutoCompleteTextView actv = promptsView.findViewById(R.id.seller_food_item_name);
                    actv.setThreshold(1);//will start working from first character
                    actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private List<String> getListFoodItem(List<FoodItem> foodItems) {
        List<String> list = new ArrayList<>();
        for (FoodItem item : foodItems) {
            list.add(item.toString());
        }
        return list;
    }

    private void loadFoodItems() {
        showProgressDialog();
        String sellerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ServiceManager.getInstance(getActivity()).fetchFoodItemsForFlat(sellerId, this);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof SellerItemAdapter.SellerItemHolder) {
            // get the removed item name to display it in snack bar
            String name = mDataset.get(viewHolder.getAdapterPosition()).getSellerID();

            // backup of removed item for undo purpose
            final FoodItemDetails deletedItem = mDataset.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onTaskCompleted(JSONObject result) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<FoodItemDetails> foodItemDetails = objectMapper.readValue(result.getJSONArray("Result").toString(), new TypeReference<List<FoodItemDetails>>() {
            });
            mDataset = foodItemDetails;
            mAdapter.setDataset(foodItemDetails);
            mAdapter.notifyDataSetChanged();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        hideProgressDialog();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }


    private void showDialogueBox() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.add_seller_items_dialog_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set add_seller_items_dialog_prompt.xmlt.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText foodItemName = (EditText) promptsView
                .findViewById(R.id.seller_food_item_name);
        final NumberPicker numberPicker = (NumberPicker) promptsView
                .findViewById(R.id.numberPicker);
        final EditText foodItemDesc = (EditText) promptsView
                .findViewById(R.id.seller_food_item_desc);
        final EditText foodItemPrice = (EditText) promptsView
                .findViewById(R.id.seller_food_item_price);
        RadioGroup radioGroup = promptsView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {

                }
            }
        });

        if (numberPicker != null) {
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(10);
            numberPicker.setWrapSelectorWheel(true);
        }

        if (selectedItem != null) {
            foodItemName.setText(selectedItem.getItemName());
            numberPicker.setValue(Integer.parseInt(selectedItem.getServedFor()));
            foodItemDesc.setText(selectedItem.getItemDesc());
            foodItemPrice.setText(selectedItem.getPrice());
        }

        fetchFoodItem(promptsView);

        // set dialog message
        alertDialogBuilder
                .setTitle("Add the food item details:")
                .setCancelable(false)
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                // Save the data to the database from here

                                Gson gson = new Gson();
                                SellerItemInfo sellerItemInfo = new SellerItemInfo();
                                sellerItemInfo.setFoodName(String.valueOf(foodItemName.getText()));
                                sellerItemInfo.setServedFor(numberPicker.getValue());
                                sellerItemInfo.setSellerID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                sellerItemInfo.setPrice(String.valueOf(foodItemPrice.getText()));
                                sellerItemInfo.setAvailable(true);
                                try {
                                    ServiceManager.getInstance(getActivity()).addSellerItem(new JSONObject(gson.toJson(sellerItemInfo)), new TaskHandler() {
                                        @Override
                                        public void onTaskCompleted(JSONObject result) {
                                            Toast.makeText(getContext(), foodItemName.getText() + " is added!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("ADD_SELLER_ITEM", error.getMessage());
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show the dialog box
        alertDialog.show();
    }
}
