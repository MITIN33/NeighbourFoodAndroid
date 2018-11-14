package com.start.neighbourfood.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.start.neighbourfood.NFApplication;
import com.start.neighbourfood.R;
import com.start.neighbourfood.models.v1.UserBaseInfo;

public class BaseFragment extends Fragment {

    private ProgressDialog mProgressDialog;
    private NFApplication nfApplication;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfApplication = (NFApplication) getActivity().getApplication();
    }

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

    public UserBaseInfo getUserBaseInfo(){
        return NFApplication.getSharedPreferenceUtils().getUserBaseInfo();
    }
}
