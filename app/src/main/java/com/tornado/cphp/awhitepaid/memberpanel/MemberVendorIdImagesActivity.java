package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.adapter.CustomGrid;
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

public class MemberVendorIdImagesActivity extends AppCompatActivity {

    private static final String TAG=MemberVendorIdImagesActivity.class.getSimpleName();
    private String strVendorId,strJsonResponse;

    private ArrayList<String> listImage=new ArrayList<>();
    private ArrayList<String> listImageName=new ArrayList<>();
    private ArrayList<String> listVendorId=new ArrayList<>();

    private TextView txtTitle;
    private Toolbar mToolbarVendorImages;


    private ProgressDialog pd;
    GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_vendor_id_images);

        getSupportActionBar().hide();

        Intent intent=getIntent();
        strVendorId=intent.getStringExtra("strVendorId");
        Log.d(TAG, "vendor id: "+strVendorId);


        mToolbarVendorImages= (Toolbar) findViewById(R.id.mToolbarVendorImages);
        txtTitle= (TextView) mToolbarVendorImages.findViewById(R.id.txtTitle);
        txtTitle.setText(strVendorId+" Images");
        mToolbarVendorImages.setNavigationIcon(R.drawable.back_icon);
        mToolbarVendorImages.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        getImages();

        grid = (GridView)findViewById(R.id.grid);
    }

    private void getImages() {

        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("mode","memberShowVendorImage");
            jsonObject.put("vendorid",strVendorId);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getImagesAsy().execute(String.valueOf(jsonObject));
        }
    }

    private class getImagesAsy extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MemberVendorIdImagesActivity.this);
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
                if (!strStatus.equals("3")){
                    JSONArray array = jsonObject.getJSONArray("response");
                    for (int i=0; i< array.length(); i++){
                        JSONObject object=array.getJSONObject(i);
                        String strImage=object.getString("image_name");
                        Log.d(TAG, "image: "+strImage);
                        String strImageName=object.getString("image_text");
                        String strVendorId=object.getString("vendorid");

                        listVendorId.add(strVendorId);
                        listImage.add(strImage);
                        listImageName.add(strImageName);
                    }
                    CustomGrid adapter = new CustomGrid(MemberVendorIdImagesActivity.this, listImage, listImageName, listVendorId);
                    grid.setAdapter(adapter);
                }else {
                    Toast.makeText(MemberVendorIdImagesActivity.this, "No Product Avaiables", Toast.LENGTH_SHORT).show();
                }



            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
