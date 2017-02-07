package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
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
 * Created by cphp on 04-Jan-17.
 */

public class MemberShowCodeFragment extends Fragment {


    private static final String TAG=MemberShowCodeFragment.class.getSimpleName();
    private String strQRCodeResponse="",strQRCode="";
    private Bitmap bitmap;
    public final static int QRcodeWidth = 500;
    private ImageView img_qr_code_image;
    private ProgressDialog pd;
    private TextView txtQRCodeTitle;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_showcode,container,false);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        img_qr_code_image=(ImageView)getActivity().findViewById(R.id.img_qr_code_image);
        txtQRCodeTitle=(TextView)getActivity().findViewById(R.id.txtQRCodeTitle);
        txtQRCodeTitle.setText("Scan AWhitePaid code to make payment to "+ MemberHomeActivity.strMemberId);


        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("mode","memberQRcode");
            Log.d(TAG, "member id: "+MemberHomeActivity.strMemberId);
            jsonObject.put("memberid", MemberHomeActivity.strMemberId);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getQRCode().execute(String.valueOf(jsonObject));
        }
    }

    private class getQRCode  extends AsyncTask<String,String,String> {


//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pd=new ProgressDialog(getActivity());
//            pd.setMessage("Please Wait...");
//            pd.setCancelable(false);
//            pd.setIndeterminate(false);
//            pd.setCanceledOnTouchOutside(false);
//            pd.show();
//        }

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
//            if (pd.isShowing()){
//                pd.dismiss();
//            }

            try {
                JSONObject Object = new JSONObject(strQRCodeResponse);
                String strStutus=Object.getString("status");
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);
                JSONArray jsonArray = Object.getJSONArray("response");
                strQRCode=jsonArray.getString(0);
                Log.d(TAG, "QRCode: "+strQRCode);

                bitmap=TextToImageEncode(strQRCode);
                img_qr_code_image.setImageBitmap(bitmap);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private Bitmap TextToImageEncode(String strQRCode) throws WriterException {
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
