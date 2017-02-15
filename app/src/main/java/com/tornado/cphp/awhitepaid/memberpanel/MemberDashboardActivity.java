package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tornado.cphp.awhitepaid.vendorpanel.LoginTypeActivity;
import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MemberDashboardActivity extends AppCompatActivity {

    private static final String TAG = MemberDashboardActivity.class.getSimpleName();
    @BindView(R.id.view)
    View view;
    @BindView(R.id.txtWallet)
    TextView txtWallet;
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.btnAddLocation)
    Button btnAddLocation;
    @BindView(R.id.btnScanCode)
    Button btnScanCode;
    @BindView(R.id.txtBalance)
    TextView txtBalance;
    @BindView(R.id.btnLoadMoeny)
    Button btnLoadMoeny;
    @BindView(R.id.mLinearLayoutMoney)
    LinearLayout mLinearLayoutMoney;
    @BindView(R.id.imgSend)
    ImageView imgSend;
    @BindView(R.id.txtSend)
    TextView txtSend;
    @BindView(R.id.mRelativeSend)
    RelativeLayout mRelativeSend;
    @BindView(R.id.imgWithdrawal)
    ImageView imgWithdrawal;
    @BindView(R.id.txtWithdrawal)
    TextView txtWithdrawal;
    @BindView(R.id.mRelativeWithdrawal)
    RelativeLayout mRelativeWithdrawal;
    @BindView(R.id.imgAddress)
    ImageView imgAddress;
    @BindView(R.id.txtAddress)
    TextView txtAddress;
    @BindView(R.id.mRelativeAddress)
    RelativeLayout mRelativeAddress;
    @BindView(R.id.mLinearLayout)
    LinearLayout mLinearLayout;
    @BindView(R.id.imgPhoto)
    ImageView imgPhoto;
    @BindView(R.id.txtPhoto)
    TextView txtPhoto;
    @BindView(R.id.mRelativePhoto)
    RelativeLayout mRelativePhoto;
    @BindView(R.id.imgPassword)
    ImageView imgPassword;
    @BindView(R.id.txtPassword)
    TextView txtPassword;
    @BindView(R.id.mRelativePassword)
    RelativeLayout mRelativePassword;
    @BindView(R.id.imgMe)
    ImageView imgMe;
    @BindView(R.id.txtMe)
    TextView txtMe;
    @BindView(R.id.mRelativeMe)
    RelativeLayout mRelativeMe;
    @BindView(R.id.mLinearLayout1)
    LinearLayout mLinearLayout1;
    @BindView(R.id.imgReports)
    ImageView imgReports;
    @BindView(R.id.txtReports)
    TextView txtReports;
    @BindView(R.id.mRelativeReport)
    RelativeLayout mRelativeReport;
    @BindView(R.id.imgBalance)
    ImageView imgBalance;
    @BindView(R.id.Balance)
    TextView Balance;
    @BindView(R.id.mRelativeActivation)
    RelativeLayout mRelativeActivation;
    @BindView(R.id.imgLoadMoney)
    ImageView imgLoadMoney;
    @BindView(R.id.txtLoadMoney)
    TextView txtLoadMoney;
    @BindView(R.id.mRelativeLoadMoney)
    RelativeLayout mRelativeLoadMoney;
    @BindView(R.id.mLinearLayout2)
    LinearLayout mLinearLayout2;
    @BindView(R.id.mRelativeMain)
    RelativeLayout mRelativeMain;
//    @BindView(R.id.imgLogout)
//    ImageView imgLogout;
//    @BindView(R.id.toolbar_dashboard)
//    Toolbar toolbarDashboard;
    //    @BindView(R.id.imgName)
//    ImageView imgName;
//    @BindView(R.id.view)
//    View view;
//    @BindView(R.id.txtWallet)
//    TextView txtWallet;
//    @BindView(R.id.layout)
//    RelativeLayout layout;
//    @BindView(R.id.btnAddLocation)
//    Button btnAddLocation;
//    @BindView(R.id.btnScanCode)
//    Button btnScanCode;
//    @BindView(R.id.txtBalance)
//    TextView txtBalance;
//    @BindView(R.id.btnLoadMoeny)
//    Button btnLoadMoeny;
//    @BindView(R.id.txtSend)
//    TextView txtSend;
//    @BindView(R.id.mRalativeSend)
//    RelativeLayout mRalativeSend;
//    @BindView(R.id.txtPay)
//    TextView txtPay;
//    @BindView(R.id.mRelariveWithdrawal)
//    RelativeLayout mRelariveWithdrawal;
//    @BindView(R.id.layout_send)
//    LinearLayout layoutSend;
//    @BindView(R.id.txtAddress)
//    TextView txtAddress;
//    @BindView(R.id.mRelativeAddress)
//    RelativeLayout mRelativeAddress;
//    @BindView(R.id.txtPhoto)
//    TextView txtPhoto;
//    @BindView(R.id.mRelativePhoto)
//    RelativeLayout mRelativePhoto;
//    @BindView(R.id.layout_address)
//    LinearLayout layoutAddress;
//    @BindView(R.id.txtPassword)
//    TextView txtPassword;
//    @BindView(R.id.mRelativePassword)
//    RelativeLayout mRelativePassword;
//    @BindView(R.id.txtMe)
//    TextView txtMe;
//    @BindView(R.id.mRelativeMe)
//    RelativeLayout mRelativeMe;
//    @BindView(R.id.layout_ask)
//    LinearLayout layoutAsk;
//    @BindView(R.id.txtReports)
//    TextView txtReports;
//    @BindView(R.id.mRelativeReport)
//    RelativeLayout mRelativeReport;
//    @BindView(R.id.mLayoutReport)
//    LinearLayout mLayoutReport;
    private Toolbar mToolbarMemberDashboard;
