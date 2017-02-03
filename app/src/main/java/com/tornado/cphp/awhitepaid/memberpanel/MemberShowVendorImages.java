package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.adapter.ShowImageAdapter;
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

public class MemberShowVendorImages extends AppCompatActivity {

    private static final String TAG=MemberShowVendorImages.class.getSimpleName();
    private ProgressDialog pd;
    private RecyclerView mRecylerviewVendorImage;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private StaggeredGridLayoutManager gridLayoutManager;
    ArrayAdapter<String> adapter;
    private String strJsonResponse="";
    private ArrayList<String> listId=new ArrayList<>();
    private ArrayList<String> listEntryDate=new ArrayList<>();
    private ArrayList<String> listVendorId=new ArrayList<>();
    private ArrayList<String> listImageText=new ArrayList<>();
    private ArrayList<String> listImagePath=new ArrayList<>();
    private Toolbar mToolbarMembershowImage;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_show_vendor_images);
        getSupportActionBar().hide();
        mToolbarMembershowImage=(Toolbar)findViewById(R.id.mToolbarMembershowImage);
        txtTitle= (TextView) mToolbarMembershowImage.findViewById(R.id.txtTitle);
        txtTitle.setText("Vendor Images");
        mToolbarMembershowImage.setNavigationIcon(R.drawable.back_icon);
        mToolbarMembershowImage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mRecylerviewVendorImage = (RecyclerView)findViewById(R.id.mRecylerviewVendorImage);
//        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        gridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecylerviewVendorImage.setLayoutManager(gridLayoutManager);

        getImage();
    }

    private void getImage() {

        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("mode","memberShowVendorImage");
        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
           new getImageshow().execute(String.valueOf(jsonObject));
        }

    }

    private class getImageshow extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(MemberShowVendorImages.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(false);
            pd.setCanceledOnTouchOutside(false);
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
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                strJsonResponse = buffer.toString();
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

            Log.d(TAG, "json response: "+strJsonResponse);
            return strJsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            listEntryDate.clear();
            listImagePath.clear();
            listImageText.clear();
            listVendorId.clear();

            if (pd.isShowing()){
                pd.dismiss();
            }
            try {

                JSONObject jsonObject = new JSONObject(strJsonResponse);
                String strStatus=jsonObject.getString("status");
                String strMessage=jsonObject.getString("message");
                JSONArray jsonArray=jsonObject.getJSONArray("response");
                for (int i=0; i<jsonArray.length(); i++){
                    JSONObject object=jsonArray.getJSONObject(i);
                    String strId=object.getString("id");
                    String strEntryDate=object.getString("entrydate");
                    String strVendorId=object.getString("vendorid");
                    String strImageName=object.getString("image_text");
                    String strImagePath=object.getString("image_name");

                    Log.d(TAG, "image path: "+strImagePath);


                    listId.add(strId);
                    listVendorId.add(strVendorId);
                    listImageText.add(strImageName);
                    listImagePath.add(strImagePath);
                    listEntryDate.add(strEntryDate);
                }

                mAdapter = new ShowImageAdapter(getApplicationContext(),listEntryDate,listImageText,listImagePath,listId);
                mRecylerviewVendorImage.setAdapter(mAdapter);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
