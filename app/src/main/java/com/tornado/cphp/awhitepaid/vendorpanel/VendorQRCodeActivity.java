package com.tornado.cphp.awhitepaid.vendorpanel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tornado.cphp.awhitepaid.R;

public class VendorQRCodeActivity extends AppCompatActivity {

    private static final String TAG=VendorQRCodeActivity.class.getSimpleName();
    private Toolbar toolbar_qrcode;
    private TextView txtTitle;
    private ImageView img_qr_code_image;
    private TextView txtQRCodeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_qrcode);

        getSupportActionBar().hide();
        toolbar_qrcode=(Toolbar)findViewById(R.id.toolbar_qrcode);
        txtTitle=(TextView)toolbar_qrcode.findViewById(R.id.txtTitle);
        txtTitle.setText("PayWall Code");
        img_qr_code_image=(ImageView)findViewById(R.id.img_qr_code_image);

        txtQRCodeTitle=(TextView)findViewById(R.id.txtQRCodeTitle);
        txtQRCodeTitle.setText("Scan PayWall code to make payment to "+ VendorHomeAcivity.strVendorId);
        toolbar_qrcode.setNavigationIcon(R.drawable.back_icon);
        toolbar_qrcode.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_qr_code_image.setImageBitmap(VendorEdiProfileActivity.bitmap);
    }
}