//    private ImageView imgLogout;
    private TextView txtNo, txtYes,txtLogOut;
    public static String strMemberId,strBalance,strSpiltBlance;
    private String JsonResponse = "";
    public static final String PREFS_NAME = "MemberLoginPrefes";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_dashboard);
        ButterKnife.bind(this);
        getSupportActionBar().hide();

//        YoYo.with(Techniques.FlipInX)
//                .duration(2000)
//                .playOn(mRelativeMain);

        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        strMemberId = intent.getStringExtra("MemberId");
        Log.d(TAG, "member id: " + strMemberId);

        mToolbarMemberDashboard = (Toolbar) findViewById(R.id.mToolbarMemberDashboard);
//        imgLogout = (ImageView) mToolbarMemberDashboard.findViewById(R.id.imgLogout);
        txtLogOut=(TextView)mToolbarMemberDashboard.findViewById(R.id.txtLogOut);
//        mToolbarMemberDashboard.setNavigationIcon(R.drawable.back_icon);
//        mToolbarMemberDashboard.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        txtLogOut.setOnClickListener(new View.OnClickListener() {
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

        getWallterBalance();

    }

    private void getWallterBalance() {

        Map<String, String> jsonParams = new HashMap<String, String>();

        jsonParams.put("mode", "getMemberBalance");
        jsonParams.put("memberid", strMemberId);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, StringUtils.strBaseURL,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "json Response: " + response);
                        try {
                            String strStatus = response.getString("status");
                            JSONArray jsonArray = response.getJSONArray("response");
                            strBalance = jsonArray.getString(0);
                            Log.d(TAG, "member wallet balance: " + strBalance);
                            String [] strFloatValue=strBalance.split("\\.");
                            strSpiltBlance=strFloatValue[0];
                            Log.d(TAG, "strSplitvalues: "+strSpiltBlance);
                            txtBalance.setText("BALANCE " + strSpiltBlance);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Handle Error
                        Log.d(TAG, "onErrorResponse: " + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };
        requestQueue.add(postRequest);
    }

//    @OnClick({R.id.mRalativeSend, R.id.mRelariveWithdrawal, R.id.mRelativeAddress, R.id.mRelativePhoto, R.id.mRelativePassword, R.id.mRelativeMe, R.id.mRelativeReport})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.mRalativeSend:
//                Intent intent = new Intent(MemberDashboardActivity.this, MemberPaymentTransferActivtiy.class);
//                startActivity(intent);
//                break;
//            case R.id.mRelariveWithdrawal:
//                Intent intentWithdrawal = new Intent(MemberDashboardActivity.this, MemberPaymentTransferActivtiy.class);
//                intentWithdrawal.putExtra("tabindex", "2");
//                startActivity(intentWithdrawal);
//                break;
//            case R.id.mRelativeAddress:
//                break;
//            case R.id.mRelativePhoto:
//                break;
//            case R.id.mRelativePassword:
//                break;
//            case R.id.mRelativeMe:
//                Intent intentProfile = new Intent(MemberDashboardActivity.this, MemberEditProfileActivity.class);
//                startActivity(intentProfile);
//                break;
//            case R.id.mRelativeReport:
//                Intent intentReport = new Intent(MemberDashboardActivity.this, MemberReportActivity.class);
//                startActivity(intentReport);
//                break;
//        }
//    }

    @OnClick({R.id.mRelativeSend, R.id.mRelativeWithdrawal, R.id.mRelativeAddress, R.id.mRelativePhoto, R.id.mRelativePassword, R.id.mRelativeMe, R.id.mRelativeReport, R.id.mRelativeActivation, R.id.mRelativeLoadMoney, R.id.mRelativeMain})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mRelativeSend:
                Intent intent = new Intent(MemberDashboardActivity.this, MemberPaymentTransferActivtiy.class);
                startActivity(intent);
                break;
            case R.id.mRelativeWithdrawal:
                Intent intentWithdrawal = new Intent(MemberDashboardActivity.this, MemberPaymentTransferActivtiy.class);
                intentWithdrawal.putExtra("tabindex", "2");
                startActivity(intentWithdrawal);
                break;
            case R.id.mRelativeAddress:
                break;
            case R.id.mRelativePhoto:
                Intent intentImage=new Intent(MemberDashboardActivity.this,MemberShowVendorInformationActivity.class);
                startActivity(intentImage);
                break;
            case R.id.mRelativePassword:
                Intent intentChangePassword=new Intent(MemberDashboardActivity.this,MemberChangePasswordActivity.class);
                startActivity(intentChangePassword);
                break;
            case R.id.mRelativeMe:
                Intent intentProfile = new Intent(MemberDashboardActivity.this, MemberEditProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.mRelativeReport:
                Intent intentReport = new Intent(MemberDashboardActivity.this, MemberReportActivity.class);
                startActivity(intentReport);
                break;
            case R.id.mRelativeActivation:
                Intent intentActivation=new Intent(MemberDashboardActivity.this,MemberActivationActivity.class);
                startActivity(intentActivation);
                break;
            case R.id.mRelativeLoadMoney:
//                Intent intentMoney=new Intent(MemberDashboardActivity.this,MemberShowVendorImages.class);
//                startActivity(intentMoney);
                break;
            case R.id.mRelativeMain:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(MemberDashboardActivity.this, LoginTypeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        getWallterBalance();
    }
}
