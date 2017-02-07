package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

/**
 * Created by cphp on 12-Jan-17.
 */

public class MemberWithdrawalFragment extends Fragment {

    private static final String TAG = MemberWithdrawalFragment.class.getSimpleName();
    private TextView txtBalance, etDeduction, etPayableAmount, txtTitle,etTDS,txtTDS;
    private EditText etWithdrawalAmount;
    private Button btnSubmit;
    private ProgressDialog pd;
    private String strCharges="", strJsonResponse = "",strChargesRespone="",strYourBalance="",strPanResponse="",JsonResponse="",strWalletBalance="";
    private RelativeLayout relative_layout,rl_no_pancard;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_withdrawal,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtBalance = (TextView)getActivity().findViewById(R.id.txtBalance);
        etTDS= (TextView)getActivity().findViewById(R.id.etTDS);
        btnSubmit=(Button)getActivity().findViewById(R.id.btnSubmit);
        etPayableAmount = (TextView)getActivity().findViewById(R.id.etPayableAmount);
//        txtTitle = (TextView) toolbar_withdrawal.findViewById(R.id.txtTitle);
//        txtTitle.setText("Withdrawal Request");
//        txtTDS=(TextView)getActivity().findViewById(R.id.txtTDS);
        etWithdrawalAmount = (EditText)getActivity().findViewById(R.id.etWithdrawalAmount);
        relative_layout=(RelativeLayout)getActivity().findViewById(R.id.relative_layout);

        getCurrentBalance();
        getCharges();

        etWithdrawalAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String strValues=s.toString();

                if (strValues.equals("")){
                    etWithdrawalAmount.setHint("0");
                }
                else if (Integer.parseInt(strValues)<100){
                    Toast.makeText(getActivity(), "Minimum Withdrawal Amount is 100", Toast.LENGTH_SHORT).show();
                }
                else {

                    int intDeducationValues = Integer.parseInt(strValues) * 15 / 100;

                    int intTds=Integer.parseInt(etWithdrawalAmount.getText().toString()) * Integer.parseInt(strCharges)/100;
//                    etTDS.setText(String.valueOf(intTds));

//                    etDeduction.setText(String.valueOf(intDeducationValues));


                    int intTotal=intTds;
                    int intPayableAmount = Integer.parseInt(strValues) - intTotal;


                    etPayableAmount.setText(String.valueOf(intPayableAmount));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

//                if (s!=null){
//                    String strValues=s.toString();
//
//                    int intDeducationValues = Integer.parseInt(strValues) * 15 / 100;
//
//                    etDeduction.setText(String.valueOf(intDeducationValues));
//
//                    int intPayableAmount = Integer.parseInt(strValues) - intDeducationValues;
//
//                    etPayableAmount.setText(String.valueOf(intPayableAmount));
//
//                }

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strWithDrawalAmount = etWithdrawalAmount.getText().toString();

                if (StringUtils.isBlank(strWithDrawalAmount)) {
                    Toast.makeText(getActivity(), "Please Enter WithdrawalAmount values", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(strWithDrawalAmount)>Integer.parseInt(strWalletBalance) ) {
                    Toast.makeText(getActivity(), "Your Wallet Balance Is low", Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(strWithDrawalAmount)<100) {
                    Toast.makeText(getActivity(), "Minimum Withdrawal Amount 100 Rs.", Toast.LENGTH_SHORT).show();
                }else {

                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("mode", "MemberWithdrwalSubmit");
                        jsonObject.put("memberid", MemberHomeActivity.strMemberId);
                        jsonObject.put("amount", strWithDrawalAmount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (jsonObject.length() > 0) {
                        new sendWithAmount().execute(String.valueOf(jsonObject));
                    }

                }
            }
        });

    }

    private void getCharges() {

        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("mode","VendorWithdrwalCharge");
        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getCharges().execute(String.valueOf(jsonObject));
        }
    }

    private class sendWithAmount extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setIndeterminate(false);
            pd.setMessage("Please Wait....");
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
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
            if (pd.isShowing()) {
                pd.dismiss();
            }

            try {
                JSONObject jsonObject = new JSONObject(strJsonResponse);
                String strStatus = jsonObject.getString("status");
                String Messagge = jsonObject.getString("message");
                if (!Messagge.equals("No Data found")) {
                    JSONArray array = jsonObject.getJSONArray("response");
                    String strResponse=array.getString(0);
                    Toast.makeText(getActivity(), strResponse, Toast.LENGTH_SHORT).show();
                    etWithdrawalAmount.getText().clear();
                    etPayableAmount.setText(0);
//                    etTDS.setText(0);

                } else {
                    Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void getCurrentBalance() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mode", "getMemberBalance");
            jsonObject.put("memberid", MemberHomeActivity.strMemberId);
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
                txtBalance.setText("BALANCE " + strWalletBalance);
//                strCharges=jsonArray.getString(1);
//                Log.d(TAG, "withdrawal charges: "+strCharges);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class getCharges extends AsyncTask<String,String,String> {

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
                strChargesRespone = buffer.toString();
                Log.i(TAG, strChargesRespone);
                //send to post execute
                return strChargesRespone;
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

            return strChargesRespone;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                JSONObject Object = new JSONObject(strChargesRespone);
                String strStatus = Object.getString("status");
                Log.d(TAG, "status: " + strStatus);
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);
                JSONArray jsonArray=Object.getJSONArray("response");
                strCharges=jsonArray.getString(0);
                Log.d(TAG, "charges : "+strCharges);
//                String strTds=txtTDS.getText().toString();
//                txtTDS.setText(strTds +"("+strCharges+"%"+")");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
