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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.start.neighbourfood.NFApplication;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.RecyclerTouchListener;
import com.start.neighbourfood.adapters.SellerItemAdapter;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.RecyclerItemTouchHelper;
import com.start.neighbourfood.models.v1.UserBaseInfo;
import com.start.neighbourfood.models.v1.request.FoodItemDetails;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class SellerFoodFragment extends BaseFragment implements TaskHandler, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final String TAG = SellerFoodFragment.class.getSimpleName();
    protected List<FoodItemDetails> mDataset;
    protected SellerItemAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private FoodItemDetails selectedItem;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FirebaseUser user;
    int positin;
    private Switch switchOne;

    public SellerFoodFragment() {
    }

    @SuppressLint("ValidFragment")
    public SellerFoodFragment(List<FoodItemDetails> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        positin = -1;
        selectedItem = null;
        user = FirebaseAuth.getInstance().getCurrentUser();
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            super.onCreateView(inflater, container, savedInstanceState);
            View rootView = inflater.inflate(R.layout.content_seller_item_list, container, false);
            final RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view);
            switchOne = rootView.findViewById(R.id.switch_one);
            mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container_seller);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    loadFoodItems();
                }
            });
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_orange_dark,
                    android.R.color.holo_blue_dark);
            switchOne.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                switchOne.setText("Available");
                                switchOne.setTextColor(ContextCompat.getColor(getContext(), R.color.green_dark));
                                ServiceManager.getInstance(getActivity()).toggleAvailability(user.getUid(), String.valueOf(true), null);
                            } else {
                                switchOne.setText("Not Available");
                                ServiceManager.getInstance(getActivity()).toggleAvailability(user.getUid(), String.valueOf(false), null);
                                switchOne.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_500));
                            }
                        }
                    });
            coordinatorLayout = (CoordinatorLayout) rootView;
            mAdapter = new SellerItemAdapter(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (positin != -1) {
                        selectedItem = mDataset.get(positin);
                    }
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
                    positin = position;
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
        FloatingActionButton mPlusOneButton = rootView.findViewById(R.id.add_food_item_button);
        mPlusOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open popup window
                showDialogueBox();
            }
        });
    }

    private void loadFoodItems() {
        //showProgressDialog();
        String sellerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ServiceManager.getInstance(getActivity()).fetchSellingItemsForFlat(sellerId, this);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof SellerItemAdapter.SellerItemHolder) {
            // get the removed item name to display it in snack bar
            final String id = mDataset.get(viewHolder.getAdapterPosition()).getSellerItemID();
            final String name = mDataset.get(viewHolder.getAdapterPosition()).getItemName();

            // backup of removed item for undo purpose
            final FoodItemDetails deletedItem = mDataset.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());


            ServiceManager.getInstance(getActivity()).removeSellerItem(id, new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject request, JSONObject result) {
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

                @Override
                public void onErrorResponse(JSONObject request, VolleyError error) {

                }
            });
        }
    }

    @Override
    public void onTaskCompleted(JSONObject request, JSONObject result) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<FoodItemDetails> foodItemDetails = objectMapper.readValue(result.getJSONArray("Result").toString(), new TypeReference<List<FoodItemDetails>>() {
            });
            mDataset = foodItemDetails;
            mAdapter.setDataset(foodItemDetails);
            mAdapter.notifyDataSetChanged();
            if (foodItemDetails.size() > 0) {
                switchOne.setChecked(foodItemDetails.get(0).isAvailable());
            }
            else {
                switchOne.setChecked(true);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        //hideProgressDialog();
    }

    @Override
    public void onErrorResponse(JSONObject request, VolleyError error) {

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
        final ElegantNumberButton numberPicker = (ElegantNumberButton) promptsView
                .findViewById(R.id.numberPicker);
        final EditText foodItemDesc = (EditText) promptsView
                .findViewById(R.id.seller_food_item_desc);
        final EditText foodItemPrice = (EditText) promptsView
                .findViewById(R.id.seller_food_item_price);
        final RadioButton radioGroup = promptsView.findViewById(R.id.radio_veg);

        if (numberPicker != null) {
            numberPicker.setRange(1, 10);
        }

        if (selectedItem != null) {
            foodItemName.setText(selectedItem.getItemName());
            numberPicker.setNumber(selectedItem.getServedFor());
            foodItemDesc.setText(selectedItem.getItemDesc());
            foodItemPrice.setText(selectedItem.getPrice());
        }

        //fetchFoodItem(promptsView);
        String updateBtnText = selectedItem != null ? "Update" : "Add";
        // set dialog message
        alertDialogBuilder
                .setTitle("Add the food item details:")
                .setCancelable(false)
                .setPositiveButton(updateBtnText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                // Save the data to the database from here

                                UserBaseInfo userBaseInfo = NFApplication.getSharedPreferenceUtils().getUserBaseInfo();
                                Gson gson = new Gson();
                                final FoodItemDetails foodItemDetails = new FoodItemDetails();
                                foodItemDetails.setItemName(String.valueOf(foodItemName.getText()));
                                foodItemDetails.setServedFor(String.valueOf(numberPicker.getNumber()));
                                foodItemDetails.setSellerID(userBaseInfo.getUserUid());
                                foodItemDetails.setPrice(String.valueOf(foodItemPrice.getText()));
                                foodItemDetails.setFlatID(userBaseInfo.getFlatId());
                                foodItemDetails.setItemDesc(String.valueOf(foodItemDesc.getText()));
                                foodItemDetails.setVeg(radioGroup.isChecked());
                                foodItemDetails.setAvailable(true);
                                try {

                                    if (selectedItem == null) {
                                        ServiceManager.getInstance(getActivity()).addSellerItem(new JSONObject(gson.toJson(foodItemDetails)), new TaskHandler() {
                                            @Override
                                            public void onTaskCompleted(JSONObject request, JSONObject result) {
                                                mDataset.add(foodItemDetails);
                                                mAdapter.notifyDataSetChanged();
                                                Toast.makeText(getContext(), foodItemName.getText() + " is added!", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onErrorResponse(JSONObject request, VolleyError error) {
                                                Log.e(TAG, error.getMessage());
                                            }
                                        });
                                    } else {
                                        ServiceManager.getInstance(getActivity()).updateSellerItem(selectedItem.getSellerItemID(), new JSONObject(gson.toJson(foodItemDetails)), new TaskHandler() {
                                            @Override
                                            public void onTaskCompleted(JSONObject request, JSONObject result) {
                                                mAdapter.notifyItemChanged(positin, foodItemDetails);
                                                Toast.makeText(getContext(), foodItemName.getText() + " is updated!", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onErrorResponse(JSONObject request, VolleyError error) {
                                                Log.e("ADD_SELLER_ITEM", error.getMessage());
                                            }
                                        });
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                selectedItem = null;
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                selectedItem = null;
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show the dialog box
        alertDialog.show();

    }
}
