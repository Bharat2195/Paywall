package com.tornado.cphp.awhitepaid;

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

import com.tornado.cphp.awhitepaid.utils.StringUtils;

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

public class QRCodeResultActivity extends AppCompatActivity {

    private static final String TAG=QRCodeResultActivity.class.getSimpleName();
    private Toolbar toolbar_code_result;
    private TextView txtTitle,txtName;
    private String strMemberId="",strMemberName="",strQRCode="",JsonResponse="";
    private Button btnName,btnProcessToPay;
    private EditText etReason,etAmount;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_result);

        getSupportActionBar().hide();
        toolbar_code_result=(Toolbar)findViewById(R.id.toolbar_code_result);
        txtTitle=(TextView)toolbar_code_result.findViewById(R.id.txtTitle);
        txtName=(TextView)findViewById(R.id.txtName);

        btnName=(Button) findViewById(R.id.btnName);

        toolbar_code_result.setNavigationIcon(R.drawable.back_icon);
        toolbar_code_result.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnProcessToPay=(Button)findViewById(R.id.btnProcessToPay);
//        etReason=(EditText)findViewById(R.id.etReason);
        etAmount=(EditText)findViewById(R.id.etAmount);

        Intent intent=getIntent();
        strMemberId=intent.getStringExtra("memberId");
        strMemberName=intent.getStringExtra("memberName");
        strQRCode=intent.getStringExtra("QrCode");


        Log.d(TAG, "memberid: "+strMemberId);
        Log.d(TAG, "membername: "+strMemberName);
        Log.d(TAG, "qrcode: "+strQRCode);

        txtName.setText(strMemberName);

        txtTitle.setText("Pay Money to "+strMemberName);


        char chrFirstChecter=strMemberName.charAt(0);
        btnName.setText(String.valueOf(chrFirstChecter));



        btnProcessToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strAmount=etAmount.getText().toString();
                Log.d(TAG, "amount: "+strAmount);
                if (StringUtils.isBlank(strAmount) ) {
                    Toast.makeText(QRCodeResultActivity.this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
                }else if (strAmount.equals("0")){
                    Toast.makeText(QRCodeResultActivity.this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
                }else if (strAmount.contains("-")){
                    Toast.makeText(QRCodeResultActivity.this, "Please Enter Correct Amount", Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(strAmount) > Integer.parseInt(VendorMainActivity.strSpitValue)){
                    Toast.makeText(QRCodeResultActivity.this, "Insufficient Your Wallet balance", Toast.LENGTH_SHORT).show();
                }else {

                    JSONObject jsonObject=new JSONObject();
                    try{
                        jsonObject.put("mode","vendorToOtherFundTransfer");
                        jsonObject.put("fromid", VendorMainActivity.strVendorId);
                        jsonObject.put("to",strQRCode);
                        jsonObject.put("amount",strAmount);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if (jsonObject.length()>0){
                        new transferAmount().execute(String.valueOf(jsonObject));
                    }
                }
            }
        });
    }

    private class transferAmount extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(QRCodeResultActivity.this);
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

                if (strStutus.equals("1")){
                    Toast.makeText(QRCodeResultActivity.this, "Transaction Done Successfully", Toast.LENGTH_SHORT).show();
                    etAmount.setText("0");
                }else if (strStutus.equals("2")) {
                    Toast.makeText(QRCodeResultActivity.this, "Qrcode Is Not Valid", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(QRCodeResultActivity.this, "Vendor Doesn't Have Sufficient Balance", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}