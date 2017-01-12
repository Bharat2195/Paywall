package com.tornado.cphp.paywall.memberpanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;
import com.tornado.cphp.paywall.QRCodeResultActivity;
import com.tornado.cphp.paywall.R;
import com.tornado.cphp.paywall.ScanCodeFragment;
import com.tornado.cphp.paywall.VendorMainActivity;
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

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by cphp on 03-Jan-17.
 */

public class MemberScanCodeFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private static final String TAG = MemberScanCodeFragment.class.getSimpleName();
    private ZXingScannerView mScannerView;
    //    private Button btnQrScanner;
    private String JsonResponse = "",strQRCodeResult="";
    private ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        strQRCodeResult = rawResult.getText();
        Log.d(TAG, "qrcode: " + strQRCodeResult);


        if (rawResult != null) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("mode", "getVendorOrMemberQrcode");
                jsonObject.put("vendorid", MemberDashboardActivity.strMemberId);
                jsonObject.put("qrcode", strQRCodeResult);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jsonObject.length() > 0) {
                new GetQRCodeDetail().execute(String.valueOf(jsonObject));
            }
        }
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(MemberScanCodeFragment.this);
            }
        }, 1000);

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    private class GetQRCodeDetail extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
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


            if (pd.isShowing()) {
                pd.dismiss();
            }

            try {
                JSONObject Object = new JSONObject(JsonResponse);
                String strStutus = Object.getString("status");
                Log.d(TAG, "status: " + strStutus);
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);
                if (strStutus.equals("1")) {
                    JSONArray jsonArray = Object.getJSONArray("response");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String strMemberId = jsonObject.getString("memberid");
                        Log.d(TAG, "memberid: " + strMemberId);
                        String strMemberName = jsonObject.getString("membername");
                        Log.d(TAG, "Membername :" + strMemberName);
                        Intent intent = new Intent(getActivity(), MemberQRCodeResultActivity.class);
                        intent.putExtra("memberId", strMemberId);
                        intent.putExtra("memberName", strMemberName);
                        intent.putExtra("QrCode", strQRCodeResult);
                        startActivity(intent);


                    }
                } else {
                    Toast.makeText(getActivity(), "Qrcode Is Not Valid", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
