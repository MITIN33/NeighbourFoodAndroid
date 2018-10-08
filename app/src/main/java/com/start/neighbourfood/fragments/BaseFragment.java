package com.start.neighbourfood.fragments;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

import com.start.neighbourfood.R;
import com.start.neighbourfood.pages.BaseActivity;

public class BaseFragment extends Fragment {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null && getActivity() != null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(true);
        }
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }

    public String getFromSharedPreference(String key) {
        if (getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).getFromSharedPreference(key);
        }
        return null;
    }

    public boolean saveFromSharedPreference(String key, String object) {
        if (getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).saveStringInSharedPreference(key, object);
        }
        return false;
    }
}
