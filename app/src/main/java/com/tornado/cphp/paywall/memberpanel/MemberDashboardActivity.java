package com.tornado.cphp.paywall.memberpanel;

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
import com.tornado.cphp.paywall.R;
import com.tornado.cphp.paywall.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.log.LoggerDefault;

public class MemberDashboardActivity extends AppCompatActivity {

    private static final String TAG = MemberDashboardActivity.class.getSimpleName();
    @BindView(R.id.imgName)
    ImageView imgName;
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
    @BindView(R.id.txtSend)
    TextView txtSend;
    @BindView(R.id.mRalativeSend)
    RelativeLayout mRalativeSend;
    @BindView(R.id.txtPay)
    TextView txtPay;
    @BindView(R.id.mRelariveWithdrawal)
    RelativeLayout mRelariveWithdrawal;
    @BindView(R.id.layout_send)
    LinearLayout layoutSend;
    @BindView(R.id.txtAddress)
    TextView txtAddress;
    @BindView(R.id.mRelativeAddress)
    RelativeLayout mRelativeAddress;
    @BindView(R.id.txtPhoto)
    TextView txtPhoto;
    @BindView(R.id.mRelativePhoto)
    RelativeLayout mRelativePhoto;
    @BindView(R.id.layout_address)
    LinearLayout layoutAddress;
    @BindView(R.id.txtPassword)
    TextView txtPassword;
    @BindView(R.id.mRelativePassword)
    RelativeLayout mRelativePassword;
    @BindView(R.id.txtMe)
    TextView txtMe;
    @BindView(R.id.mRelativeMe)
    RelativeLayout mRelativeMe;
    @BindView(R.id.layout_ask)
    LinearLayout layoutAsk;
    @BindView(R.id.txtReports)
    TextView txtReports;
    @BindView(R.id.mRelativeReport)
    RelativeLayout mRelativeReport;
    @BindView(R.id.mLayoutReport)
    LinearLayout mLayoutReport;
    private Toolbar mToolbarMemberDashboard;
    private ImageView imgLogout;
    private TextView txtNo, txtYes;
    public static String strMemberId;
    private String JsonResponse = "";
    public static final String PREFS_NAME = "MemberLoginPrefes";
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_dashboard);
        ButterKnife.bind(this);
        getSupportActionBar().hide();

        requestQueue= Volley.newRequestQueue(this);

        Intent intent = getIntent();
        strMemberId = intent.getStringExtra("MemberId");
        Log.d(TAG, "member id: " + strMemberId);

        mToolbarMemberDashboard = (Toolbar) findViewById(R.id.mToolbarMemberDashboard);
        imgLogout = (ImageView) mToolbarMemberDashboard.findViewById(R.id.imgLogout);
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

        getWallterBalance();


    }

    private void getWallterBalance() {

        Map<String,String> jsonParams=new HashMap<String,String>();

        jsonParams.put("mode","getMemberBalance");
        jsonParams.put("memberid",strMemberId);


        JsonObjectRequest postRequest = new JsonObjectRequest( Request.Method.POST, StringUtils.strBaseURL,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "json Response: "+response);
                        try {
                            String strStatus=response.getString("status");
                            JSONArray jsonArray=response.getJSONArray("response");
                            String strBalance=jsonArray.getString(0);
                            Log.d(TAG, "member wallet balance: "+strBalance);
                            txtBalance.setText("BALANCE "+strBalance);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try{

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Handle Error
                        Log.d(TAG, "onErrorResponse: "+error);
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

    @OnClick({R.id.mRalativeSend, R.id.mRelariveWithdrawal, R.id.mRelativeAddress, R.id.mRelativePhoto, R.id.mRelativePassword, R.id.mRelativeMe, R.id.mRelativeReport})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mRalativeSend:
                Intent intent=new Intent(MemberDashboardActivity.this,MemberPaymentTransferActivtiy.class);
                startActivity(intent);
                break;
            case R.id.mRelariveWithdrawal:
                Intent intentWithdrawal=new Intent(MemberDashboardActivity.this,MemberPaymentTransferActivtiy.class);
                intentWithdrawal.putExtra("tabindex","2");
                startActivity(intentWithdrawal);
                break;
            case R.id.mRelativeAddress:
                break;
            case R.id.mRelativePhoto:
                break;
            case R.id.mRelativePassword:
                break;
            case R.id.mRelativeMe:
                Intent intentProfile=new Intent(MemberDashboardActivity.this,MemberEditProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.mRelativeReport:
                Intent intentReport=new Intent(MemberDashboardActivity.this,MemberReportActivity.class);
                startActivity(intentReport);
                break;
        }
    }
}
