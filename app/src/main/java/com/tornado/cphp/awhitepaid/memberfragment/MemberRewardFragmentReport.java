package com.tornado.cphp.awhitepaid.memberfragment;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.tornado.cphp.awhitepaid.memberpanel.MemberHomeActivity;
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
 * Created by bharat_android on 2/10/2017.
 */

public class MemberRewardFragmentReport extends Fragment {

    private static final String TAG = MemberRewardFragmentReport.class.getSimpleName();
    String strMemberid = "", strJsonResponse = "";

    ProgressDialog pd;
    ArrayList<String> listEntryDate= new ArrayList<>();
    ArrayList<String> listAmount= new ArrayList<>();
    ArrayList<String> listReward= new ArrayList<>();
    private ListView mListviewRewardReport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reward_report,container,false);

    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListviewRewardReport = (ListView) getActivity().findViewById(R.id.mListviewRewardReport);
        LayoutInflater layoutInflater=getActivity().getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.header_list_item_member_reward_report,null);
        mListviewRewardReport.addHeaderView(view);
        getReportData();
    }

    private void getReportData() {
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("mode", "memberrewardreport");
            jsonObject.put("memberid", MemberHomeActivity.strMemberId);
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
                        String strEntryDate= object.getString("Entry Date");
                        String strAmount= object.getString("amount");
                        String strReward= object.getString("reward");

                        listEntryDate.add(strEntryDate);
                        listAmount.add(strAmount);
                        listReward.add(strReward);


                    }

                    if (mListviewRewardReport != null) {
                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), R.layout.list_item_member_reward_report, listEntryDate);
                        mListviewRewardReport.setAdapter(customAdapter);


                    } else {
                        Toast.makeText(getActivity(), "listview null", Toast.LENGTH_SHORT).show();
                    }

                }else {

                    Toast.makeText(getActivity(), "No data available", Toast.LENGTH_SHORT).show();
                    if (mListviewRewardReport != null) {
                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), R.layout.list_item_member_reward_report, listEntryDate);
                        mListviewRewardReport.setAdapter(customAdapter);

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
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(resource, null);

                holder.txtEntryDate= (TextView) convertView.findViewById(R.id.txtEntryDate);
                holder.txtReward = (TextView) convertView.findViewById(R.id.txtReward);
                holder.txtAmount = (TextView) convertView.findViewById(R.id.txtAmount);


                convertView.setTag(holder);


            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtEntryDate.setText(items.get(position));
            holder.txtReward.setText(listReward.get(position));
            holder.txtAmount.setText(listAmount.get(position));

            return convertView;

        }
    }


    private class ViewHolder {

        TextView txtEntryDate,txtReward, txtAmount;
    }



}
