package com.tornado.cphp.paywall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tornado.cphp.paywall.memberpanel.MemberLoginActivity;

public class LoginTypeActivity extends AppCompatActivity {

    private static final String TAG=LoginTypeActivity.class.getSimpleName();
    private Button btnVendorLogin,btnMemberLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}
