package com.tornado.cphp.paywall;

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

public class VendorRegistrationActivity extends AppCompatActivity {

    private static final String TAG = VendorRegistrationActivity.class.getSimpleName();
    private Toolbar toolbar_registration;
    private TextView txtTitle;

    private String JsonResponse = "",strCheckVendorResponse="";
    private ProgressDialog pd;
    private EditText etVendorId,etVendorName,etVendorEmail,etVendorMobile,etVendorPassword;
    private TextView txtRegister,txtEroorVendorId;
    private RelativeLayout layout_footer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().hide();

        toolbar_registration = (Toolbar) findViewById(R.id.toolbar_registration);
        txtTitle = (TextView) toolbar_registration.findViewById(R.id.txtTitle);
        txtTitle.setText("Registration");
        toolbar_registration.setNavigationIcon(R.drawable.back_icon);
        toolbar_registration.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etVendorId= (EditText) findViewById(R.id.etVendorId);
        etVendorName= (EditText) findViewById(R.id.etVendorName);
        etVendorEmail= (EditText) findViewById(R.id.etVendorEmail);
        etVendorMobile= (EditText) findViewById(R.id.etVendorMobile);
        etVendorPassword= (EditText) findViewById(R.id.etVendorPassword);

        txtRegister= (TextView) findViewById(R.id.txtRegister);
        txtEroorVendorId= (TextView) findViewById(R.id.txtEroorVendorId);
        layout_footer= (RelativeLayout) findViewById(R.id.layout_footer);
        layout_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRegistrationData();
            }
        });

        etVendorId.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText;
//                txtEroorVendorId.setVisibility(View.VISIBLE);
                if (!hasFocus){
                    editText=(EditText)v;
                    String strVendorId=editText.getText().toString();
                    Log.d(TAG, "vendor id: "+strVendorId);
                    if (strVendorId.equals("")){
                        txtEroorVendorId.setVisibility(View.GONE);
                    }

                    JSONObject jsonObject=new JSONObject();
                    try{
                        jsonObject.put("mode","checkVendorid");
                        jsonObject.put("vendorid",strVendorId);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if (jsonObject.length()>0){
                        new checkVendorId().execute(String.valueOf(jsonObject));
                    }

                }
            }
        });

//        txtRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getRegistrationData();
//            }
//        });

    }


    private void getRegistrationData() {
        String strVendorId = etVendorId.getText().toString().trim();
        String strVendorName = etVendorName.getText().toString().trim();
        String strEmailId = etVendorEmail.getText().toString().trim();
        String strMobileNumber = etVendorMobile.getText().toString().trim();
        String strPassword = etVendorPassword.getText().toString().trim();


        if (StringUtils.isBlank(strVendorId)) {
            Toast.makeText(VendorRegistrationActivity.this, "Please Enter Vendor Id", Toast.LENGTH_SHORT).show();
        }else if (txtEroorVendorId.getVisibility()==View.VISIBLE){
            Toast.makeText(VendorRegistrationActivity.this, "Vendor id Is Already Used", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strVendorName)) {
            Toast.makeText(VendorRegistrationActivity.this, "Please Enter Vendor Name", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strEmailId)) {
            Toast.makeText(VendorRegistrationActivity.this, "Please Enter Email Id", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strMobileNumber)) {
            Toast.makeText(VendorRegistrationActivity.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strPassword)) {
            Toast.makeText(VendorRegistrationActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        } else {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mode", "vendorJoining");
                jsonObject.put("vendorid", strVendorId);
                jsonObject.put("password", strPassword);
                jsonObject.put("name", strVendorName);
                jsonObject.put("email", strEmailId);
                jsonObject.put("mobile", strMobileNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jsonObject.length() > 0) {
                new postRegistrationData().execute(String.valueOf(jsonObject));
            }
        }
    }

    private class postRegistrationData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(VendorRegistrationActivity.this);
            pd.setIndeterminate(false);
            pd.setMessage("Please wait....");
            pd.setCancelable(false);
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


            if (pd.isShowing()) {
                pd.dismiss();
            }

            try {
                JSONObject Object = new JSONObject(JsonResponse);
                String strStatus = Object.getString("status");
                String strMessage = Object.getString("message");
                JSONArray jsonArray = Object.getJSONArray("response");
                String strResult = jsonArray.getString(0);
                Log.d(TAG, "result respose: " + strResult);
                Toast.makeText(VendorRegistrationActivity.this, strResult, Toast.LENGTH_SHORT).show();
                if (strStatus.equals("1")) {
                    etVendorId.getText().clear();
                    etVendorName.getText().clear();
                    etVendorEmail.getText().clear();
                    etVendorMobile.getText().clear();
                    etVendorPassword.getText().clear();
                    Intent intentLogin=new Intent(VendorRegistrationActivity.this,VendorLoginActivity.class);
                    startActivity(intentLogin);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class checkVendorId extends AsyncTask<String,String,String> {

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
                strCheckVendorResponse = buffer.toString();
                Log.i(TAG, strCheckVendorResponse);
                //send to post execute
                return strCheckVendorResponse;
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

            return strCheckVendorResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                JSONObject Object = new JSONObject(strCheckVendorResponse);
                String strStatus = Object.getString("status");
                String strMessage = Object.getString("message");
                if (strStatus.equals("1")){
                    txtEroorVendorId.setVisibility(View.VISIBLE);
                }else {
                    txtEroorVendorId.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
