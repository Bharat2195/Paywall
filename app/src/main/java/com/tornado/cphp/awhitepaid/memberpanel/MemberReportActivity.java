package com.tornado.cphp.awhitepaid.memberpanel;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.memberfragment.MemberFundTransferFragment;
import com.tornado.cphp.awhitepaid.memberfragment.MemberRechargeFragmentReport;
import com.tornado.cphp.awhitepaid.memberfragment.MemberRewardFragmentReport;
import com.tornado.cphp.awhitepaid.memberfragment.NonWorkingIncomeFragment;
import com.tornado.cphp.awhitepaid.memberfragment.WorkingIncomeFragment;
import com.tornado.cphp.awhitepaid.utils.CheckConnectivity;

import java.util.ArrayList;
import java.util.List;

public class MemberReportActivity extends AppCompatActivity {

    private static final String TAG = MemberReportActivity.class.getSimpleName();

    TabLayout tabLayout;
    private Toolbar mToolbarReport;
    private ViewPager viewPager;
    private TextView txtTitle;
    private String tabIndex;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_report);

        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        isConnected = CheckConnectivity.checkNow(getApplicationContext());

        if (!isConnected) {
            Toast.makeText(MemberReportActivity.this, "Sorry! Not connected to Internet", Toast.LENGTH_SHORT).show();
        }
//        if (checkConnection==false){
//            StringUtils.showToast("Newtwokt Not Available",getApplicationContext());
//        }else {
//
//        }
        mToolbarReport= (Toolbar) findViewById(R.id.mToolbarReport);
        txtTitle = (TextView) mToolbarReport.findViewById(R.id.txtTitle);
        txtTitle.setText("Member Repots");
        mToolbarReport.setNavigationIcon(R.drawable.back_icon);
        mToolbarReport.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        tabIndex = intent.getStringExtra("tabindex");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        if (tabIndex != null) {
            viewPager.setCurrentItem(Integer.valueOf(tabIndex));
        }


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NonWorkingIncomeFragment(), "Non Working Cashback");
        adapter.addFragment(new WorkingIncomeFragment(), "Working Cashback");
        adapter.addFragment(new MemberFundTransferFragment(), "Fund Transfer");
        adapter.addFragment(new MemberRechargeFragmentReport(), "Recharge Report");
        adapter.addFragment(new MemberRewardFragmentReport(),"Reward Report");

        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



}
