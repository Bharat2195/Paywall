package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

public class MemberShowVendorInformationActivity extends AppCompatActivity {

    private static final String TAG=MemberShowVendorInformationActivity.class.getSimpleName();
    private Toolbar mToolbarVendroShowInformation;
    private TextView txtTitle;
    private ListView mListviewVendroShowInformation;
    private ArrayList<String> listVendorName=new ArrayList<>();
    private ArrayList<String> listVendorLatLag=new ArrayList<>();
    String strMemberid = "", strJsonResponse = "";

    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_show_vendor_information);
        getSupportActionBar().hide();

        mToolbarVendroShowInformation=(Toolbar)findViewById(R.id.mToolbarVendroShowInformation);
        txtTitle=(TextView)mToolbarVendroShowInformation.findViewById(R.id.txtTitle);
        txtTitle.setText("Vendor Information");
        mToolbarVendroShowInformation.setNavigationIcon(R.drawable.back_icon);
        mToolbarVendroShowInformation.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mListviewVendroShowInformation= (ListView) findViewById(R.id.mListviewVendroShowInformation);
        getReportData();




    }

    private void getReportData() {

        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("mode","memberShowVendor");
        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getShowVendorInformation().execute(String.valueOf(jsonObject));
        }
    }

    private class getShowVendorInformation extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MemberShowVendorInformationActivity.this);
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
                JSONArray array = jsonObject.getJSONArray("response");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String strVendorId=object.getString("vendorid");
                    Log.d(TAG, "vendord id: "+strVendorId);
//                    String strLatLag=object.getString("latitude_and_longitude");1
                    listVendorName.add(strVendorId);
//                    listVendorLatLag.add(strLatLag);


                }

                if (mListviewVendroShowInformation != null) {
                    CustomAdapter customAdapter = new CustomAdapter(MemberShowVendorInformationActivity.this, R.layout.list_item_vendor_show_information, listVendorName);
                    mListviewVendroShowInformation.setAdapter(customAdapter);


                } else {
                    Toast.makeText(getApplicationContext(), "listview null", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
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

                holder.txtVendorId = (TextView) convertView.findViewById(R.id.txtVendorId);


                convertView.setTag(holder);


            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtVendorId.setText(items.get(position));
            holder.txtVendorId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MemberShowVendorInformationActivity.this,MemberVendorIdImagesActivity.class);
                    intent.putExtra("strVendorId",items.get(position));
                    startActivity(intent);
                }
            });

            return convertView;

        }
    }

    private class ViewHolder {

        TextView txtVendorId;
    }

}
