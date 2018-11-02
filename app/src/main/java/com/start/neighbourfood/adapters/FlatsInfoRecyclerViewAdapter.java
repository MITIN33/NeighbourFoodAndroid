package com.start.neighbourfood.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import com.squareup.picasso.Picasso;
import com.start.neighbourfood.R;
import com.start.neighbourfood.models.v1.response.HoodDetails;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class FlatsInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = "FLAT_ADAPTOR";

    private List<HoodDetails> mDataSet;
    private List<HoodDetails> flatlistfiltered;
    private Context context;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public FlatsInfoRecyclerViewAdapter(Context context, List<HoodDetails> dataSet) {
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
        HoodDetails flatsInfo = flatlistfiltered.get(position);
        String flatText = "Served by Flat: "+flatsInfo.getFlatNumber();
        rowHolder.flatNumber.setText(flatText);
        rowHolder.rating.setText(flatsInfo.getRating() == null ? "4/5" : flatsInfo.getRating());
        rowHolder.userName.setText(flatsInfo.getSellerName());
        if(flatsInfo.getPhotoUrl() == null){
            rowHolder.imageView.setImageResource(R.drawable.food_icon);
        }else {
            Picasso.get().load(flatsInfo.getPhotoUrl()).into(rowHolder.imageView);
            //new DownLoadImageTask(rowHolder.imageView).execute(flatsInfo.getPhotoUrl());
        }

        List<String> list = new ArrayList<>();
        int k =0;
        for (int i = 0; i < Math.min(3,flatsInfo.getFoodItems().size()); i++) {
            list.add(flatsInfo.getFoodItems().get(i));
        }

        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(context, R.layout.nf_list_item, list){
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

    public void setDataSet(List<HoodDetails> dataset){
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
                    List<HoodDetails> filteredList = new ArrayList<>();
                    for (HoodDetails item : mDataSet) {
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
                flatlistfiltered = (List<HoodDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
