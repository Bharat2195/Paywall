package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * Created by cphp on 12-Jan-17.
 */
public class BinaryLevelSummary extends Fragment {

    private static final String TAG = NonWorkingIncomeFragment.class.getSimpleName();
    String strMemberid = "", strJsonResponse = "";

    ProgressDialog pd;
    ArrayList<String> listDetail= new ArrayList<>();
    ArrayList<String> listLevelNo= new ArrayList<>();
    ArrayList<String> listTotal= new ArrayList<>();
    private ListView mListviewBinaryLevelSummary;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_binary_levelsummary,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListviewBinaryLevelSummary = (ListView) getActivity().findViewById(R.id.mListviewBinaryLevelSummary);
        LayoutInflater layoutInflater=getActivity().getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.header_list_item_binary_level_summary,null);
        mListviewBinaryLevelSummary.addHeaderView(view);
        getReportData();
    }

    private void getReportData() {
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("mode", "MemberLevelSummary");
            jsonObject.put("memberid", MemberDashboardActivity.strMemberId);
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
                if (!strStatus.equals("2")){
                    JSONArray array = jsonObject.getJSONArray("response");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String strTotal=object.getString("NoofMember");
                        String strLevelNo= object.getString("levelno");

                        listDetail.add("Detail");
                        listTotal.add(strTotal);
                        listLevelNo.add(strLevelNo);

                    }

                    if (mListviewBinaryLevelSummary != null) {
                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), R.layout.list_item_binary_level_summary, listDetail);
                        mListviewBinaryLevelSummary.setAdapter(customAdapter);


                    } else {
                        Toast.makeText(getActivity(), "listview null", Toast.LENGTH_SHORT).show();
                    }

                }else {

                    Toast.makeText(getActivity(), "No data available", Toast.LENGTH_SHORT).show();
                    if (mListviewBinaryLevelSummary != null) {
                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), R.layout.list_item_binary_level_summary, listDetail);
                        mListviewBinaryLevelSummary.setAdapter(customAdapter);

                    } else {
                        Toast.makeText(getActivity(), "listview null", Toast.LENGTH_SHORT).show();
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

                holder.txtDetail= (TextView) convertView.findViewById(R.id.txtDetail);
                holder.txtLevelNo = (TextView) convertView.findViewById(R.id.txtLevelNo);
                holder.txtTotal = (TextView) convertView.findViewById(R.id.txtTotal);

                convertView.setTag(holder);


            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtDetail.setText(items.get(position));
            holder.txtLevelNo.setText(listLevelNo.get(position));
            holder.txtTotal.setText(listTotal.get(position));

            holder.txtDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(),DetailOfLevelSummary.class);
                    intent.putExtra("strName","level");
                    intent.putExtra("strLevelNo",listLevelNo.get(position));
                    startActivity(intent);
                }
            });

            return convertView;

        }
    }

    private class ViewHolder {

        TextView txtDetail,txtLevelNo, txtTotal;
    }


}
