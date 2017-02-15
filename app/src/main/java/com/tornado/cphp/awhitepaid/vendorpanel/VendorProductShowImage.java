package com.tornado.cphp.awhitepaid.vendorpanel;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoViewAttacher;

public class VendorProductShowImage extends AppCompatActivity {

    private static final String TAG=VendorProductShowImage.class.getSimpleName();

    PhotoViewAttacher mAttacher;

    String strImagePath,strId;
    @BindView(R.id.imgCancle)
    ImageView imgCancle;
    @BindView(R.id.relative_layout)
    RelativeLayout relativeLayout;
    @BindView(R.id.imgProduct)
    ImageView imgProduct;
    @BindView(R.id.imgDelete)
    ImageView imgDelete;
    RequestQueue requestQueue;
    Dialog dialog;

    private TextView txtYes,txtNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_product_show_image);
        ButterKnife.bind(this);

        getSupportActionBar().hide();

        requestQueue= Volley.newRequestQueue(this);

        imgCancle = (ImageView) findViewById(R.id.imgCancle);
        imgProduct = (ImageView) findViewById(R.id.imgProduct);

        Intent intent = getIntent();
        strImagePath = intent.getStringExtra("strImagePath");
        strId=intent.getStringExtra("strId");
        Picasso.with(getApplicationContext()).load(strImagePath).into(imgProduct);
        mAttacher = new PhotoViewAttacher(imgProduct);

    }

    @OnClick({R.id.imgCancle, R.id.imgDelete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgCancle:
                onBackPressed();
                break;
            case R.id.imgDelete:
                dialog = new Dialog(VendorProductShowImage.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.delete_product_dialog);
                txtNo = (TextView) dialog.findViewById(R.id.txtNo);
                txtYes = (TextView) dialog.findViewById(R.id.txtYes);
                dialog.show();
                txtNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                txtYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        deleteimage();
                    }
                });
                break;
        }
    }

    private void deleteimage() {
        Map<String,String> jsonParms=new HashMap<>();

        jsonParms.put("mode","vendorDeleteImageSubmit");
        jsonParms.put("vendorid",VendorMainActivity.strVendorId);
        jsonParms.put("id",strId);


        JsonObjectRequest postRequest = new JsonObjectRequest( Request.Method.POST, StringUtils.strBaseURL,
                new JSONObject(jsonParms),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "json Response: "+response);
                        try {
                            String strStatus=response.getString("status");
                            JSONArray jsonArray=response.getJSONArray("response");
                            Toast.makeText(VendorProductShowImage.this, jsonArray.get(0).toString(), Toast.LENGTH_SHORT).show();
                            if (strStatus.equals("2")){
                                Intent intent=new Intent(VendorProductShowImage.this,VendorUploadImageActivity.class);
                                intent.putExtra("tabindex","1");
                                startActivity(intent);
                            }
                            Log.d(TAG, "response "+jsonArray.get(0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try{

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Handle Error
                        Log.d(TAG, "onErrorResponse: "+error);
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

}
