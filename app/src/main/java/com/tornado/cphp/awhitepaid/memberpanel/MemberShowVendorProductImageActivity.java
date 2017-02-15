package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.tornado.cphp.awhitepaid.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MemberShowVendorProductImageActivity extends AppCompatActivity {


    private static final String TAG = MemberShowVendorProductImageActivity.class.getSimpleName();

    PhotoViewAttacher mAttacher;
    String strImagePath, strId;
    @BindView(R.id.imgCancle)
    ImageView imgCancle;
    @BindView(R.id.relative_layout)
    RelativeLayout relativeLayout;
    @BindView(R.id.imgProduct)
    ImageView imgProduct;
    Dialog dialog;
    RequestQueue requestQueue;
    @BindView(R.id.imgMap)
    ImageView imgMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_show_vendor_product_image);
        ButterKnife.bind(this);

        getSupportActionBar().hide();

        requestQueue = Volley.newRequestQueue(this);

        imgCancle = (ImageView) findViewById(R.id.imgCancle);
        imgProduct = (ImageView) findViewById(R.id.imgProduct);

        Intent intent = getIntent();
        strImagePath = intent.getStringExtra("strImagePath");
        strId = intent.getStringExtra("strId");
        Picasso.with(getApplicationContext()).load(strImagePath).into(imgProduct);
        mAttacher = new PhotoViewAttacher(imgProduct);


    }

    @OnClick({R.id.imgCancle, R.id.imgMap})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgCancle:
                onBackPressed();
                break;
            case R.id.imgMap:
                Intent intent = new Intent(MemberShowVendorProductImageActivity.this, MemberShowVendorAddressonMapActivity.class);
                intent.putExtra("strVendorId", strId);
                startActivity(intent);
                break;
        }
    }
}
