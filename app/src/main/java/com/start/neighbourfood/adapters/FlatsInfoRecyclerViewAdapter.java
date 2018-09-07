package com.start.neighbourfood.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.start.neighbourfood.R;
import com.start.neighbourfood.models.FlatsInfo;
import com.start.neighbourfood.models.FoodItem;

import java.util.ArrayList;
import java.util.List;


public class FlatsInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = "FLAT_ADAPTOR";

    private List<FlatsInfo> mDataSet;
    private List<FlatsInfo> flatlistfiltered;
    private Context context;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public FlatsInfoRecyclerViewAdapter(Context context, List<FlatsInfo> dataSet) {
        // For now, giving the dummy food items
        mDataSet = dataSet;
        this.context = context;
        flatlistfiltered = mDataSet;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        // Create a new view.
        View v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_flat_info, viewGroup, false);

        return new RowViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rowHolder = (RowViewHolder) holder;
        FlatsInfo flatsInfo = flatlistfiltered.get(position);
        String flatText = "Served by Flat: "+flatsInfo.getFlatNumber();
        rowHolder.flatNumber.setText(flatText);
        rowHolder.rating.setText(flatsInfo.getRating() == null ? "4/5" : flatsInfo.getRating());
        rowHolder.userName.setText(flatsInfo.getSellerName());
        rowHolder.imageView.setImageResource(R.drawable.food_icon);
        List<FoodItem> list = new ArrayList<>();
        int k =0;
        for (FoodItem item : flatsInfo.getFoodItems()){
            list.add(item);
            if (k++ > 3)
                break;
        }
        final ArrayAdapter<FoodItem> arrayAdapter =
                new ArrayAdapter<FoodItem>(context, R.layout.nf_list_item, list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                /// Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text size 25 dip for ListView each item
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);

                // Return the view
                return view;
            }
        };

        rowHolder.foodListView.setAdapter(arrayAdapter);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return flatlistfiltered.size();
    }

    public void setDataSet(List<FlatsInfo> dataset){
        mDataSet = dataset;
        flatlistfiltered = dataset;
    }

    public class RowViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, flatNumber, rating;
        public ImageView imageView;
        public ListView foodListView;

        public RowViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.user_name);
            imageView = (ImageView) v.findViewById(R.id.food_image);
            foodListView = (ListView) v.findViewById(R.id.items_list);
            flatNumber = (TextView) v.findViewById(R.id.flat_number_fragment);
            rating = (TextView) v.findViewById(R.id.rating);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    flatlistfiltered = mDataSet;
                } else {
                    List<FlatsInfo> filteredList = new ArrayList<>();
                    for (FlatsInfo item : mDataSet) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (item.getSellerName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }

                    flatlistfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = flatlistfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                flatlistfiltered = (List<FlatsInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
