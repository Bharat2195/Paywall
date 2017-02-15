package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class MemberHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MemberHomeActivity.class.getSimpleName();
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

    private Toolbar mToolbarMemberDashboard;
    //    private ImageView imgLogout;
    private TextView txtNo, txtYes, txtLogOut;
    public static String strMemberId, strBalance, strSpiltBlance;
    private String JsonResponse = "";
    public static final String PREFS_NAME = "MemberLoginPrefes";
    RequestQueue requestQueue;
    private TextView txtMemberHeaderTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_home);
        ButterKnife.bind(this);
//        YoYo.with(Techniques.FlipInX)
//                .duration(2000)
//                .playOn(mRelativeMain);

        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        strMemberId = intent.getStringExtra("MemberId");
        Log.d(TAG, "member id: " + strMemberId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        YoYo.with(Techniques.Landing)
                .duration(2000)
                .playOn(mRelativeMain);

        txtLogOut = (TextView) toolbar.findViewById(R.id.txtLogOut);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        txtLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MemberHomeActivity.this);
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
                        Intent intent = new Intent(MemberHomeActivity.this, MemberLoginActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View viewMemberHeader = navigationView.getHeaderView(0);
        txtMemberHeaderTitle = (TextView) viewMemberHeader.findViewById(R.id.txtMemberHeaderTitle);
        txtMemberHeaderTitle.setText(strMemberId);

        getWallterBalance();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            Intent intent = new Intent(MemberHomeActivity.this, LoginTypeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
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
                            String[] strFloatValue = strBalance.split("\\.");
                            strSpiltBlance = strFloatValue[0];
                            Log.d(TAG, "strSplitvalues: " + strSpiltBlance);
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

    @OnClick({R.id.mRelativeSend, R.id.mRelativeWithdrawal, R.id.mRelativeAddress, R.id.mRelativePhoto, R.id.mRelativePassword, R.id.mRelativeMe, R.id.mRelativeReport, R.id.mRelativeActivation, R.id.mRelativeLoadMoney, R.id.mRelativeMain})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mRelativeSend:
                Intent intent = new Intent(MemberHomeActivity.this, MemberPaymentTransferActivtiy.class);
                startActivity(intent);
                break;
            case R.id.mRelativeWithdrawal:
                Intent intentWithdrawal = new Intent(MemberHomeActivity.this, MemberPaymentTransferActivtiy.class);
                intentWithdrawal.putExtra("tabindex", "2");
                startActivity(intentWithdrawal);
                break;
            case R.id.mRelativeAddress:
                Toast.makeText(MemberHomeActivity.this, "Coming Soon...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mRelativePhoto:
                Intent intentImage = new Intent(MemberHomeActivity.this, MemberShowVendorInformationActivity.class);
                startActivity(intentImage);
                break;
            case R.id.mRelativePassword:
                Intent intentChangePassword = new Intent(MemberHomeActivity.this, MemberChangePasswordActivity.class);
                startActivity(intentChangePassword);
                break;
            case R.id.mRelativeMe:
                Intent intentProfile = new Intent(MemberHomeActivity.this, MemberEditProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.mRelativeReport:
                Intent intentReport = new Intent(MemberHomeActivity.this, MemberReportActivity.class);
                startActivity(intentReport);
                break;
            case R.id.mRelativeActivation:
                Intent intentActivation = new Intent(MemberHomeActivity.this, MemberActivationActivity.class);
                startActivity(intentActivation);
                break;
            case R.id.mRelativeLoadMoney:
                Intent intentRecharge = new Intent(MemberHomeActivity.this, MemberRechargeActivity.class);
                startActivity(intentRecharge);
                break;
            case R.id.mRelativeMain:
                break;
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.member_home, menu);
//        return true;
//    }
//
//    @Overridenot
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            // Handle the camera action
        } else if (id == R.id.nav_image) {
            Intent intentImage = new Intent(MemberHomeActivity.this, MemberShowVendorInformationActivity.class);
            startActivity(intentImage);

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_report) {
            Intent intentReport = new Intent(MemberHomeActivity.this, MemberReportActivity.class);
            startActivity(intentReport);

        } else if (id == R.id.nav_qrcode) {
            Intent intentQRCode = new Intent(MemberHomeActivity.this, MemberPaymentTransferActivtiy.class);
            startActivity(intentQRCode);


        } else if (id == R.id.nav_Address) {

        } else if (id == R.id.nav_mobile) {
            Intent intent=new Intent(MemberHomeActivity.this,MemberMobileNumberToPay.class);
            startActivity(intent);

        } else if (id == R.id.nav_password) {
            Intent intentPassword = new Intent(MemberHomeActivity.this, MemberChangePasswordActivity.class);
            startActivity(intentPassword);

        } else if (id == R.id.nav_activation) {
            Intent intentActivivation = new Intent(MemberHomeActivity.this, MemberActivationActivity.class);
            startActivity(intentActivivation);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
