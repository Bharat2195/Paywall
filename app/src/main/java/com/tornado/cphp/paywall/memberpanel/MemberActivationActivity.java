package com.tornado.cphp.paywall.memberpanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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

public class MemberActivationActivity extends AppCompatActivity {

    private static final String TAG = MemberActivationActivity.class.getSimpleName();
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.txtMemberId)
    TextView txtMemberId;
    @BindView(R.id.etMemberid)
    EditText etMemberid;
    @BindView(R.id.txtMemberName)
    TextView txtMemberName;
    @BindView(R.id.etMemberName)
    EditText etMemberName;
    @BindView(R.id.txtAmount)
    TextView txtAmount;
    @BindView(R.id.spnAmount)
    Spinner spnAmount;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    private Toolbar mToolbarActiveMember;
    private TextView txtTitle;
    private String strMemberId = "", strMemberName = "", strActive = "",JsonResponse="";
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_activation);
        ButterKnife.bind(this);
        getSupportActionBar().hide();

        mToolbarActiveMember = (Toolbar) findViewById(R.id.mToolbarActiveMember);
        txtTitle = (TextView) mToolbarActiveMember.findViewById(R.id.txtTitle);
        txtTitle.setText("Activation Member");
        Intent intent = getIntent();
        strMemberId = intent.getStringExtra("strMemberId");
        Log.d(TAG, "member id: " + strMemberId);
        strMemberName = intent.getStringExtra("strMemberName");
        strActive = intent.getStringExtra("strActive");
        Log.d(TAG, "member name: " + strMemberName);
        Log.d(TAG, "active: " + strActive);


        etMemberid.setText(strMemberId);
        etMemberName.setText(strMemberName);


        mToolbarActiveMember.setNavigationIcon(R.drawable.back_icon);
        mToolbarActiveMember.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        YoYo.with(Techniques.FadeInLeft)
                .duration(1000)
                .playOn(etMemberid);

        YoYo.with(Techniques.FadeInLeft)
                .duration(1000)
                .playOn(etMemberName);


        YoYo.with(Techniques.FadeInLeft)
                .duration(1000)
                .playOn(spnAmount);

        YoYo.with(Techniques.FadeInLeft)
                .duration(700)
                .playOn(btnSubmit);

    }

    @OnClick(R.id.btnSubmit)
    public void onClick() {

        String strMemberID = etMemberid.getText().toString().trim();
        String strMemberName = etMemberName.getText().toString().trim();
        String strAmount = spnAmount.getSelectedItem().toString();
        Log.d(TAG, "amount: " + strAmount);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mode", "MemberActivationSubmit");
            jsonObject.put("memberid", strMemberID);
            jsonObject.put("amount", strAmount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getActivation().execute(String.valueOf(jsonObject));
        }

    }

    private class getActivation extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(MemberActivationActivity.this);
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

                JSONArray jsonArray=Object.getJSONArray("response");
                String strRespnse=jsonArray.getString(0);
                Log.d(TAG, "response: "+strRespnse);
                Toast.makeText(MemberActivationActivity.this, strRespnse, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
