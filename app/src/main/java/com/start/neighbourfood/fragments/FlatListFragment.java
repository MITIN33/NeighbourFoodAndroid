package com.start.neighbourfood.fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.start.neighbourfood.Utils.RecyclerTouchListener;
import com.start.neighbourfood.adapters.FlatsInfoRecyclerViewAdapter;
import com.start.neighbourfood.R;
import com.start.neighbourfood.models.FlatsInfo;
import com.start.neighbourfood.models.FoodItemDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class FlatListFragment extends Fragment {

    private List<FlatsInfo> mDataset;
    public FlatsInfoRecyclerViewAdapter mAdapter;
    private SearchView searchView;

    public FlatListFragment(){

    }

    @SuppressLint("ValidFragment")
    public FlatListFragment(List<FlatsInfo> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        View rootView = inflater.inflate(R.layout.content_flat_list, container, false);
        RecyclerView mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mAdapter = new FlatsInfoRecyclerViewAdapter(getActivity(), mDataset);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        //Set On Click listner
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                FlatsInfo flats = mDataset.get(position);
                //Toast.makeText(getContext(), flats.getItemName() + " is selected!", Toast.LENGTH_SHORT).show();
                loadFoodItemsForFlat(flats);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return rootView;
    }

    private void loadFoodItemsForFlat(FlatsInfo flatsInfo) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Slide exitSlide = new Slide();
        exitSlide.setSlideEdge(Gravity.LEFT);
        Slide enterSlide = new Slide();
        enterSlide.setSlideEdge(Gravity.RIGHT);


        List<FoodItemDetails> mDataset = new ArrayList<>();
        mDataset.add(new FoodItemDetails("Samosa", "430"));
        FoodListFragment fragment = new FoodListFragment(mDataset);
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragment.setEnterTransition(enterSlide);
        fragment.setExitTransition(exitSlide);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        SearchManager searchManager = (SearchManager)getContext().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
    }
}
