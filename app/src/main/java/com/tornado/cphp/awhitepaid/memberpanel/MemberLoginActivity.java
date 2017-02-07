package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tornado.cphp.awhitepaid.LoginTypeActivity;
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

import pl.droidsonroids.gif.GifImageView;

public class MemberLoginActivity extends AppCompatActivity {

    private static final String TAG = MemberLoginActivity.class.getSimpleName();
    private String JsonResponse = "",MemberId="";
    public static final String PREFS_NAME = "MemberLoginPrefes";
    private EditText etMemberid,etPassword;
    private Button btnLogin;
    private TextView txtSignIn,txtRegister;
    GifImageView gifLoading;
    JSONObject jsonObject;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_login);

        getSupportActionBar().hide();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        if (preferences.getString("logged", "").toString().equals("logged")) {
            Intent intent = new Intent(MemberLoginActivity.this, MemberHomeActivity.class);
            intent.putExtra("MemberId", preferences.getString("MemberId", "").toString());
            startActivity(intent);
        }

        etMemberid=(EditText)findViewById(R.id.etMemberid);
        etPassword=(EditText)findViewById(R.id.etPassword);
        btnLogin=(Button) findViewById(R.id.btnLogin);
        gifLoading=(GifImageView)findViewById(R.id.gifLoading);
        txtRegister=(TextView)findViewById(R.id.txtRegister);
        txtSignIn=(TextView)findViewById(R.id.txtSignIn);

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignIn=new Intent(MemberLoginActivity.this,MemberRegistrationActivity.class);
                startActivity(intentSignIn);
            }
        });


        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegiter=new Intent(MemberLoginActivity.this,MemberRegistrationActivity.class);
                startActivity(intentRegiter);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLogin();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(MemberLoginActivity.this, LoginTypeActivity.class);
        startActivity(intent);
    }

    private void getLogin() {

        String strUserName = etMemberid.getText().toString().trim();
        String strPassword = etPassword.getText().toString().trim();

        if (StringUtils.isBlank(strUserName)) {
            Toast.makeText(MemberLoginActivity.this, "Pleae Enter MemberId", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strPassword)) {
            Toast.makeText(MemberLoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        } else {

            jsonObject = new JSONObject();
            try {
                jsonObject.put("mode", "memberLogin");
                jsonObject.put("memberid", strUserName);
                jsonObject.put("password", strPassword);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jsonObject.length() > 0) {
                new GetLogin().execute(String.valueOf(jsonObject));

            }

        }

    }

    private class GetLogin extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(MemberLoginActivity.this);
            pd.setIndeterminate(false);
            pd.setMessage("Please wait....");
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();

//            gifLoading.setVisibility(View.VISIBLE);
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


            if (pd.isShowing()){
                pd.dismiss();
            }

            try {
                JSONObject Object = new JSONObject(JsonResponse);
                String strStutus=Object.getString("status");
                Log.d(TAG, "status: "+strStutus);
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);
                if (strStutus.equalsIgnoreCase("1")){
                    JSONArray jsonArray=Object.getJSONArray("response");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        MemberId=jsonObject1.getString("memberid");
                        Log.d(TAG, "member Id: "+MemberId);
                        String strPassword=jsonObject1.getString("password");
                        Log.d(TAG, "strPassword: "+strPassword);
                        Toast.makeText(MemberLoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("logged", "logged");
                        editor.putString("MemberId", MemberId);
                        editor.commit();
                        getActivation();

                    }
                }else {
                    JSONArray jsonArray=Object.getJSONArray("response");
                    String strJsonArray=jsonArray.getString(0);
                    Log.d(TAG, "json arrat: "+strJsonArray);
                    Toast.makeText(MemberLoginActivity.this, strJsonArray, Toast.LENGTH_SHORT).show();

                }
//                JSONArray jsonArray = Object.getJSONArray("response");
//                for (int i=0; i<jsonArray.length(); i++){
//                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
//                    VendorId=jsonObject1.getString("vendorid");
//                    Log.d(TAG, "vendor id: "+VendorId);
//                    String Password=jsonObject1.getString("password");
//                    Log.d(TAG, "Password: "+Password);
//                }
//                String strSucess = jsonArray.getString(1);
//                Log.d(TAG, "success message: "+strSucess);
//                Toast.makeText(VendorLoginActivity.this, strSucess, Toast.LENGTH_SHORT).show();
//                if (strStutus.equals("1")){
//                    Intent intent=new Intent(VendorLoginActivity.this,VendorMainActivity.class);
//                    intent.putExtra("strVendorId",VendorId);
//                    startActivity(intent);
//                }
//                Log.d(TAG, "login response: " + strSucess);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getActivation() {


        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("mode","checkMemberidActiveOrNot");
            jsonObject.put("memberid",MemberId);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getActivationStatus().execute(String.valueOf(jsonObject));
        }

    }

    private class getActivationStatus extends AsyncTask<String,String,String> {

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

        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                JSONObject Object = new JSONObject(JsonResponse);
                String strStutus=Object.getString("status");
                Log.d(TAG, "status data: "+strStutus);
                String strMessage = Object.getString("message");
                if (strStutus.equals("1")){
                    JSONArray jsonArray=Object.getJSONArray("response");
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        String MemberId=jsonObject.getString("memberid");
                        String strMemberName=jsonObject.getString("membername");
                        String strActive=jsonObject.getString("active");
                        Intent intent=new Intent(MemberLoginActivity.this,MemberActivationActivity.class);
                        intent.putExtra("strMemberId",MemberId);
                        intent.putExtra("strMemberName",strMemberName);
                        intent.putExtra("strActive",strActive);
                        startActivity(intent);

                    }

                }else {
                    Intent intent=new Intent(MemberLoginActivity.this,MemberHomeActivity.class);
                    intent.putExtra("MemberId",MemberId);
                    startActivity(intent);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
