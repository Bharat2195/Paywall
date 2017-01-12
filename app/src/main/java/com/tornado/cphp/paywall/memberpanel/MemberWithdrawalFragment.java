package com.tornado.cphp.paywall.memberpanel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tornado.cphp.paywall.R;

/**
 * Created by cphp on 12-Jan-17.
 */

public class MemberWithdrawalFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_withdrawal,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
