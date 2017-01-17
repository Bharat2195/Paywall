package com.tornado.cphp.paywall.memberpanel;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tornado.cphp.paywall.R;
import com.tornado.cphp.paywall.VendorEdiProfileActivity;
import com.tornado.cphp.paywall.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MemberEditProfileActivity extends AppCompatActivity {

    private static final String TAG = MemberEditProfileActivity.class.getSimpleName();
    @BindView(R.id.imgQRCode)
    ImageView imgQRCode;
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.etPlacementId)
    EditText etPlacementId;
    @BindView(R.id.etSponsorId)
    EditText etSponsorId;
    @BindView(R.id.etMemberID)
    EditText etMemberID;
    @BindView(R.id.etMemberName)
    EditText etMemberName;
    @BindView(R.id.imgMemberName)
    ImageView imgMemberName;
    @BindView(R.id.etMemberEmail)
    EditText etMemberEmail;
    @BindView(R.id.imgMemberEmail)
    ImageView imgMemberEmail;
    @BindView(R.id.etMemberMobile)
    EditText etMemberMobile;
    @BindView(R.id.imgMemberMobile)
    ImageView imgMemberMobile;
    @BindView(R.id.etMemberAddress)
    EditText etMemberAddress;
    @BindView(R.id.imgMemberAddress)
    ImageView imgMemberAddress;
    @BindView(R.id.etMemberCity)
    EditText etMemberCity;
    @BindView(R.id.imgMemberCity)
    ImageView imgMemberCity;
    @BindView(R.id.etMemberState)
    EditText etMemberState;
    @BindView(R.id.imgMemberState)
    ImageView imgMemberState;
    @BindView(R.id.etMemberCountry)
    EditText etMemberCountry;
    @BindView(R.id.imgMemberCountry)
    ImageView imgMemberCountry;
    @BindView(R.id.txtSave)
    TextView txtSave;
    @BindView(R.id.layout_footer)
    RelativeLayout layoutFooter;
    @BindView(R.id.activity_registration)
    RelativeLayout activityRegistration;

    private ProgressDialog pd;
    private String JsonResponse="",strQRCodeResponse="",strQRCode="",strEditprofileResponse="";
    public final static int QRcodeWidth = 500;
    public static Bitmap bitmap ;
    String strMemberId="";
    private Toolbar mToolbarMemberEditProfile;
    private TextView txtTitle;


    private final String KEY_MODE="mode";
    private final String KEY_MEMBERID="memberid";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_edit_profile);
        ButterKnife.bind(this);
        getSupportActionBar().hide();

        mToolbarMemberEditProfile= (Toolbar) findViewById(R.id.mToolbarMemberEditProfile);
        txtTitle=(TextView)mToolbarMemberEditProfile.findViewById(R.id.txtTitle);
        txtTitle.setText("Edit Profile");
        mToolbarMemberEditProfile.setNavigationIcon(R.drawable.back_icon);
        mToolbarMemberEditProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        strMemberId=MemberDashboardActivity.strMemberId;
        Log.d(TAG, "member id: "+strMemberId);

        getProfileData();
    }

    private void getProfileData() {


        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("mode","MemberEditProfile");
            jsonObject.put("memberid",strMemberId);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getProfile().execute(String.valueOf(jsonObject));
        }


    }

    @OnClick(R.id.txtSave)
    public void onClick() {

        getRegisterData();

    }

    private void getRegisterData() {

        String strMemberId=etMemberID.getText().toString().trim();
        String strMemberName=etMemberName.getText().toString().trim();
        String strMemberMobile=etMemberMobile.getText().toString().trim();
        String strMemberEmail=etMemberEmail.getText().toString().trim();
        String strMemberAddress=etMemberAddress.getText().toString().trim();
        String strMemberCity=etMemberCity.getText().toString().trim();
        String strMemberState=etMemberState.getText().toString().trim();
        String strMemberCountry=etMemberCountry.getText().toString().trim();


        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("mode","MemberUpdateProfiledb");
            jsonObject.put("memberid",strMemberId);
            jsonObject.put("membername",strMemberName);
            jsonObject.put("mobile",strMemberMobile);
            jsonObject.put("email",strMemberEmail);
            jsonObject.put("address",strMemberAddress);
            jsonObject.put("city",strMemberCity);
            jsonObject.put("state",strMemberState);
            jsonObject.put("country",strMemberCountry);

        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getResgiterData().execute(String.valueOf(jsonObject));
        }
    }


    private class getProfile  extends AsyncTask<String,String,String>{

        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(MemberEditProfileActivity.this);
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
                strEditprofileResponse = buffer.toString();
                Log.i(TAG, strEditprofileResponse);
                //send to post execute
                return strEditprofileResponse;
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

            return strEditprofileResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if (pd.isShowing()){
                pd.dismiss();
            }

            try {
                JSONObject Object = new JSONObject(strEditprofileResponse);
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);
                JSONArray jsonArray = Object.getJSONArray("response");
                for (int i=0; i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    String strPlacementId=jsonObject.getString("placementid");
                    String strSponsorId=jsonObject.getString("sponsorid");
                    String strMemberId=jsonObject.getString("memberid");
                    String strMemberName=jsonObject.getString("membername");
                    String strMobile=jsonObject.getString("mobile");
                    String strEmail=jsonObject.getString("email");
                    String strAddress=jsonObject.getString("address");
                    String strCity=jsonObject.getString("city");
                    String strState=jsonObject.getString("state");
                    String strCountry=jsonObject.getString("contry");

                    etPlacementId.setText(strPlacementId);
                    etSponsorId.setText(strSponsorId);
                    etMemberID.setText(strMemberId);
                    etMemberName.setText(strMemberName);
                    etMemberMobile.setText(strMobile);
                    etMemberEmail.setText(strEmail);
                    etMemberAddress.setText(strAddress);
                    etMemberCity.setText(strCity);
                    etMemberState.setText(strState);
                    etMemberCountry.setText(strCountry);
                }




            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class getResgiterData extends AsyncTask<String,String,String>{

        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(MemberEditProfileActivity.this);
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
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);
                JSONArray jsonArray = Object.getJSONArray("response");
                String strResponse=jsonArray.getString(0);
                Toast.makeText(MemberEditProfileActivity.this, strResponse, Toast.LENGTH_SHORT).show();




            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
