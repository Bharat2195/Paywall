package com.tornado.cphp.awhitepaid.vendorpanel;

import android.content.Intent;
import android.net.Uri;
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
import com.tornado.cphp.awhitepaid.utils.CheckConnectivity;
import com.tornado.cphp.awhitepaid.vendorfragment.ImageUploadVenderFragment;
import com.tornado.cphp.awhitepaid.vendorfragment.ShowImageVenderFragment;

import java.util.ArrayList;
import java.util.List;

public class VendorUploadImageActivity extends AppCompatActivity {


    public static final String UPLOAD_KEY = "image";
    private Uri filePath;

    TabLayout tabLayout;
    private Toolbar toolbar_upload_image;
    private ViewPager viewPager;
    private TextView txtTitle;
    private String tabIndex;
    private boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_upload_image);

        getSupportActionBar().hide();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        isConnected = CheckConnectivity.checkNow(getApplicationContext());

        if (!isConnected) {
            Toast.makeText(VendorUploadImageActivity.this, "Sorry! Not connected to Internet", Toast.LENGTH_SHORT).show();
        }
        toolbar_upload_image = (Toolbar) findViewById(R.id.toolbar_upload_image);
        txtTitle = (TextView) toolbar_upload_image.findViewById(R.id.txtTitle);
        txtTitle.setText("Upload Images");
//        tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbar_upload_image.setNavigationIcon(R.drawable.back_icon);
        toolbar_upload_image.setNavigationOnClickListener(new View.OnClickListener() {
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
//        setupTabIcons();
    }

    private void setupViewPager(ViewPager viewPager) {
        VendorUploadImageActivity.ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ImageUploadVenderFragment(), "Image Upload");
        adapter.addFragment(new ShowImageVenderFragment(), "Show Image");

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
