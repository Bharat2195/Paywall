package com.tornado.cphp.awhitepaid.vendorpanel;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VendorMainActivity extends AppCompatActivity {

    private static final String TAG = VendorMainActivity.class.getSimpleName();
    @BindView(R.id.imgName)
    ImageView imgName;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.txtWallet)
    TextView txtWallet;
    //    @BindView(R.id.imgLogout)
//    ImageView imgLogout;
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
    @BindView(R.id.mLinearLayoutWallet)
    LinearLayout mLinearLayoutWallet;
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
    @BindView(R.id.mRelativeBalance)
    RelativeLayout mRelativeBalance;
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
    //    @BindView(R.id.mLayoutMain)
//    RelativeLayout mLayoutMain;
    private Toolbar toolbar_main;
    private TextView txtTitle,txtLogOut;
    private ImageView imgProfile, imgLogout;
    public static String strVendorId = "";
    //    private Button btnAddLocation, btnScanCode;
//    private TextView txtBalance;
//    private Button btnLoadMoeny;
    public static String strWalletBalance = "",strSpitValue="";
    private String JsonResponse = "", strTDS = "";
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 99;
    public static final String PREFS_NAME = "LoginPrefes";
    private TextView txtNo, txtYes;

//    private RelativeLayout mRalativeSend, mRelariveWithdrawal, mRelativeAddress, mRelativePhoto, mRelativePassword, mRelativeMe, mRelativeReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dashboard_vendor);
        ButterKnife.bind(this);

        getSupportActionBar().hide();
        Intent intent = getIntent();
        strVendorId = intent.getStringExtra("VendorId");
        toolbar_main = (Toolbar) findViewById(R.id.toolbar_main);
        Log.d(TAG, "vendor id: " + strVendorId);
        txtLogOut=(TextView)toolbar_main.findViewById(R.id.txtLogOut);
//        imgLogout = (ImageView) toolbar_main.findViewById(R.id.imgLogout);

        YoYo.with(Techniques.Landing)
                .duration(2000)
                .playOn(mRelativeMain);
        txtLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(VendorMainActivity.this);
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
                        Intent intent = new Intent(VendorMainActivity.this, VendorLoginActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission();
        }

//        txtBalance = (TextView) findViewById(R.id.txtBalance);
//        btnLoadMoeny = (Button) findViewById(R.id.btnLoadMoeny);

//        mRalativeSend = (RelativeLayout) findViewById(R.id.mRalativeSend);
//        mRelariveWithdrawal = (RelativeLayout) findViewById(R.id.mRelariveWithdrawal);
//        mRelativeAddress = (RelativeLayout) findViewById(R.id.mRelativeAddress);
//        mRelativePhoto = (RelativeLayout) findViewById(R.id.mRelativePhoto);
//        mRelativePassword = (RelativeLayout) findViewById(R.id.mRelativePassword);
//        mRelativeMe = (RelativeLayout) findViewById(R.id.mRelativeMe);
//        mRelativeReport = (RelativeLayout) findViewById(R.id.mRelativeReport);

        getWalletBalance();
//        mRalativeSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentSend = new Intent(VendorMainActivity.this, PaySendMoneyVendorActivity.class);
//                startActivity(intentSend);
//            }
//        });

//        mRelativeMe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentProfile = new Intent(VendorMainActivity.this, VendorEdiProfileActivity.class);
//                startActivity(intentProfile);
//            }
//        });

//        mRelativePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentPhoto = new Intent(VendorMainActivity.this, VendorUploadImageActivity.class);
//                startActivity(intentPhoto);
//            }
//        });

//        mRelativeAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentAddress = new Intent(VendorMainActivity.this, AddAddressMapsActivity.class);
//                startActivity(intentAddress);
//            }
//        });


//        mRelativePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentPassword = new Intent(VendorMainActivity.this, VendorChangePasswordActivity.class);
//                startActivity(intentPassword);
//            }
//        });

