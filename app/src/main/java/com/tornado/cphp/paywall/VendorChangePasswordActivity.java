package com.tornado.cphp.paywall;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class VendorChangePasswordActivity extends AppCompatActivity {

    private static final String TAG=VendorUploadImageActivity.class.getSimpleName();

    private Toolbar toolbar_change_password;
    private TextView txtTitle;
    private EditText etChangeOldPassword, etChangeNewPassword, etChangeConfirmPassword;
    private Button btnChangeUpadateNow;
    String strChangePwdRespose="",strChangePwdMessage="",strOldPassword = "", strNewPassword = "", strConfirmPassword = "", strMemberid = "",JsonResponse="",strMessagge ="",strChangePasswordResponse="";
    TextView txtSignIn,txtErrorMsgOldPassword,txtErrorMsgConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_change_password);
        getSupportActionBar().hide();


        toolbar_change_password= (Toolbar) findViewById(R.id.toolbar_change_password);
        txtTitle=(TextView)toolbar_change_password.findViewById(R.id.txtTitle);
        txtTitle.setText("Change Password");
        toolbar_change_password.setNavigationIcon(R.drawable.back_icon);
        toolbar_change_password.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txtErrorMsgOldPassword=(TextView)findViewById(R.id.txtErrorMsgOldPassword);
        txtErrorMsgConfirmPassword=(TextView)findViewById(R.id.txtErrorMsgConfirmPassword);
        etChangeOldPassword= (EditText)findViewById(R.id.etChangeOldPassword);
        etChangeNewPassword= (EditText)findViewById(R.id.etChangeNewPassword);
        etChangeConfirmPassword= (EditText)findViewById(R.id.etChangeConfirmPassword);
        btnChangeUpadateNow= (Button)findViewById(R.id.btnChangeUpadateNow);

        btnChangeUpadateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strOldPassword = etChangeOldPassword.getText().toString();
                strNewPassword = etChangeNewPassword.getText().toString();
                strConfirmPassword = etChangeConfirmPassword.getText().toString();


                if (StringUtils.isBlank(strOldPassword)) {
                    showToast("Please Enter OldPassword");
                }else if (StringUtils.isBlank(strNewPassword)) {
                    showToast("Please Enter News Password");
                }else if (StringUtils.isBlank(strConfirmPassword)) {
                    showToast("Please Enter ConfirmPassword");
                }else if (!strConfirmPassword.equals(strNewPassword)){
//                    showToast("Both Password don't match");
                    txtErrorMsgConfirmPassword.setVisibility(View.VISIBLE);
                }else if (strConfirmPassword.equals(strNewPassword)){
                    txtErrorMsgConfirmPassword.setVisibility(View.GONE);
                    if (strMessagge.equals("Success")){
                        JSONObject jsonObject=new JSONObject();
                        try{
                            jsonObject.put("mode","vendorChangepasswordSubmit");
                            jsonObject.put("vendorid", VendorMainActivity.strVendorId);
                            jsonObject.put("oldpassword",strOldPassword);
                            jsonObject.put("newpassword",strNewPassword);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if (jsonObject.length()>0){
                            new Postdata().execute(String.valueOf(jsonObject));
                        }
                    }else {
                        showToast("Please Enter Correct Old Password");
                    }

                }
            }
        });

        etChangeOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText;

                if (!hasFocus){
                    JSONObject jsonObject=new JSONObject();
                    try{
                        editText=(EditText)v;
                        String oldPassword=editText.getText().toString();
                        Log.d(TAG,"MemberId:"+oldPassword);
                        jsonObject.put("mode","vendorOldPasswordCheck");
                        jsonObject.put("vendorid",VendorMainActivity.strVendorId);
                        jsonObject.put("oldpassword", oldPassword);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if (jsonObject.length()>0){
                        new checkPassword().execute(String.valueOf(jsonObject));
                    }
                }
            }
        });


    }

    private class Postdata  extends AsyncTask<String,String,String> {
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
                strChangePasswordResponse = buffer.toString();
                Log.i(TAG, strChangePasswordResponse);
                //send to post execute
                return strChangePasswordResponse;
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

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(strChangePasswordResponse);
                String strStatus = jsonObject.getString("status");
                Log.d(TAG, "status: "+strStatus);
                strChangePasswordResponse = jsonObject.getString("message");
                JSONArray jsonArray=jsonObject.getJSONArray("response");
                String strResponse=jsonArray.getString(0);
                Toast.makeText(VendorChangePasswordActivity.this, strResponse, Toast.LENGTH_SHORT).show();
                if (strStatus.equals(1)){
                    etChangeOldPassword.getText().clear();
                    etChangeNewPassword.getText().clear();
                    etChangeConfirmPassword.getText().clear();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class checkPassword  extends AsyncTask<String,String,String> {
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

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(JsonResponse);
                String strStatus = jsonObject.getString("status");
                strMessagge = jsonObject.getString("message");
                JSONArray jsonArray=jsonObject.getJSONArray("response");
                String strResponse=jsonArray.getString(0);

                if (strStatus.equals("1")){
                    txtErrorMsgOldPassword.setVisibility(View.GONE);
                }else {
                    txtErrorMsgOldPassword.setVisibility(View.VISIBLE);
                    etChangeNewPassword.setClickable(false);
                    etChangeConfirmPassword.setClickable(false);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    private void showToast(String strMessage){
        Toast toast=Toast.makeText(VendorChangePasswordActivity.this,strMessage, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

    }
}
