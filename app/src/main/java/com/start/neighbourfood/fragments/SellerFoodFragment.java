package com.start.neighbourfood.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.RecyclerTouchListener;
import com.start.neighbourfood.adapters.FoodItemsRecyclerViewAdapter;
import com.start.neighbourfood.adapters.SellerItemAdapter;
import com.start.neighbourfood.models.FlatsInfo;
import com.start.neighbourfood.models.FoodItemDetails;
import com.start.neighbourfood.models.RecyclerItemTouchHelper;

import java.util.List;

public class SellerFoodFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener  {

    protected List<FlatsInfo> mDataset;
    protected SellerItemAdapter sellerItemAdapter;
    private CoordinatorLayout coordinatorLayout;

    public SellerFoodFragment(){}

    @SuppressLint("ValidFragment")
    public SellerFoodFragment(List<FlatsInfo> mDataset){
        this.mDataset = mDataset;
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
            sellerItemAdapter = new SellerItemAdapter(mDataset);
            mRecyclerView.setAdapter(sellerItemAdapter);
            //coordinatorLayout = inflater.inflate(R.id.coordinateLayout, container);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            //Set On Click listner
            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    FlatsInfo flats = mDataset.get(position);
                    Toast.makeText(getContext(), flats.getFlatNumber() + " is selected!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
            return rootView;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof SellerItemAdapter.SellerItemHolder) {
            // get the removed item name to display it in snack bar
            String name = mDataset.get(viewHolder.getAdapterPosition()).getFlatNumber();

            // backup of removed item for undo purpose
            final FlatsInfo deletedItem = mDataset.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            sellerItemAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    sellerItemAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
