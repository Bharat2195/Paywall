package com.tornado.cphp.paywall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class VendorProductShowImage extends AppCompatActivity {

    ImageView imgCancle,imgProduct;

    PhotoViewAttacher mAttacher;

    String strImagePath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_product_show_image);

        getSupportActionBar().hide();

        imgCancle= (ImageView) findViewById(R.id.imgCancle);
        imgProduct= (ImageView) findViewById(R.id.imgProduct);

//        Intent intent=getIntent();
//        Bitmap bitmap= (Bitmap) intent.getParcelableExtra(Intent.EXTRA_STREAM);
//        Picasso.with(getApplicationContext()).load(strImagePath).into(imgProduct);
//        imgProduct.setImageBitmap(bitmap);
//        mAttacher=new PhotoViewAttacher(imgProduct);

        imgCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(DashBoard_product_show_image.this, "Cliked!!!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }
}
