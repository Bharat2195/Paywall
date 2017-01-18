package com.tornado.cphp.paywall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tornado.cphp.paywall.adapter.ImageUploadSearverActivity;
import com.tornado.cphp.paywall.config.RequestHandler;
import com.tornado.cphp.paywall.utils.StringUtils;

import net.gotev.uploadservice.Logger;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.tornado.cphp.paywall.R.id.editText;
import static com.tornado.cphp.paywall.utils.StringUtils.UPLOAD_URL;

/**
 * Created by cphp on 28-Dec-16.
 */
public class ImageUploadVenderFragment extends Fragment implements View.OnClickListener {

    private Button buttonChoose;
    private Button buttonUpload;

    public static final String UPLOAD_KEY = "image";
    private ImageView imageView;

    private EditText editTextName;

    private Bitmap bitmap;
    String imagePath;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    private static final int REQUEST_IMAGE = 100;

    File destination;

    int serverResponseCode = 0;
    ProgressDialog dialog = null;

    String upLoadServerUri = null;

    ProgressDialog loading;
    private String strImageName="";
    private String JsonResponse = "";

    private static final String TAG = ImageUploadVenderFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewUpload = inflater.inflate(R.layout.fragment_image_upload, container, false);
        return viewUpload;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        upLoadServerUri =StringUtils.UPLOAD_URL;

        buttonChoose = (Button) getActivity().findViewById(R.id.buttonChoose);
        buttonUpload = (Button) getActivity().findViewById(R.id.buttonUpload);

        editTextName = (EditText) getActivity().findViewById(editText);

        imageView = (ImageView) getActivity().findViewById(R.id.imageView);

        requestStoragePermission();

        String name = dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");

        strImageName=editTextName.getText().toString() + ".jpg";


        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);


    }

    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onClick(View v) {

        if (v == buttonChoose) {
            showFileChooser();
        }

        if (v == buttonUpload) {
//            uploadImage();
////            uploadtoserverImage();
            uploadMultipart();
        }

    }

    public void uploadMultipart() {
//        //getting name for the image
//        String name = editTextName.getText().toString().trim();
//
//        //getting the actual path of the image
//        String path = getPath(filePath);
//
//        Log.d(TAG, "image path actual: " + path);
//
//        //Uploading code
//        try {
//            String uploadId = UUID.randomUUID().toString();
//
//            //Creating a multi part request
//          String strResponse=  new MultipartUploadRequest(getActivity(), uploadId, StringUtils.UPLOAD_URL)
//                    .addFileToUpload(path, "image") //Adding file
////                    .addParameter("name", name) //Adding text parameter to the request
//                    .setNotificationConfig(new UploadNotificationConfig())
//                    .setMaxRetries(2)
//                    .startUpload(); //Starting the upload
//            Log.d(TAG, "uploadMultipart response: "+strResponse);
//
//        } catch (Exception exc) {
//            Toast.makeText(getActivity(), exc.getMessage(), Toast.LENGTH_SHORT).show();
//        }

        dialog = ProgressDialog.show(getActivity(), "", "Uploading file...", true);

        new Thread(new Runnable() {
            public void run() {
                uploadFile(imagePath);
            }
        }).start();


        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("mode", "vendorImageUploadSubmit");
            jsonObject.put("vendorid", "disha");
            jsonObject.put("image_name", imagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject.length() > 0) {
            new uploadImage().execute(String.valueOf(jsonObject));
        }
    }

    //method to get the file path from uri
//    public String getPath(Uri uri) {
//        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        String document_id = cursor.getString(0);
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//        cursor.close();
//
//        cursor = getActivity().getContentResolver().query(
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        cursor.moveToFirst();
//        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//        cursor.close();
//
//        return path;
//    }


