package com.tornado.cphp.paywall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
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

public class VendorEdiProfileActivity extends AppCompatActivity {

    private static final String TAG=VendorEdiProfileActivity.class.getSimpleName();
    private EditText etVendorId,etVendorName,etVendorEmail,etVendorMobile,etVendorPassword,etVendorAccountNo,etVendorBankName,
            etVendorBankBranchName,etVendorIFSCCode;
    private ImageView imgvendorId,imgVendorName,imgVendorEmail,imgVendorMobile,imgVendorPassword,imgVendorBankAccountNo,
            imgVendorBankName,imgVendorBankBranchName,imgVendorBankIFSCCOde;
    private TextView txtSave;
    private ProgressDialog pd;
    private String JsonResponse="",strQRCodeResponse="",strQRCode="",strEditprofileResponse="";
    ImageView imgQRCode;
    Thread thread ;
    public final static int QRcodeWidth = 500;
    public static Bitmap bitmap ;
    String strVendorId="";
    private Toolbar toolbar_edit_profile;
    private TextView txtTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().hide();



        toolbar_edit_profile=(Toolbar)findViewById(R.id.toolbar_edit_profile);
        txtTitle=(TextView)toolbar_edit_profile.findViewById(R.id.txtTitle);
        txtTitle.setText("Edit Profile");
        toolbar_edit_profile.setNavigationIcon(R.drawable.back_icon);
        toolbar_edit_profile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgQRCode=(ImageView)toolbar_edit_profile.findViewById(R.id.imgQRCode);

        etVendorId=(EditText)findViewById(R.id.etVendorId);
        etVendorName=(EditText)findViewById(R.id.etVendorName);
        etVendorEmail=(EditText)findViewById(R.id.etVendorEmail);
        etVendorMobile=(EditText)findViewById(R.id.etVendorMobile);
        etVendorPassword= (EditText) findViewById(R.id.etVendorPassword);

        etVendorAccountNo= (EditText) findViewById(R.id.etVendorAccountNo);
        etVendorBankName= (EditText) findViewById(R.id.etVendorBankName);
        etVendorBankBranchName= (EditText) findViewById(R.id.etVendorBankBranchName);
        etVendorIFSCCode= (EditText) findViewById(R.id.etVendorIFSCCode);

        imgQRCode=(ImageView)findViewById(R.id.imgQRCode);
        txtSave=(TextView)findViewById(R.id.txtSave);

        strVendorId= VendorMainActivity.strVendorId;
        Log.d(TAG, "vendor id: "+strVendorId);
        getEditProfileData();
        getQRCode();


        imgVendorName=(ImageView)findViewById(R.id.imgVendorName);
        imgVendorEmail=(ImageView)findViewById(R.id.imgVendorEmail);
        imgVendorMobile=(ImageView)findViewById(R.id.imgVendorMobile);
        imgVendorBankAccountNo=(ImageView)findViewById(R.id.imgVendorBankAccountNo);
        imgVendorBankName=(ImageView)findViewById(R.id.imgVendorBankName);
        imgVendorBankBranchName=(ImageView)findViewById(R.id.imgVendorBankBranchName);
        imgVendorBankIFSCCOde=(ImageView)findViewById(R.id.imgVendorBankIFSCCOde);

        imgQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(VendorEdiProfileActivity.this,VendorQRCodeActivity.class);
                startActivity(intent);
            }
        });



        imgVendorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etVendorName.setEnabled(true);
            }
        });

        imgVendorEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etVendorEmail.setEnabled(true);
            }
        });

        imgVendorMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etVendorMobile.setEnabled(true);
            }
        });

        imgVendorBankAccountNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etVendorAccountNo.setEnabled(true);
            }
        });
        imgVendorBankName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etVendorBankName.setEnabled(true);
            }
        });

        imgVendorBankBranchName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etVendorBankBranchName.setEnabled(true);
            }
        });

        imgVendorBankIFSCCOde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etVendorIFSCCode.setEnabled(true);
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strVendorId=etVendorId.getText().toString();
                String strVendorName=etVendorName.getText().toString();
                String strVendorEmail=etVendorEmail.getText().toString();
                String strVendorPassword=etVendorPassword.getText().toString();
                String strVendorMobile=etVendorMobile.getText().toString();
                String strVendorBankAccountNo=etVendorAccountNo.getText().toString();
                String strVendorBankName=etVendorBankName.getText().toString();
                String strVendorBankIFSCCode=etVendorIFSCCode.getText().toString();
                String strVendorBranchName=etVendorBankBranchName.getText().toString();


                JSONObject jsonObject=new JSONObject();
                try{
                    jsonObject.put("mode","vendorUpdateProfile");
                    jsonObject.put("vendorid",strVendorId);
                    jsonObject.put("vendor_name",strVendorName);
                    jsonObject.put("vendor_mobile",strVendorMobile);
                    jsonObject.put("vendor_email",strVendorEmail);
                    jsonObject.put("bankaccountnumber", strVendorBankAccountNo);
                    jsonObject.put("bankname", strVendorBankName);
                    jsonObject.put("bankbranch", strVendorBranchName);
                    jsonObject.put("ifsccode", strVendorBankIFSCCode);


                }catch (Exception e){
                    e.printStackTrace();
                }

                if (jsonObject.length()>0){
                    new editProfile().execute(String.valueOf(jsonObject));
                }
            }
        });

    }

    private void getQRCode() {

        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("mode","vendorQRcode");
            jsonObject.put("vendorid",strVendorId);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getQRCode().execute(String.valueOf(jsonObject));
        }
    }

    private void getEditProfileData() {

        JSONObject jsonObject=new JSONObject();
        try{

            jsonObject.put("mode","vendorEditProfile");
            jsonObject.put("vendorid", VendorMainActivity.strVendorId);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getProfileData().execute(String.valueOf(jsonObject));
        }


    }

    private class getProfileData extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(VendorEdiProfileActivity.this);
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
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);
                JSONArray jsonArray = Object.getJSONArray("response");
                for (int i=0; i<jsonArray.length(); i++){
                    JSONObject object=jsonArray.getJSONObject(i);

                    String strVendorId=object.getString("vendorid");
                    String strName=object.getString("name");
                    String strAddress=object.getString("address");
                    String strEmail=object.getString("email");
                    String strMobile=object.getString("mobile");
                    String strPassword=object.getString("password");
                    String strBankAccountNo=object.getString("bankaccountnumber");
                    String strBankName=object.getString("bankname");
                    String strBankBranchName=object.getString("bankbranch");
                    String strIFSCCode=object.getString("ifsccode");


                    etVendorId.setText("Vendor Id :"+strVendorId);
                    etVendorName.setText("Vendor Name :"+strName);
                    etVendorEmail.setText("Email :"+strEmail);
                    etVendorMobile.setText("Vendor Mobile :"+strMobile);
                    etVendorPassword.setText("Vendor Password :"+strPassword);
                    etVendorIFSCCode.setText("Vendor IFSC Code :"+strIFSCCode);
                    etVendorAccountNo.setText("Bank Account No :"+strBankAccountNo);
                    etVendorBankBranchName.setText("Bank Branch Name :"+strBankBranchName);
                    etVendorBankName.setText("Vendror Bank Name :"+strBankName);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class getQRCode  extends AsyncTask<String,String,String>{

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
                strQRCodeResponse = buffer.toString();
                Log.i(TAG, strQRCodeResponse);
                //send to post execute
                return strQRCodeResponse;
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

            return strQRCodeResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);



            try {
                JSONObject Object = new JSONObject(strQRCodeResponse);
                String strStutus=Object.getString("status");
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);
                JSONArray jsonArray = Object.getJSONArray("response");
                strQRCode=jsonArray.getString(0);
                Log.d(TAG, "QRCode: "+strQRCode);

                bitmap=TextToImageEncode(strQRCode);
                imgQRCode.setImageBitmap(bitmap);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap TextToImageEncode(String strQRCode) throws WriterException{
        BitMatrix bitMatrix;

        try {
            bitMatrix = new MultiFormatWriter().encode(
                    strQRCode,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.colorPrimaryDark):getResources().getColor(android.R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private class editProfile extends AsyncTask<String,String,String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(VendorEdiProfileActivity.this);
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
                String strStutus=Object.getString("status");
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);
                JSONArray jsonArray = Object.getJSONArray("response");
                String strUpdate=jsonArray.getString(0);
                Toast.makeText(VendorEdiProfileActivity.this, strUpdate, Toast.LENGTH_SHORT).show();




            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
