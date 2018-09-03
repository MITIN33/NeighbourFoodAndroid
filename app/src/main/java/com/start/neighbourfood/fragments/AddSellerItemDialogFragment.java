package com.start.neighbourfood.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.start.neighbourfood.R;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * @link AddSellerItemDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddSellerItemDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddSellerItemDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton mPlusOneButton;

    public AddSellerItemDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddSellerItemDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddSellerItemDialogFragment newInstance(String param1, String param2) {
        AddSellerItemDialogFragment fragment = new AddSellerItemDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plus_one, container, false);

        //Find the +1 button
        mPlusOneButton = (FloatingActionButton) view.findViewById(R.id.add_food_item_button);
        mPlusOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open popup window
                    LayoutInflater li = LayoutInflater.from(getContext());
                    View promptsView = li.inflate(R.layout.add_seller_items_dialog_prompt, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getContext());

                    // set add_seller_items_dialog_prompt.xmlt.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText foodItemName = (EditText) promptsView
                            .findViewById(R.id.seller_food_item_name);
                    final EditText foodItemServedFor = (EditText) promptsView
                        .findViewById(R.id.seller_food_item_served_for);
                    final EditText foodItemDesc = (EditText) promptsView
                        .findViewById(R.id.seller_food_item_desc);
                    final EditText foodItemPrice = (EditText) promptsView
                        .findViewById(R.id.seller_food_item_price);

                    // set dialog message
                    alertDialogBuilder
                            .setTitle("Add the food item details:")
                            .setCancelable(false)
                            .setPositiveButton("Add",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            // get user input and set it to result
                                            // edit text
                                            // Save the data to the database from here
                                            Toast.makeText(getContext(), foodItemName.getText() + " is added!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show the dialog box
                    alertDialog.show();
                }
            });

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {

       /* if (mListener != null) {
            mListener.onFragmentInteraction(uri);*/
        //}
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri) ;
        }
    }*/

}
