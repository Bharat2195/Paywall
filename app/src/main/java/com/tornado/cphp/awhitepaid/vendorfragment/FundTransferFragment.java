package com.tornado.cphp.awhitepaid.vendorfragment;

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
import com.tornado.cphp.awhitepaid.utils.StringUtils;
import com.tornado.cphp.awhitepaid.vendorpanel.VendorHomeAcivity;

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
 * Created by cphp on 02-Jan-17.
 */

public class FundTransferFragment extends Fragment {

    private static final String TAG = WithdrawalReportFragment.class.getSimpleName();
    String strMemberid = "", strJsonResponse = "";

    ProgressDialog pd;
    ArrayList<String> listEntryDate= new ArrayList<>();
    ArrayList<String> listToId= new ArrayList<>();
    ArrayList<String> listAmount= new ArrayList<>();
    ArrayList<String> listCharges= new ArrayList<>();
    ArrayList<String> listNetAmount= new ArrayList<>();
    ArrayList<String> listTime= new ArrayList<>();
    private ListView mListviewFundtRansferReport;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fundtransfer,container,false);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mListviewFundtRansferReport = (ListView) getActivity().findViewById(R.id.mListviewFundtRansferReport);
        LayoutInflater layoutInflater=getActivity().getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.header_fund_transfer_list_item,null);
        mListviewFundtRansferReport.addHeaderView(view);

        getReportData();
    }

    private void getReportData() {
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("mode", "VendorFundtransferReport");
            jsonObject.put("vendorid", VendorHomeAcivity.strVendorId);
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
                if (!Messagge.equalsIgnoreCase("No Data found")){
                    JSONArray array = jsonObject.getJSONArray("response");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String strId=object.getString("id");
                        String strEntryDate=object.getString("Entry Date");
                        String strFromId=object.getString("fromid");
                        String strToId= object.getString("toid");
                        String strAmount= object.getString("amount");
                        String strCharges= object.getString("charges");
                        String strNetAmount= object.getString("netamount");
                        String strType= object.getString("type");
                        String strDateTime=object.getString("entrydate");

                        String strTime=StringUtils.getTime(strDateTime);
                        Log.d(TAG, "time: "+strTime);
                        listTime.add(strTime);


                        listEntryDate.add(strEntryDate);
                        listToId.add(strToId);
                        listAmount.add(strAmount);
                        listCharges.add(strCharges);
                        listNetAmount.add(strNetAmount);


                    }

                    if (mListviewFundtRansferReport != null) {
                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), R.layout.list_item_fund_transfer_report, listEntryDate);
                        mListviewFundtRansferReport.setAdapter(customAdapter);


                    } else {
                        Toast.makeText(getActivity(), "listview null", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                    if (mListviewFundtRansferReport != null) {
                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), R.layout.list_item_fund_transfer_report, listEntryDate);
                        mListviewFundtRansferReport.setAdapter(customAdapter);


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

                holder.txtEntryDate = (TextView) convertView.findViewById(R.id.txtEntryDate);
                holder.txtToId = (TextView) convertView.findViewById(R.id.txtToId);
                holder.txtAmount = (TextView) convertView.findViewById(R.id.txtAmount);
                holder.txtCharges = (TextView) convertView.findViewById(R.id.txtCharges);
                holder.txtAmount = (TextView) convertView.findViewById(R.id.txtAmount);
                holder.txtNetAmount= (TextView) convertView.findViewById(R.id.txtNetAmount);
                holder.txtTime= (TextView) convertView.findViewById(R.id.txtTime);


                convertView.setTag(holder);


            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtEntryDate.setText(items.get(position));
            holder.txtToId.setText(listToId.get(position));
            holder.txtAmount.setText(listAmount.get(position));
            holder.txtCharges.setText(listCharges.get(position));
            holder.txtNetAmount.setText(listNetAmount.get(position));
            holder.txtTime.setText(listTime.get(position));

            return convertView;

        }
    }

    private class ViewHolder {

        TextView txtEntryDate, txtToId, txtAmount, txtCharges, txtNetAmount,txtTime;
    }

}
