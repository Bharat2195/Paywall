package com.tornado.cphp.awhitepaid;

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

import com.tornado.cphp.awhitepaid.utils.CheckConnectivity;

import java.util.ArrayList;
import java.util.List;

public class PaySendMoneyVendorActivity extends AppCompatActivity {

    private static final String TAG = PaySendMoneyVendorActivity.class.getSimpleName();

    TabLayout tabLayout;
    private Toolbar toolbar_payorsendmoney;
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
        setContentView(R.layout.activity_pay_send_money_vendor);
        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        isConnected = CheckConnectivity.checkNow(getApplicationContext());

        if (!isConnected) {
            Toast.makeText(PaySendMoneyVendorActivity.this, "Sorry! Not connected to Internet", Toast.LENGTH_SHORT).show();
        }
//        if (checkConnection==false){
//            StringUtils.showToast("Newtwokt Not Available",getApplicationContext());
//        }else {
//
//        }
        toolbar_payorsendmoney = (Toolbar) findViewById(R.id.toolbar_payorsendmoney);
        txtTitle = (TextView) toolbar_payorsendmoney.findViewById(R.id.txtTitle);
        txtTitle.setText("Payment Transfer");
        toolbar_payorsendmoney.setNavigationIcon(R.drawable.back_icon);
        toolbar_payorsendmoney.setNavigationOnClickListener(new View.OnClickListener() {
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
//            setupTabIcons();

    }

    private void setupUnselectTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

//    private void setupTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabSelectIcon[0]);
//        tabLayout.getTabAt(1).setIcon(tabSelectIcon[1]);
//        tabLayout.getTabAt(2).setIcon(tabSelectIcon[2]);
//    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ScanCodeFragment(), "Scan Code");
        adapter.addFragment(new ShowCodeFragment(), "Show Code");
        adapter.addFragment(new WithdrawalFragment(), "Withdrawal");

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
