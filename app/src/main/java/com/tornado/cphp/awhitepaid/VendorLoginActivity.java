package com.tornado.cphp.awhitepaid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tornado.cphp.awhitepaid.utils.JsonParser;
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
import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class VendorLoginActivity extends AppCompatActivity {

    private static final String TAG = VendorLoginActivity.class.getSimpleName();
    private String JsonResponse = "",VendorId="";
    JsonParser jsonParser;
    private EditText etUsername,etPassword;
    private Button btnLogin;
    private TextView txtSignIn,txtRegister;
    GifImageView gifLoading;
    JSONObject jsonObject;
    private ProgressDialog pd;
    private RelativeLayout relativeLayout;
    ArrayList<TextView>listTextview=new ArrayList<>();
    public static final String PREFS_NAME = "LoginPrefes";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        etUsername=(EditText)findViewById(R.id.etUsername);
        etPassword=(EditText)findViewById(R.id.etPassword);
        btnLogin=(Button) findViewById(R.id.btnLogin);
        gifLoading=(GifImageView)findViewById(R.id.gifLoading);
        txtRegister=(TextView)findViewById(R.id.txtRegister);
        txtSignIn=(TextView)findViewById(R.id.txtSignIn);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);

        listTextview.add(etUsername);
        listTextview.add(etPassword);


        for (int i=0; i<listTextview.size(); i++){
            YoYo.with(Techniques.BounceInLeft)
                    .duration(1000)
                    .playOn(listTextview.get(i));
        }

        YoYo.with(Techniques.Bounce)
                .duration(700)
                .playOn(btnLogin);


        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        if (preferences.getString("logged", "").toString().equals("logged")) {
            Intent intent = new Intent(VendorLoginActivity.this, VendorHomeAcivity.class);
            intent.putExtra("VendorId", preferences.getString("VendorId", "").toString());
            startActivity(intent);
        }

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignIn=new Intent(VendorLoginActivity.this,VendorRegistrationActivity.class);
                startActivity(intentSignIn);
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegiter=new Intent(VendorLoginActivity.this,VendorRegistrationActivity.class);
                startActivity(intentRegiter);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(VendorLoginActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                getLogin();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(VendorLoginActivity.this,LoginTypeActivity.class);
        startActivity(intent);
    }

    private void getLogin() {

        String strUserName = etUsername.getText().toString().trim();
        String strPassword = etPassword.getText().toString().trim();

        if (StringUtils.isBlank(strUserName)) {
            Toast.makeText(VendorLoginActivity.this, "Pleae Enter VendorId", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strPassword)) {
            Toast.makeText(VendorLoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        } else {

            jsonObject = new JSONObject();
            try {
                jsonObject.put("mode", "vendorLogin");
                jsonObject.put("vendorid", strUserName);
                jsonObject.put("password", strPassword);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jsonObject.length() > 0) {
                new GetLogin().execute(String.valueOf(jsonObject));

            }

        }

    }

    private class GetLogin extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(VendorLoginActivity.this);
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
//            if (JsonResponse!=null){
//                gifLoading.setVisibility(View.GONE);
//            }

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
                        VendorId=jsonObject1.getString("vendorid");
                        Log.d(TAG, "Vendor Id: "+VendorId);
                        String strPassword=jsonObject1.getString("password");
                        Log.d(TAG, "strPassword: "+strPassword);
                        Toast.makeText(VendorLoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("logged", "logged");
                        editor.putString("VendorId", VendorId);
                        editor.commit();
                        Intent intent=new Intent(VendorLoginActivity.this,VendorHomeAcivity.class);
                        intent.putExtra("VendorId",VendorId);
                        startActivity(intent);

                    }
                }else {
                    JSONArray jsonArray=Object.getJSONArray("response");
                    String strJsonArray=jsonArray.getString(0);
                    Log.d(TAG, "json arrat: "+strJsonArray);
                    Toast.makeText(VendorLoginActivity.this, strJsonArray, Toast.LENGTH_SHORT).show();

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
}