//    private void uploadtoserverImage() {
//
//        class UploadImage extends AsyncTask<Bitmap, Void, String> {
//
//            ProgressDialog loading;
//            RequestHandler rh = new RequestHandler();
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                loading = ProgressDialog.show(getActivity(), "Uploading Image", "Please wait...", true, true);
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                loading.dismiss();
//                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            protected String doInBackground(Bitmap... params) {
//                Bitmap bitmap = params[0];
//                String uploadImage = getStringImage(bitmap);
//
//                HashMap<String, String> data = new HashMap<>();
//                data.put(UPLOAD_KEY, uploadImage);
//
//                String result = rh.sendPostRequest(UPLOAD_URL, data);
//
//                return result;
//            }
//        }
//
//        UploadImage ui = new UploadImage();
//        ui.execute(bitmap);
//    }

    private void showFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodedImage;
    }
    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;



        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :" +imagePath);
            return 0;
        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("image_name", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"image_name\";filename="+ fileName + "" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(getActivity(), "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(getActivity(), "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(getActivity(), "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK ) {
//            filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
//                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
//                //Setting the Bitmap to ImageView
//                imageView.setImageBitmap(bitmap);
                FileInputStream in = new FileInputStream(destination);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                imagePath = destination.getAbsolutePath();
                //tvPath.setText(imagePath);
                Log.d("INFO", "PATH === " +imagePath);
                Bitmap bmp = BitmapFactory.decodeStream(in, null, options);
                imageView.setImageBitmap(bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadImage() {
        //Showing the progress dialog
//        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);

        String name = editTextName.getText().toString().trim();
        Log.d(TAG, "image name: " + name);

        String image = getStringImage(bitmap);
        Log.d(TAG, "base64Image: " + image);


        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("mode", "vendorImageUploadSubmit");
            jsonObject.put("vendorid", VendorMainActivity.strVendorId);
            jsonObject.put("image_text", name);
            jsonObject.put("image_name", image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject.length() > 0) {
            new uploadImage().execute(String.valueOf(jsonObject));
        }

//        StringRequest stringRequest = new StringRequest(Request.Method.POST, StringUtils.strBaseURL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        //Disimissing the progress dialog
//                        loading.dismiss();
//                        //Showing toast message of the response
//                        Log.e(TAG, "response: "+s);
//                        Toast.makeText(getActivity(), s , Toast.LENGTH_LONG).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Dismissing the progress dialog
//                        loading.dismiss();
//
//                        //Showing toast
//                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
//                    }
//                }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                //Converting Bitmap to String
//                String image = getStringImage(bitmap);
//                Log.d(TAG, "base64Image: "+image);
//
//                //Getting Image Name
//                String name = editTextName.getText().toString().trim();
//                Log.d(TAG, "image name: "+name);
//
//                //Creating parameters
//                Map<String,String> params = new Hashtable<String, String>();
//
//                //Adding parameters
//                params.put("mode",KEY_MODE);
//                params.put(KEY_VENDORID,VendorMainActivity.strVendorId);
//                params.put(KEY_IMAGE_NAME,name);
//                params.put(KEY_BASE,image);
//
//                //returning parameters
//                return params;
//            }
//        };

        //Creating a Request Queue
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//
//        //Adding request to the queue
//        requestQueue.add(stringRequest);
    }

    private class uploadImage extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), "Uploading...", "Please wait...", false, false);
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
                JsonResponse = buffer.toString();
                Log.i(TAG, JsonResponse);
                //send to post execute
                return JsonResponse;
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

            return JsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (loading.isShowing()) {
                loading.dismiss();
            }

            try {
                JSONObject Object = new JSONObject(JsonResponse);
                String strStutus = Object.getString("status");
                Log.d(TAG, "status: " + strStutus);
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);


                JSONArray jsonArray = Object.getJSONArray("response");
                String strResponse = jsonArray.getString(0);
                Toast.makeText(getActivity(), strResponse, Toast.LENGTH_SHORT).show();
                editTextName.getText().clear();
                imageView.setImageBitmap(null);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
