package com.tornado.cphp.awhitepaid;

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
 * Created by cphp on 02-Jan-17.
 */

public class AccountStatementFragment  extends Fragment {

    private static final String TAG = WithdrawalReportFragment.class.getSimpleName();
    String strMemberid = "", strJsonResponse = "",prevalues="";

    ProgressDialog pd;
    ArrayList<String> listSrNo= new ArrayList<>();
    ArrayList<String> listDate= new ArrayList<>();
    ArrayList<String> listType= new ArrayList<>();
    ArrayList<String> listRemark= new ArrayList<>();
    ArrayList<String> listAdd= new ArrayList<>();
    ArrayList<String> listLess= new ArrayList<>();
    ArrayList<String> listWallettype= new ArrayList<>();
    ArrayList<String> listBalance= new ArrayList<>();
    ArrayList<String> Balance= new ArrayList<>();
    private ListView mListviewAccountStatementReport;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_statement,container,false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListviewAccountStatementReport = (ListView) getActivity().findViewById(R.id.mListviewAccountStatementReport);
        LayoutInflater layoutInflater=getActivity().getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.header_account_statement_list_item,null);
        mListviewAccountStatementReport.addHeaderView(view);
        getReportData();

    }

    private void getReportData() {
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("mode", "VendorAccountStatement");
            jsonObject.put("vendorid", VendorMainActivity.strVendorId);
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
            Log.d(TAG, "strJsonresponse: "+strJsonResponse);
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
                if (!Messagge.equalsIgnoreCase("")) {
                    JSONArray array = jsonObject.getJSONArray("response");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String strId=String.valueOf(i+1);
                        String strEntryDate= object.getString("Entry Date");
                        String MemberId=object.getString("memberid");
                        String strFromId=object.getString("fromid");
                        String strTransacType=object.getString("transtype");
                        String strAmount= object.getString("amount");
//                    String strWalletType= object.getString("wallettype");


                        listBalance.add(strAmount);
                        if (strAmount.contains("-")){
                            listLess.add(strAmount.replace("-",""));
                            listAdd.add("---");
                        }else {
                            listAdd.add(strAmount);
                            listLess.add("---");
                        }

                        if (i==0){
                            prevalues=listBalance.get(i);
                        }else {
                            prevalues=listBalance.get(i-1);
                        }

                        if (i==0){
                            Balance.add(strAmount);
                        }else {
                            float preValues=Float.parseFloat(Balance.get(i-1));
                            if (strAmount.contains("-")){
                                float finalValues=preValues-Float.parseFloat(listLess.get(i));
                                Balance.add(String.valueOf(finalValues));
                            }else {
                                float totlaValues=preValues+Float.parseFloat(strAmount);
                                Balance.add(String.valueOf(totlaValues));
                            }
                        }



                        listSrNo.add(strId);
                        listDate.add(strEntryDate);
                        listRemark.add("From:"+strFromId);
                        listType.add(strTransacType);
//                    listWallettype.add(strWalletType);
//                    if (!strAmount.contains("-")){
//                        listAdd.add(strAmount);
//                        listLess.add("---");
//                        if (i!=0){
//                            int balance=(Integer.parseInt(listBalance.get(i-1))-Integer.parseInt(strAmount));
//                            listBalance.add(String.valueOf(balance));
//                        }else {
//                            listBalance.add(strAmount);
//                        }
//                    }else {
//                        listLess.add(strAmount);
//                        listAdd.add("---");
//                        if (i!=0){
//                            int balance=(Integer.parseInt(listBalance.get(i-1))+Integer.parseInt(strAmount));
//                            listBalance.add(String.valueOf(balance));
//                        }else {
//                            listBalance.add(strAmount);
//                        }
//
//
//
//                    }

                    }

                    if (mListviewAccountStatementReport != null) {
                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), R.layout.list_item_account_statement, listSrNo);
                        mListviewAccountStatementReport.setAdapter(customAdapter);


                    } else {
                        Toast.makeText(getActivity(), "listview null", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                    if (mListviewAccountStatementReport != null) {
                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), R.layout.list_item_account_statement, listSrNo);
                        mListviewAccountStatementReport.setAdapter(customAdapter);


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

                holder.txtSrNo = (TextView) convertView.findViewById(R.id.txtSrNo);
                holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
                holder.txtType = (TextView) convertView.findViewById(R.id.txtType);
                holder.txtRemark = (TextView) convertView.findViewById(R.id.txtRemark);
                holder.txtAdd = (TextView) convertView.findViewById(R.id.txtAdd);
                holder.txtLess= (TextView) convertView.findViewById(R.id.txtLess);
//                holder.txtWallettype= (TextView) convertView.findViewById(R.id.txtWallettype);
                holder.txtBalance= (TextView) convertView.findViewById(R.id.txtBalance);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtSrNo.setText(items.get(position));
            holder.txtDate.setText(listDate.get(position));
            holder.txtType.setText(listType.get(position));
            holder.txtRemark.setText(listRemark.get(position));
            holder.txtAdd.setText(listAdd.get(position));
            holder.txtLess.setText(listLess.get(position));
//            holder.txtWallettype.setText(listWallettype.get(position));
            holder.txtBalance.setText(Balance.get(position));

            return convertView;

        }
    }

    private class ViewHolder {

        TextView txtSrNo, txtDate, txtType, txtRemark, txtAdd,txtLess,txtWallettype,txtBalance;
    }
}
