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
import com.tornado.cphp.awhitepaid.memberfragment.MemberScanCodeFragment;
import com.tornado.cphp.awhitepaid.memberfragment.MemberShowCodeFragment;
import com.tornado.cphp.awhitepaid.memberfragment.MemberWithdrawalFragment;
import com.tornado.cphp.awhitepaid.utils.CheckConnectivity;

import java.util.ArrayList;
import java.util.List;

public class MemberPaymentTransferActivtiy extends AppCompatActivity {

    private static final String TAG = MemberPaymentTransferActivtiy.class.getSimpleName();

    TabLayout tabLayout;
    private Toolbar mToolbarPaymentTransfer;
    private ViewPager viewPager;
    private TextView txtTitle;
    private String tabIndex;
    private boolean isConnected;
    private int[] tabIcons = {
            R.drawable.scan_code_unselected,
            R.drawable.show_code_unselected,
            R.drawable.payment_request_unselected
    };
    private int[] tabSelectIcon = {
            R.drawable.scan_code_selected,
            R.drawable.show_code_selected,
            R.drawable.payment_request_selected
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_payment_transfer_activtiy);
        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        isConnected = CheckConnectivity.checkNow(getApplicationContext());

        if (!isConnected) {
            Toast.makeText(MemberPaymentTransferActivtiy.this, "Sorry! Not connected to Internet", Toast.LENGTH_SHORT).show();
        }
//        if (checkConnection==false){
//            StringUtils.showToast("Newtwokt Not Available",getApplicationContext());
//        }else {
//
//        }
        mToolbarPaymentTransfer = (Toolbar) findViewById(R.id.mToolbarPaymentTransfer);
        txtTitle = (TextView) mToolbarPaymentTransfer.findViewById(R.id.txtTitle);
        txtTitle.setText("Payment Transfer");
        mToolbarPaymentTransfer.setNavigationIcon(R.drawable.back_icon);
        mToolbarPaymentTransfer.setNavigationOnClickListener(new View.OnClickListener() {
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
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MemberScanCodeFragment(), "Scan Code");
        adapter.addFragment(new MemberShowCodeFragment(), "Show Code");
        adapter.addFragment(new MemberWithdrawalFragment(), "Withdrawal");

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