//        mRelariveWithdrawal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentWitdrawal = new Intent(VendorMainActivity.this, PaySendMoneyVendorActivity.class);
//                intentWitdrawal.putExtra("tabindex", "2");
//                startActivity(intentWitdrawal);
//            }
//        });
//        mRelativeReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentReport = new Intent(VendorMainActivity.this, VendorReportsActivity.class);
//                startActivity(intentReport);
//            }
//        });
//        btnAddLocation=(Button)findViewById(R.id.btnAddLocation);
//        btnScanCode=(Button)findViewById(R.id.btnScanCode);
//        btnAddLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentLocation=new Intent(VendorMainActivity.this,AddAddressMapsActivity.class);
//                startActivity(intentLocation);
//            }
//        });


//        imgProfile=(ImageView)toolbar_main.findViewById(R.id.imgProfile);
//        imgProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(VendorMainActivity.this,VendorEdiProfileActivity.class);
//                startActivity(intent);
//            }
//        });

//        btnScanCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentScanCode=new Intent(VendorMainActivity.this,QRScanCodeActivity.class);
//                startActivity(intentScanCode);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(VendorMainActivity.this,LoginTypeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        Intent intenBackPressed = new Intent(Intent.ACTION_MAIN);
//        intenBackPressed.addCategory(Intent.CATEGORY_HOME);
//        intenBackPressed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intenBackPressed);
//        finish();
//        System.exit(0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        getWalletBalance();
    }

    public boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
            return false;
        } else {
            return true;
        }
    }


    private void getWalletBalance() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mode", "getVendorBalance");
            jsonObject.put("vendorid", strVendorId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonObject.length() > 0) {
            new getwalletBalance().execute(String.valueOf(jsonObject));
        }
    }

    @OnClick({R.id.btnAddLocation, R.id.btnLoadMoeny, R.id.mRelativeSend, R.id.mRelativeWithdrawal, R.id.mRelativeAddress, R.id.mRelativePhoto, R.id.mRelativePassword, R.id.mRelativeMe, R.id.mRelativeReport, R.id.mRelativeBalance, R.id.mRelativeLoadMoney})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddLocation:
                break;
            case R.id.btnLoadMoeny:
                break;
            case R.id.mRelativeSend:
                Intent intentSend = new Intent(VendorMainActivity.this, PaySendMoneyVendorActivity.class);
                startActivity(intentSend);
                break;
            case R.id.mRelativeWithdrawal:
                Intent intentWitdrawal = new Intent(VendorMainActivity.this, PaySendMoneyVendorActivity.class);
                intentWitdrawal.putExtra("tabindex", "2");
                startActivity(intentWitdrawal);
                break;
            case R.id.mRelativeAddress:
                Intent intentAddress = new Intent(VendorMainActivity.this, AddAddressMapsActivity.class);
                startActivity(intentAddress);
                break;
            case R.id.mRelativePhoto:
                Intent intentPhoto = new Intent(VendorMainActivity.this, VendorUploadImageActivity.class);
                startActivity(intentPhoto);
                break;
            case R.id.mRelativePassword:
                Intent intentPassword = new Intent(VendorMainActivity.this, VendorChangePasswordActivity.class);
                startActivity(intentPassword);
                break;
            case R.id.mRelativeMe:
                Intent intentProfile = new Intent(VendorMainActivity.this, VendorEdiProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.mRelativeReport:
                Intent intentReport = new Intent(VendorMainActivity.this, VendorReportsActivity.class);
                startActivity(intentReport);
                break;
            case R.id.mRelativeBalance:
                break;
            case R.id.mRelativeLoadMoney:
                break;
        }
    }

    private class getwalletBalance extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(StringUtils.strBaseURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
// json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    return null;
                }
                JsonResponse = buffer.toString();
                Log.i(TAG, JsonResponse);
                //send to post execute
                return JsonResponse;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            return JsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                JSONObject Object = new JSONObject(JsonResponse);
                String strStutus = Object.getString("status");
                Log.d(TAG, "status: " + strStutus);
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);

                JSONArray jsonArray = Object.getJSONArray("response");
                strWalletBalance = jsonArray.getString(0);
                Log.d(TAG, "array response: " + strWalletBalance);
                String [] strFloatBalance=strWalletBalance.split("\\.");
                strSpitValue=strFloatBalance[0];
                Log.d(TAG, "strSpilt wallter balance: "+strSpitValue);
                txtBalance.setText("BALANCE " + strSpitValue);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

}
