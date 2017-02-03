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

public class DetailOfLevelSummary extends AppCompatActivity {

    private static final String TAG = DetailOfLevelSummary.class.getSimpleName();
    private String strLevelNo = "";
    String strMemberid = "", strJsonResponse = "",strName="";
    ProgressDialog pd;
    ArrayList<String> listMemberDetail = new ArrayList<>();
    ArrayList<String> listJoiningDate = new ArrayList<>();
    ArrayList<String> listActivationDate = new ArrayList<>();
    private ListView mListviewdDetailOflevelBinary;
    private Toolbar mToolbarDetailOfLevel;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_of_level_summary);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        strLevelNo = intent.getStringExtra("strLevelNo");
        strName=intent.getStringExtra("strName");
        Log.d(TAG, "level no: " + strLevelNo);

        mToolbarDetailOfLevel=(Toolbar)findViewById(R.id.mToolbarDetailOfLevel);
        mToolbarDetailOfLevel.setNavigationIcon(R.drawable.back_icon);
        mToolbarDetailOfLevel.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtTitle=(TextView)mToolbarDetailOfLevel.findViewById(R.id.txtTitle);
        if (strName.equals("Round")){
            txtTitle.setText("Detail Of Round"+"-"+strLevelNo);
        }else {
            txtTitle.setText("Detail Of Level"+"-"+strLevelNo);
        }


        mListviewdDetailOflevelBinary = (ListView) findViewById(R.id.mListviewdDetailOflevelBinary);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.header_list_item_detail_of_level_summary, null);
        mListviewdDetailOflevelBinary.addHeaderView(view);
        getReportData();
    }

    private void getReportData() {
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("mode", "MemberDetailLevelSummary");
            jsonObject.put("memberid", MemberDashboardActivity.strMemberId);
            jsonObject.put("levelno", strLevelNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject.length() > 0) {
            new GetReportJson().execute(String.valueOf(jsonObject));
        }
    }

    private class GetReportJson extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(DetailOfLevelSummary.this);
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
                if (!strStatus.equals("2")) {
                    JSONArray array = jsonObject.getJSONArray("response");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String strMemberID = object.getString("memberid");
                        String strMemberName = object.getString("membername");
                        String strEntryDate = object.getString("Entry Date");
                        String strActivationDate = object.getString("Activtion Date");
                        String strLevelNo = object.getString("levelno");
                        listMemberDetail.add(strMemberID + "-" + strMemberName);
                        listJoiningDate.add(strEntryDate);
                        listActivationDate.add(strActivationDate);


                    }

                    if (mListviewdDetailOflevelBinary != null) {
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), R.layout.list_item_detail_of_level_summary, listMemberDetail);
                        mListviewdDetailOflevelBinary.setAdapter(customAdapter);


                    } else {
                        Toast.makeText(getApplicationContext(), "listview null", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), "No data available", Toast.LENGTH_SHORT).show();
                    if (mListviewdDetailOflevelBinary != null) {
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), R.layout.list_item_detail_of_level_summary, listMemberDetail);
                        mListviewdDetailOflevelBinary.setAdapter(customAdapter);

                    } else {
                        Toast.makeText(getApplicationContext(), "listview null", Toast.LENGTH_SHORT).show();
                    }
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

                holder.txtMemberDetail = (TextView) convertView.findViewById(R.id.txtMemberDetail);
                holder.txtJoiningDate = (TextView) convertView.findViewById(R.id.txtJoiningDate);
                holder.txtActivationDate = (TextView) convertView.findViewById(R.id.txtActivationDate);


                convertView.setTag(holder);


            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtMemberDetail.setText(items.get(position));
            holder.txtJoiningDate.setText(listJoiningDate.get(position));
            holder.txtActivationDate.setText(listActivationDate.get(position));

            return convertView;

        }
    }


    private class ViewHolder {

        TextView txtMemberDetail, txtJoiningDate, txtActivationDate;
    }

}
