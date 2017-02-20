package com.tornado.cphp.awhitepaid.vendorfragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.adapter.ShowImageAdapter;
import com.tornado.cphp.awhitepaid.utils.StringUtils;
import com.tornado.cphp.awhitepaid.vendorpanel.VendorHomeAcivity;
import com.tornado.cphp.awhitepaid.vendorpanel.VendorUploadImageActivity;

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

/**.
 * Created by cphp on 28-Dec-16.
 */
public class ShowImageVenderFragment extends Fragment {

    private static final String TAG=ShowImageVenderFragment.class.getSimpleName();
    private ProgressDialog pd;
    private RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private StaggeredGridLayoutManager gridLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayAdapter<String> adapter;
    private String strJsonResponse="";
    private ArrayList<String> listId=new ArrayList<>();
    private ArrayList<String> listEntryDate=new ArrayList<>();
    private ArrayList<String> listVendorId=new ArrayList<>();
    private ArrayList<String> listImageText=new ArrayList<>();
    private ArrayList<String> listImagePath=new ArrayList<>();
    private SearchView mSearch;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View viewShow=inflater.inflate(R.layout.fragment_show_image,container,false);
        return viewShow;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        mSwipeRefreshLayout = (SwipeRefreshLayout)getActivity().findViewById(R.id.swifeRefresh);
//        mSwipeRefreshLayout.setColorSchemeColors(Color.GRAY, Color.GREEN, Color.BLUE,
//                Color.RED, Color.CYAN);
//        mSwipeRefreshLayout.setDistanceToTriggerSync(20);// in dips
//        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);// LARGE also can be used
//        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));


        mSearch= VendorUploadImageActivity.clickEvent();
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "search clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView = (RecyclerView)getActivity().findViewById(R.id.recycler_view);
//        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        gridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);



//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//                JSONObject jsonObject=new JSONObject();
//                try{
//                    jsonObject.put("mode","vendorImageShow");
//                    jsonObject.put("vendorid",VendorMainActivity.strVendorId);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                if (jsonObject.length()>0){
//                    new getImageshow().execute(String.valueOf(jsonObject));
//                }
//
//            }
//        });

        getImage();







    }

    private void getImage() {

        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("mode","vendorImageShow");
            jsonObject.put("vendorid", VendorHomeAcivity.strVendorId);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (jsonObject.length()>0){
            new getImageshow().execute(String.valueOf(jsonObject));
        }

        searchingData(mSearch);






    }

    private void searchingData(SearchView mSearch) {

        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final ArrayList<String> filterList=new ArrayList<String>();

                for (int i=0; i<listImageText.size(); i++){

                    final  String text=listImageText.get(i).toLowerCase();
                    if (text.contains(newText.toLowerCase())){
                        filterList.add(listImageText.get(i));
                    }
                }

                gridLayoutManager = new StaggeredGridLayoutManager(2, 1);
                mRecyclerView.setLayoutManager(gridLayoutManager);

                mAdapter = new ShowImageAdapter(getActivity(),listImageText,listEntryDate,listImagePath,listId);
                mRecyclerView.setAdapter(mAdapter);
                return false;
            }
        });
    }




    @Override
    public void onResume() {
        super.onResume();
//        getImage();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    private class getImageshow extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(getActivity());
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

//                    Bitmap bitmapImagePath=decodeFromBase64ToBitmap(strImagePath);
//                    Log.d(TAG, "bitmapImagePath: "+bitmapImagePath);

                    listId.add(strId);
                    listVendorId.add(strVendorId);
                    listImageText.add(strImageName);
                    listImagePath.add(strImagePath);
                    listEntryDate.add(strEntryDate);
                }

                    mAdapter = new ShowImageAdapter(getActivity(),listImageText,listEntryDate,listImagePath,listId);
                    mRecyclerView.setAdapter(mAdapter);
//                    mSwipeRefreshLayout.setRefreshing(false);
//                    mRecyclerView.scrollToPosition(0);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap decodeFromBase64ToBitmap(String encodedImage)

    {

        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;

    }

}
