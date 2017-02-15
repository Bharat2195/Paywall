package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class MemberForgotPassword extends AppCompatActivity {

    private static final String TAG=MemberForgotPassword.class.getSimpleName();
    private EditText etMobileNumber;
    private TextView txtMessage,txtTitle;
    private Button btnOk,btnLogin;

    private Toolbar toolbar_forgot;

    private String strJsonResponse;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_forgot_password);
        getSupportActionBar().hide();

        etMobileNumber=(EditText)findViewById(R.id.etMobileNumber);

        txtMessage=(TextView)findViewById(R.id.txtMessage);

        btnOk=(Button)findViewById(R.id.btnOk);


        toolbar_forgot=(Toolbar)findViewById(R.id.toolbar_forgot);
        btnLogin=(Button)findViewById(R.id.btnLogin);
        txtTitle= (TextView) toolbar_forgot.findViewById(R.id.txtTitle);
        txtTitle.setText("FORGOT PASSWORD");

        toolbar_forgot.setNavigationIcon(R.drawable.back_icon);
        toolbar_forgot.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin=new Intent(MemberForgotPassword.this,MemberLoginActivity.class);
                startActivity(intentLogin);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strMobileNumber=etMobileNumber.getText().toString();

                if (StringUtils.isBlank(strMobileNumber)){
                    Toast.makeText(MemberForgotPassword.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }else {
                    JSONObject jsonObject=new JSONObject();

                    try{

                        jsonObject.put("mode","forgotPassword");
                        jsonObject.put("type","Member");
                        jsonObject.put("memberid",strMobileNumber);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (jsonObject.length()>0){
                        new getPassword().execute(String.valueOf(jsonObject));
                    }
                }

            }
        });
    }

    private class getPassword extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(MemberForgotPassword.this);
            pd.setMessage("Please Wait...");
            pd.setIndeterminate(false);
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
                JSONObject jsonObject = new JSONObject(strJsonResponse);
                String strStatus = jsonObject.getString("status");
                String strMessage = jsonObject.getString("message");
                String strResponse= jsonObject.getString("response");
                if (strStatus.equalsIgnoreCase("1")){
                    txtMessage.setVisibility(View.VISIBLE);
                }else {
                    txtMessage.setVisibility(View.GONE);
                    JSONArray jsonArray=jsonObject.getJSONArray("response");
                    String Response=jsonArray.getString(0);
                    Toast.makeText(MemberForgotPassword.this,Response , Toast.LENGTH_SHORT).show();
                }

//
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
