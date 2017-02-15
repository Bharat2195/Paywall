package com.tornado.cphp.awhitepaid.vendorpanel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.memberpanel.MemberLoginActivity;

public class LoginTypeActivity extends AppCompatActivity {

    private static final String TAG=LoginTypeActivity.class.getSimpleName();
    private Button btnVendorLogin,btnMemberLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_type);
        getSupportActionBar().hide();




        btnVendorLogin= (Button) findViewById(R.id.btnVendorLogin);
        btnMemberLogin=(Button)findViewById(R.id.btnMemberLogin);


        btnVendorLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentVendor=new Intent(LoginTypeActivity.this,VendorLoginActivity.class);
                startActivity(intentVendor);

            }
        });

        btnMemberLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMember=new Intent(LoginTypeActivity.this,MemberLoginActivity.class);
                startActivity(intentMember);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intenBackPressed = new Intent(Intent.ACTION_MAIN);
        intenBackPressed.addCategory(Intent.CATEGORY_HOME);
        intenBackPressed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intenBackPressed);
        finish();
        System.exit(0);
    }
}
