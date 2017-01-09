package com.tornado.cphp.paywall.memberpanel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tornado.cphp.paywall.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MemberActivationActivity extends AppCompatActivity {

    private static final String TAG = MemberActivationActivity.class.getSimpleName();
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.txtMemberId)
    TextView txtMemberId;
    @BindView(R.id.etMemberid)
    EditText etMemberid;
    @BindView(R.id.txtMemberName)
    TextView txtMemberName;
    @BindView(R.id.etMemberName)
    EditText etMemberName;
    @BindView(R.id.txtAmount)
    TextView txtAmount;
    @BindView(R.id.spnAmount)
    Spinner spnAmount;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    private Toolbar mToolbarActiveMember;
    private TextView txtTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_activation);
        ButterKnife.bind(this);
        getSupportActionBar().hide();

        mToolbarActiveMember=(Toolbar)findViewById(R.id.mToolbarActiveMember);
        txtTitle=(TextView)mToolbarActiveMember.findViewById(R.id.txtTitle);
        txtTitle.setText("Activation Member");

        mToolbarActiveMember.setNavigationIcon(R.drawable.back_icon);
        mToolbarActiveMember.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        YoYo.with(Techniques.FadeInLeft)
                .duration(700)
                .playOn(etMemberid);

        YoYo.with(Techniques.FadeInLeft)
                .duration(700)
                .playOn(etMemberName);


        YoYo.with(Techniques.FadeInLeft)
                .duration(700)
                .playOn(spnAmount);

        YoYo.with(Techniques.FadeInLeft)
                .duration(700)
                .playOn(btnSubmit);

    }

    @OnClick(R.id.btnSubmit)
    public void onClick() {
    }
}
