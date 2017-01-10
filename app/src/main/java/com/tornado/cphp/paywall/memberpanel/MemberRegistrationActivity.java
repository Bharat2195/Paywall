package com.tornado.cphp.paywall.memberpanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tornado.cphp.paywall.R;
import com.tornado.cphp.paywall.utils.StringUtils;

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

public class MemberRegistrationActivity extends AppCompatActivity {

    private static final String TAG = MemberRegistrationActivity.class.getSimpleName();
    @BindView(R.id.etMemberPassword)
    EditText etMemberPassword;

    private ProgressDialog pd;
    private String strSponsorIdCheckResponse = "", strJsonResponse = "";
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.etSponsorId)
    EditText etSponsorId;
    @BindView(R.id.txtEroorSponsorId)
    TextView txtEroorSponsorId;
    @BindView(R.id.etSponsorName)
    EditText etSponsorName;
    @BindView(R.id.etMemberName)
    EditText etMemberName;
    @BindView(R.id.etMobile)
    EditText etMobile;
    @BindView(R.id.etAddress)
    EditText etAddress;
    @BindView(R.id.etCity)
    EditText etCity;
    @BindView(R.id.etState)
    EditText etState;
    @BindView(R.id.etCountry)
    EditText etCountry;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etMemberid)
    EditText etMemberid;
    @BindView(R.id.txtRegister)
    TextView txtRegister;
    @BindView(R.id.mLayoutRegistration)
    RelativeLayout mLayoutRegistration;
    @BindView(R.id.activity_registration)
    RelativeLayout activityRegistration;
    private Toolbar mToolbarMemberRegistration;
    private TextView txtTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_registration);
        ButterKnife.bind(this);
        getSupportActionBar().hide();

        mToolbarMemberRegistration = (Toolbar) findViewById(R.id.mToolbarMemberRegistration);
        txtTitle = (TextView) mToolbarMemberRegistration.findViewById(R.id.txtTitle);
        txtTitle.setText("Member Resgistration");
        mToolbarMemberRegistration.setNavigationIcon(R.drawable.back_icon);
        mToolbarMemberRegistration.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etSponsorId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            EditText editText;

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    editText = (EditText) v;
                    String strSponsorId = editText.getText().toString();
                    Log.d(TAG, "strSponsorid: " + strSponsorId);

                    if (strSponsorId.equals("")) {
                        txtEroorSponsorId.setVisibility(View.GONE);
                    }
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("mode", "CheckSponsorid");
                        jsonObject.put("sponsorid", strSponsorId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (jsonObject.length() > 0) {
                        new checkSponsorId().execute(String.valueOf(jsonObject));
                    }
                }
            }
        });

    }


    @OnClick(R.id.mLayoutRegistration)
    public void onClick() {


        String strSponsorId = etSponsorId.getText().toString();
        String strSponsorName = etSponsorName.getText().toString();
        String strMemberId = etMemberid.getText().toString();
        String strPassword=etMemberPassword.getText().toString();
        String strMemberName = etMemberName.getText().toString();
        String strMobile = etMobile.getText().toString();
        String strEmail = etEmail.getText().toString();
        String strAddress = etAddress.getText().toString();
        String strCity = etCity.getText().toString();
        String strState = etState.getText().toString();
        String strCountry = etCountry.getText().toString();

        if (StringUtils.isBlank(strSponsorId)) {
            Toast.makeText(MemberRegistrationActivity.this, "Please enter sponsorid", Toast.LENGTH_SHORT).show();
        } else if (txtEroorSponsorId.getVisibility() == View.VISIBLE) {
            Toast.makeText(MemberRegistrationActivity.this, "SponsorId Is Invalid", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strSponsorName)) {
            Toast.makeText(MemberRegistrationActivity.this, "Please enter sponsor name", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strMemberId)) {
            Toast.makeText(MemberRegistrationActivity.this, "Please enter Member Id", Toast.LENGTH_SHORT).show();
        }else if (StringUtils.isBlank(strPassword)){
            Toast.makeText(MemberRegistrationActivity.this, "Please enter Member Passowrd", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strMemberName)) {
            Toast.makeText(MemberRegistrationActivity.this, "Please enter Member Name", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strMobile)) {
            Toast.makeText(MemberRegistrationActivity.this, "Please enter Mobile Number", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strEmail)) {
            Toast.makeText(MemberRegistrationActivity.this, "Please enter Email Address", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strAddress)) {
            Toast.makeText(MemberRegistrationActivity.this, "Please enter Address", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strCity)) {
            Toast.makeText(MemberRegistrationActivity.this, "Please enter City", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strState)) {
            Toast.makeText(MemberRegistrationActivity.this, "Please enter State", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strCountry)) {
            Toast.makeText(MemberRegistrationActivity.this, "Please enter Country", Toast.LENGTH_SHORT).show();
        } else {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mode", "memberJoining");
                jsonObject.put("memberid", strMemberId);
                jsonObject.put("password", strPassword);
                jsonObject.put("membername",strMemberName);
                jsonObject.put("mobile",strMobile);
                jsonObject.put("email",strEmail);
                jsonObject.put("sponsorid",strSponsorId);
                jsonObject.put("address",strAddress);
                jsonObject.put("city",strCity);
                jsonObject.put("state",strState);
                jsonObject.put("country",strCountry);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jsonObject.length()>0){
                new sumbitRegisterData().execute(String.valueOf(jsonObject));
            }

        }
    }

    private class checkSponsorId extends AsyncTask<String, String, String> {

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
                strSponsorIdCheckResponse = buffer.toString();
                Log.i(TAG, strSponsorIdCheckResponse);
                //send to post execute
                return strSponsorIdCheckResponse;
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

            return strSponsorIdCheckResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                JSONObject Object = new JSONObject(strSponsorIdCheckResponse);
                String strStatus = Object.getString("status");
                String strMessage = Object.getString("message");
                if (strStatus.equals("2")) {
                    txtEroorSponsorId.setVisibility(View.VISIBLE);
                } else {
                    txtEroorSponsorId.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class sumbitRegisterData extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(MemberRegistrationActivity.this);
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.setMessage("Please Wait.......");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

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
                strJsonResponse = buffer.toString();
                Log.i(TAG, strJsonResponse);
                //send to post execute
                return strJsonResponse;
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

            return strJsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (pd.isShowing()){
                pd.dismiss();
            }

            try {
                JSONObject Object = new JSONObject(strJsonResponse);
                String strStatus = Object.getString("status");
                String strMessage = Object.getString("message");
                JSONArray jsonArray=Object.getJSONArray("response");
                String strResponse=jsonArray.getString(0);
                Toast.makeText(MemberRegistrationActivity.this, strResponse, Toast.LENGTH_SHORT).show();
                if (strStatus.equals("3")){
                    etMemberid.getText().clear();
                    etMemberPassword.getText().clear();
                    etMemberName.getText().clear();
                    etMobile.getText().clear();
                    etEmail.getText().clear();
                    etSponsorId.getText().clear();
                    etAddress.getText().clear();
                    etCity.getText().clear();
                    etState.getText().clear();
                    etCountry.getText().clear();
                    Intent intentActivation=new Intent(MemberRegistrationActivity.this,MemberLoginActivity.class);
                    startActivity(intentActivation);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

   /* @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }*/
}
