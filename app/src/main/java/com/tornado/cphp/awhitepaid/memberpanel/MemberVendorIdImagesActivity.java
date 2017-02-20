package com.tornado.cphp.awhitepaid.memberpanel;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.adapter.CustomGrid;
import com.tornado.cphp.awhitepaid.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberVendorIdImagesActivity extends AppCompatActivity {

    private static final String TAG = MemberVendorIdImagesActivity.class.getSimpleName();
    private String strVendorId, strJsonResponse;
    private ArrayList<String> listImage = new ArrayList<>();
    private ArrayList<String> listImageName = new ArrayList<>();
    private ArrayList<String> listVendorId = new ArrayList<>();
    private ArrayList<String> listCategoryId = new ArrayList<>();
    private ArrayList<String> listProductName = new ArrayList<>();

    private ArrayList<String> listSliderImage = new ArrayList<>();
    private ArrayList<String> listSliderVendorId = new ArrayList<>();
    private ListView mListviewOperator;
    private TextView txtCancle;
    RequestQueue requestQueue;

    private Dialog dialog;

    private TextView txtTitle;
    private Toolbar mToolbarVendorImages;
    private ImageView imgSearching;

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static final Integer[] IMAGES = {R.drawable.bear, R.drawable.bonobo, R.drawable.eagle, R.drawable.horse};
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();

    CustomGrid adapter;


    private ProgressDialog pd;
    GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_vendor_id_images);

        getSupportActionBar().hide();


        requestQueue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        strVendorId = intent.getStringExtra("strVendorId");
        Log.d(TAG, "vendor id: " + strVendorId);


        mToolbarVendorImages = (Toolbar) findViewById(R.id.mToolbarVendorImages);
        txtTitle = (TextView) mToolbarVendorImages.findViewById(R.id.txtTitle);
        txtTitle.setText(strVendorId + " Images");
        mToolbarVendorImages.setNavigationIcon(R.drawable.back_icon);
        mToolbarVendorImages.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgSearching = (ImageView) mToolbarVendorImages.findViewById(R.id.imgSearching);
        imgSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchingCategoryWise();


            }
        });


        getImages();

        grid = (GridView) findViewById(R.id.grid);


    }

//    private void init() {
//        int i;
//        for (i = 0; i < 3; i++){
//            listSliderImage.add(listImage.get(i));
//            listSliderVendorId.add(listVendorId.get(i));
//
//        }
////            ImagesArray.add(IMAGES[i]);
//        mPager = (ViewPager) findViewById(R.id.pager);
//
//        mPager.setAdapter(new SlidingImage_Adapter(MemberVendorIdImagesActivity.this, listSliderImage, listSliderVendorId));
//
//
//        CirclePageIndicator indicator = (CirclePageIndicator)
//                findViewById(R.id.indicator);
//
//        indicator.setViewPager(mPager);
//
//        final float density = getResources().getDisplayMetrics().density;
//
////Set circle indicator radius
//        indicator.setRadius(5 * density);
//
//        NUM_PAGES = IMAGES.length;
//
//        // Auto start of viewpager
//        final Handler handler = new Handler();
//        final Runnable Update = new Runnable() {
//            public void run() {
//                if (currentPage == NUM_PAGES) {
//                    currentPage = 0;
//                }
//                mPager.setCurrentItem(currentPage++, true);
//            }
//        };
//        Timer swipeTimer = new Timer();
//        swipeTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(Update);
//            }
//        }, 3000, 3000);
//
//        // Pager listener over indicator
//        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int position) {
//                currentPage = position;
//
//            }
//
//            @Override
//            public void onPageScrolled(int pos, float arg1, int arg2) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int pos) {
//
//            }
//        });
//
//    }


    private void searchingCategoryWise() {

        dialog = new Dialog(MemberVendorIdImagesActivity.this);
        dialog.setTitle("Select Category");
        dialog.setContentView(R.layout.dialog_member_show_vendor_image);
        mListviewOperator = (ListView) dialog.findViewById(R.id.mListviewOperator);
        txtCancle = (TextView) dialog.findViewById(R.id.txtCancle);
        txtCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        getCategory();
        dialog.show();

    }

    private void getCategory() {

        Map<String, String> jsonParms = new HashMap<>();

        jsonParms.put("mode", "getVendoridForCategory");
        jsonParms.put("vendorid", strVendorId);
        listCategoryId.clear();
        listProductName.clear();


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, StringUtils.strBaseURL,
                new JSONObject(jsonParms),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "json Response: " + response);
                        try {

                            JSONArray jsonArray = response.getJSONArray("response");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String strCategoryId = jsonObject.getString("category_id");
                                String strProductName = jsonObject.getString("productname");

                                listCategoryId.add(strCategoryId);
                                listProductName.add(strProductName);

                            }



                            if (mListviewOperator != null) {
                                CustomAdapter customAdapter = new CustomAdapter(MemberVendorIdImagesActivity.this, R.layout.list_item_member_dialog_recharge_operator, listProductName);
                                mListviewOperator.setAdapter(customAdapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Handle Error
                        Log.d(TAG, "onErrorResponse: " + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };
        requestQueue.add(postRequest);
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

                holder.txtOperator = (TextView) convertView.findViewById(R.id.txtOperator);


                convertView.setTag(holder);


            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtOperator.setText(items.get(position));

            holder.txtOperator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strCategoryId = listCategoryId.get(position);
                    Log.d(TAG, "selcted category id: " + strCategoryId);
                    dialog.dismiss();
                    getImagesWithSearching(strCategoryId);


                }
            });

            return convertView;

        }
    }

    private void getImagesWithSearching(String strCategoryId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mode", "getProductAccrodingVendorAndCtegory");
            jsonObject.put("vendorid", strVendorId);
            jsonObject.put("category_id", strCategoryId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonObject.length() > 0) {
            new getImagesAsy().execute(String.valueOf(jsonObject));
        }
    }

    private class ViewHolder {

        TextView txtOperator;
    }


    private void getImages() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mode", "memberShowVendorImage");
            jsonObject.put("vendorid", strVendorId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonObject.length() > 0) {
            new getImagesAsy().execute(String.valueOf(jsonObject));
        }
    }

    private class getImagesAsy extends AsyncTask<String, String, String> {

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
            listVendorId.clear();
            listImage.clear();
            listImageName.clear();

            listSliderImage.clear();
            listSliderVendorId.clear();

            try {
                JSONObject jsonObject = new JSONObject(strJsonResponse);
                String strStatus = jsonObject.getString("status");
                String Messagge = jsonObject.getString("message");
                if (!strStatus.equals("3")) {
                    JSONArray array = jsonObject.getJSONArray("response");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String strImage = object.getString("image_name");
                        Log.d(TAG, "image: " + strImage);
                        String strImageName = object.getString("image_text");
                        String strVendorId = object.getString("vendorid");

                        listVendorId.add(strVendorId);
                        listImage.add(strImage);
                        listImageName.add(strImageName);
                    }

//                    init();
                    grid.setAdapter(null);
                    adapter = new CustomGrid(MemberVendorIdImagesActivity.this, listImage, listImageName, listVendorId);
                    grid.setAdapter(adapter);

                } else {
                    Toast.makeText(MemberVendorIdImagesActivity.this, "No Product Avaiables", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
