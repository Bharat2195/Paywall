package com.tornado.cphp.awhitepaid.vendorpanel;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.utils.StringUtils;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VendorRechargeActivity extends AppCompatActivity {

    private static final String TAG = VendorRechargeActivity.class.getSimpleName();

    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.etMobileNumber)
    TextInputLayout etMobileNumber;
    @BindView(R.id.etCurrentOperator)
    TextInputLayout etCurrentOperator;
    @BindView(R.id.etAmount)
    TextInputLayout etAmount;
    @BindView(R.id.btnRecharge)
    Button btnRecharge;
    @BindView(R.id.activity_member_recharge)
    RelativeLayout activityMemberRecharge;
    @BindView(R.id.etCustomerId)
    TextInputLayout etCustomerId;
    private TextView txtTitle;
    private Toolbar mToolbarRecharge;
    private final int REQUEST_CODE = 99;
    public static final int MY_PERMISSIONS_REQUEST_REAC_CONTACT = 99;
    ArrayList<String> listId = new ArrayList<>();
    ArrayList<String> listType = new ArrayList<>();
    ArrayList<String> listOperator = new ArrayList<>();
    ArrayList<String> listOperatorCode = new ArrayList<>();
    private ListView mListviewOperator;
    private TextView txtCancle;
    RequestQueue requestQueue;
    private int intWalletBalance = 0;
    private RadioGroup rgRechargeType;
    private RadioButton rdRechargeType;
    private String KEY_LOGIN_ID = "login_id";
    private String KEY_TRANSACTION_PASSWORD = "transaction_password";
    private String KEY_MESSAGE = "message";
    private String KEY_RESPONSE_TYPE = "response_type";
    private Dialog dialog;
    private ImageView imgContact;
    private String strBalance, strSpiltBlance,strMobileNumber ,JsonResponse,strWalletBalance,strSpitValue;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_recharge);
        getSupportActionBar().hide();
        ButterKnife.bind(this);


        etMobileNumber.getEditText().setText("+91");
        Selection.setSelection(etMobileNumber.getEditText().getText(),etMobileNumber.getEditText().length());

        etMobileNumber.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().contains("+91")){
                    etMobileNumber.getEditText().setText("+91");
                    Selection.setSelection(etMobileNumber.getEditText().getText(),etMobileNumber.getEditText().length());

                }

            }
        });
        requestQueue = Volley.newRequestQueue(this);
        imgContact = (ImageView) findViewById(R.id.imgContact);
        rgRechargeType = (RadioGroup) findViewById(R.id.rgRechargeType);
        mToolbarRecharge = (Toolbar) findViewById(R.id.mToolbarRecharge);
        txtTitle = (TextView) mToolbarRecharge.findViewById(R.id.txtTitle);
        txtTitle.setText("Mobile Recharge or Bill Payment");
        mToolbarRecharge.setNavigationIcon(R.drawable.back_icon);
        mToolbarRecharge.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rgRechargeType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb= (RadioButton) findViewById(checkedId);
                Log.d(TAG, "checked radio button: "+rb);
                etMobileNumber.getEditText().getText().clear();
                etCurrentOperator.getEditText().getText().clear();
                etCustomerId.getEditText().getText().clear();
                etAmount.getEditText().getText().clear();
                String strCheckedButtonName=rb.getText().toString();
                Log.d(TAG, "check radio button name: "+strCheckedButtonName);
                if (strCheckedButtonName.equalsIgnoreCase("DTH")){
                    etMobileNumber.setVisibility(View.GONE);
                    etCustomerId.setVisibility(View.VISIBLE);
                    imgContact.setVisibility(View.GONE);
                }else {
                    etMobileNumber.setVisibility(View.VISIBLE);
                    etCustomerId.setVisibility(View.GONE);
                    imgContact.setVisibility(View.VISIBLE);
                }
            }
        });

        getWallterBalance();
        if (strWalletBalance != null) {
            intWalletBalance = Integer.valueOf(strWalletBalance);
            Log.d(TAG, "wallet balance: " + intWalletBalance);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission();
        }


        imgContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        etMobileNumber.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etCurrentOperator.setVisibility(View.VISIBLE);
                    etAmount.setVisibility(View.VISIBLE);
                }
            }
        });

        etCurrentOperator.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(VendorRechargeActivity.this);
                dialog.setTitle("Select Operator");
                dialog.setContentView(R.layout.dialog_member_recharge);
                mListviewOperator = (ListView) dialog.findViewById(R.id.mListviewOperator);
                txtCancle = (TextView) dialog.findViewById(R.id.txtCancle);
                txtCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                getOperator();
                dialog.show();
            }
        });

    }

    private void getWallterBalance() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mode", "getVendorBalance");
            jsonObject.put("vendorid", VendorHomeAcivity.strVendorId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonObject.length() > 0) {
            new getwalletBalance().execute(String.valueOf(jsonObject));
        }
    }

    private class getwalletBalance extends AsyncTask<String, String, String> {
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


            try {
                JSONObject Object = new JSONObject(JsonResponse);
                String strStutus = Object.getString("status");
                Log.d(TAG, "status: " + strStutus);
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);

                JSONArray jsonArray = Object.getJSONArray("response");
                strWalletBalance = jsonArray.getString(0);
                Log.d(TAG, "array response: " + strWalletBalance);
                String [] strFloatBalance=strWalletBalance.split("\\.");
                strSpitValue=strFloatBalance[0];
                Log.d(TAG, "strSpilt wallter balance: "+strSpitValue);
//                txtBalance.setText("BALANCE " + strSpitValue);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getOperator() {

        int id = rgRechargeType.getCheckedRadioButtonId();
        rdRechargeType = (RadioButton) findViewById(id);
        String strType = rdRechargeType.getText().toString();
        Log.d(TAG, "operator type: " + strType);


        Map<String, String> jsonParms = new HashMap<>();

        jsonParms.put("mode", "passMobileOperator");
        jsonParms.put("type", strType);


        listId.clear();
        listType.clear();
        listOperator.clear();
        listOperatorCode.clear();


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, StringUtils.strBaseURL,
                new JSONObject(jsonParms),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "json Response: " + response);
                        try {

                            JSONArray jsonArray = response.getJSONArray("response");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String strId = jsonObject.getString("id");
                                String strOperator = jsonObject.getString("operator");
                                String strOperatorCode = jsonObject.getString("code");
                                String strType = jsonObject.getString("type");

                                listId.add(strId);
                                listType.add(strType);
                                listOperator.add(strOperator);
                                listOperatorCode.add(strOperatorCode);
                            }

                            if (mListviewOperator != null) {
                                CustomAdapter customAdapter = new CustomAdapter(VendorRechargeActivity.this, R.layout.list_item_member_dialog_recharge_operator, listOperator);
                                mListviewOperator.setAdapter(customAdapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Handle Error
                        Log.d(TAG, "onErrorResponse: " + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };
        requestQueue.add(postRequest);

    }

    private class CustomAdapter extends ArrayAdapter<String> {


        List<String> items;
        Context context;
        int resource;


        public CustomAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);

            this.context = context;
            this.resource = resource;
            this.items = items;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(resource, null);

                holder.txtOperator = (TextView) convertView.findViewById(R.id.txtOperator);


                convertView.setTag(holder);


            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtOperator.setText(items.get(position));
            holder.txtOperator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etCurrentOperator.getEditText().setText(listOperator.get(position));
                    dialog.dismiss();
                }
            });

            return convertView;

        }
    }

    private class ViewHolder {

        TextView txtOperator;
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


    @OnClick(R.id.btnRecharge)
    public void onClick() {
        int id = rgRechargeType.getCheckedRadioButtonId();
        rdRechargeType = (RadioButton) findViewById(id);
        String strRechargeType = rdRechargeType.getText().toString();
        Log.d(TAG, " submit operator type: " + strRechargeType);
        Log.d(TAG, "Recharge btn Clicked: ");

        String strOperator = etCurrentOperator.getEditText().getText().toString();
        String strAmount = etAmount.getEditText().getText().toString();
        Log.d(TAG, "submit stramount: " + strAmount);



        if (strRechargeType.equalsIgnoreCase("DTH")){
            strMobileNumber = etCustomerId.getEditText().getText().toString();
            Log.d(TAG, "customer id click: "+strMobileNumber);
        }else {
            String MobileNumber=etMobileNumber.getEditText().getText().toString();
            strMobileNumber = MobileNumber.substring(3,13);
            Log.d(TAG, "split mobile number: "+strMobileNumber);
            Log.d(TAG, "mobile number: "+strMobileNumber);
        }

        if (StringUtils.isBlank(strMobileNumber)) {
            Toast.makeText(VendorRechargeActivity.this, "Please Enter Mobile Number || Customer Id", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strOperator)) {
            Toast.makeText(VendorRechargeActivity.this, "Please Enter Operator", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strAmount)) {
            Toast.makeText(VendorRechargeActivity.this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
        } else if (Integer.valueOf(strAmount) > Integer.valueOf(strSpitValue)) {
            Toast.makeText(VendorRechargeActivity.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
        } else {

            pd = new ProgressDialog(VendorRechargeActivity.this);
            pd.setMessage("Please wait...");
            pd.setIndeterminate(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();


            getReachargeData(strMobileNumber, strOperator, strAmount, strRechargeType);


        }
    }

    private void getReachargeData(String strMobileNumber, String strOperator, String strAmount, String strRechargeType) {

        Map<String, String> jsonParams = new HashMap<String, String>();

        jsonParams.put("mode", "vendorRechargeSubmit");
        jsonParams.put("vendorid", VendorHomeAcivity.strVendorId);
        jsonParams.put("amount", strAmount);
        jsonParams.put("mobile", strMobileNumber);
        jsonParams.put("operator", strOperator);
        jsonParams.put("rechargetype", strRechargeType);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, StringUtils.strBaseURL,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "recharge json Response: " + response);
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        try {
                            String strStatus = response.getString("status");
                            JSONArray jsonArray = response.getJSONArray("response");
                            String strRechargeRespons = jsonArray.getString(0);
                            Log.d(TAG, "recharge array response: " + strRechargeRespons);
                            Toast.makeText(VendorRechargeActivity.this, strRechargeRespons, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Handle Error
                        Log.d(TAG, "onErrorResponse: " + error);
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };
        requestQueue.add(postRequest);
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
                                etMobileNumber.getEditText().setText(num);
                            }
                        }
                    }
                    break;
                }
        }
    }




}