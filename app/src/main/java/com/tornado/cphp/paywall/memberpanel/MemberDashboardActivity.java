package com.tornado.cphp.paywall.memberpanel;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tornado.cphp.paywall.R;

public class MemberDashboardActivity extends AppCompatActivity {

    private static final String TAG = MemberDashboardActivity.class.getSimpleName();
    private Toolbar mToolbarMemberDashboard;
    private ImageView imgLogout;
    private TextView txtNo,txtYes;
    public static final String PREFS_NAME = "MemberLoginPrefes";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_dashboard);
        getSupportActionBar().hide();

        mToolbarMemberDashboard= (Toolbar) findViewById(R.id.mToolbarMemberDashboard);
        imgLogout=(ImageView)mToolbarMemberDashboard.findViewById(R.id.imgLogout);
        mToolbarMemberDashboard.setNavigationIcon(R.drawable.back_icon);
        mToolbarMemberDashboard.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MemberDashboardActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.logout);
                txtNo = (TextView) dialog.findViewById(R.id.txtNo);
                txtYes = (TextView) dialog.findViewById(R.id.txtYes);
                dialog.show();
                txtNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                txtYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(MemberDashboardActivity.this, MemberLoginActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });



    }
}
