package com.tornado.cphp.awhitepaid.vendorpanel;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.memberpanel.MemberHomeActivity;
import com.tornado.cphp.awhitepaid.memberpanel.MemberMobileNumberToPay;
import com.tornado.cphp.awhitepaid.memberpanel.MemberQRCodeResultActivity;
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

import static com.tornado.cphp.awhitepaid.memberpanel.MemberRechargeActivity.MY_PERMISSIONS_REQUEST_REAC_CONTACT;

public class VendorMobileTransferActivity extends AppCompatActivity {

    private static final String TAG=VendorMobileTransferActivity.class.getSimpleName();
    private ImageView imgContact;
    private Toolbar mToolbarVendorMobilePay;
    private TextView txtTitle;
    private String JsonResponse = "",strMobileNumber="",strSpiltMobileNumber="";
    private ProgressDialog pd;
    private EditText etMobileNumber;
    private final int REQUEST_CODE = 99;
    private Button btnTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_mobile_transfer);
        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission();
        }

        mToolbarVendorMobilePay= (Toolbar) findViewById(R.id.mToolbarVendorMobilePay);
        txtTitle= (TextView)mToolbarVendorMobilePay.findViewById(R.id.txtTitle);
        txtTitle.setText("Enter Mobile Number to Pay");
        mToolbarVendorMobilePay.setNavigationIcon(R.drawable.back_icon);
        mToolbarVendorMobilePay.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        etMobileNumber= (EditText) findViewById(R.id.etMobileNumber);
        imgContact= (ImageView) findViewById(R.id.imgContact);
        btnTransfer=(Button)findViewById(R.id.btnTransfer);
        imgContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick contact img: ");
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strMobileNumber=etMobileNumber.getText().toString();
                strSpiltMobileNumber=strMobileNumber.substring(3,13);
                Log.d(TAG, "split mobile number: "+strSpiltMobileNumber);

                Log.d(TAG, "transfer mobile number: "+strMobileNumber);

                if (StringUtils.isBlank(strSpiltMobileNumber)){
                    Toast.makeText(VendorMobileTransferActivity.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }else {

                    getMemberName(strSpiltMobileNumber);

                }
            }
        });

        etMobileNumber.setText("+91");
        Selection.setSelection(etMobileNumber.getText(),etMobileNumber.getText().length());

        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().contains("+91")){
                    etMobileNumber.setText("+91");
                    Selection.setSelection(etMobileNumber.getText(),etMobileNumber.getText().length());
                }

            }
        });

    }

    private void getMemberName(String strMobileNumber) {

        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("mode","mobilenumberGetName");
            jsonObject.put("mobile",strMobileNumber);

        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getMemberNameFromMobileNumber().execute(String.valueOf(jsonObject));
        }
    }

    public boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_REAC_CONTACT);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_REAC_CONTACT);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String num = "";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                                Toast.makeText(MemberRechargeActivity.this, "Number=" + num, Toast.LENGTH_LONG).show();
                                etMobileNumber.setText(num);
                            }
                        }
                    }
                    break;
                }
        }
    }

    private class getMemberNameFromMobileNumber  extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd=new ProgressDialog(VendorMobileTransferActivity.this);
            pd.setIndeterminate(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
            pd.setMessage("Please wait...");
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
                String strStutus = Object.getString("status");
                Log.d(TAG, "status: " + strStutus);
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);
                if (strStutus.equalsIgnoreCase("1")){
                    JSONArray jsonArray=Object.getJSONArray("response");
                    String strMemberName=jsonArray.getString(0);
                    Log.d(TAG, "member name: "+strMemberName);
                    Intent intent=new Intent(VendorMobileTransferActivity.this,QRCodeResultActivity.class);
                    intent.putExtra("type","mobile");
                    intent.putExtra("memberId", VendorHomeAcivity.strVendorId);
                    intent.putExtra("memberName",strMemberName);
                    intent.putExtra("strMobileNumber",strSpiltMobileNumber);
                    startActivity(intent);
                }else {
                    Toast.makeText(VendorMobileTransferActivity.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }



}
