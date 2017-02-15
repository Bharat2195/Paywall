package com.tornado.cphp.awhitepaid.vendorfragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.vendorpanel.VendorHomeAcivity;
import com.tornado.cphp.awhitepaid.utils.FilePath;
import com.tornado.cphp.awhitepaid.utils.StringUtils;

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
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by cphp on 28-Dec-16.
 */
public class ImageUploadVenderFragment extends Fragment implements View.OnClickListener {

    private Button buttonChoose;
    private Button buttonUpload;

    public static final String UPLOAD_KEY = "image";
    private ImageView imageView;

    private EditText etVendorImageName;

    private Bitmap bitmap;
    String imagePath;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    private static final int REQUEST_IMAGE = 100;
    private String selectedFilePath;

    File destination;

    int serverResponseCode = 0;
    ProgressDialog dialog = null;

    String upLoadServerUri = null;

    ProgressDialog loading;
    private String strImageName;
    private String JsonResponse = "", strImageCategoryResponse = "",strSelectedCategory="",strCategoryId="";

    private Spinner spnImageCategory;
    private ArrayList<String> listImageCategoryName = new ArrayList<>();
    private ArrayList<String> listImageCategoryId = new ArrayList<>();

    private static final String TAG = ImageUploadVenderFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewUpload = inflater.inflate(R.layout.fragment_image_upload, container, false);
        return viewUpload;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        upLoadServerUri = StringUtils.UPLOAD_URL;

        buttonChoose = (Button) getActivity().findViewById(R.id.buttonChoose);
        buttonUpload = (Button) getActivity().findViewById(R.id.buttonUpload);
        spnImageCategory = (Spinner) getActivity().findViewById(R.id.spnImageCategory);


        etVendorImageName = (EditText) getActivity().findViewById(R.id.etVendorImageName);


        imageView = (ImageView) getActivity().findViewById(R.id.imageView);

        getImageCategoryName();



        requestStoragePermission();

        String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");


//        strSelectedCategory = spnImageCategory.getSelectedItem().toString();
//        Log.d(TAG, "spinner selected category: " + strSelectedCategory);




        spnImageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                strSelectedCategory=parent.getItemAtPosition(position).toString();
                Log.d(TAG, "spinner selected category: " + strSelectedCategory);

                strCategoryId=listImageCategoryId.get(position);
                Log.d(TAG, "list imagecategory id: "+strCategoryId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);


    }

    private void getImageCategoryName() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mode", "showProductCategory");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonObject.length() > 0) {
            new getImageCategory().execute(String.valueOf(jsonObject));
        }
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

//        dialog = ProgressDialog.show(getActivity(), "", "Uploading file...", true);
//
//        new Thread(new Runnable() {
//            public void run() {
//                uploadFile(imagePath);
//            }
//        }).start();

        strImageName = etVendorImageName.getText().toString();
        Log.d(TAG, "imagename: " + strImageName);



        if (StringUtils.isBlank(strSelectedCategory)) {
            Toast.makeText(getActivity(), "Please Select Category", Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(strImageName)) {
            Toast.makeText(getActivity(), "Please Enter Image Name", Toast.LENGTH_SHORT).show();
        } else {

            if (selectedFilePath != null) {
                dialog = ProgressDialog.show(getActivity(), "", "Uploading File...", true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //creating new thread to handle Http Operations
                        uploadFile(selectedFilePath);
                    }
                }).start();
            } else {
                Toast.makeText(getActivity(), "Please choose a File First", Toast.LENGTH_SHORT).show();
            }


            Log.d(TAG, "vendrod id: " + VendorHomeAcivity.strVendorId);
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("mode", "vendorImageUploadSubmit");
                jsonObject.put("vendorid", VendorHomeAcivity.strVendorId);
                jsonObject.put("image_text", strImageName);
                jsonObject.put("image_name", selectedFilePath);
                jsonObject.put("category_id",strCategoryId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (jsonObject.length() > 0) {
                new uploadImage().execute(String.valueOf(jsonObject));
            }
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
//        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodedImage;
    }

    public int uploadFile(final String selectedFilePath) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            dialog.dismiss();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });

            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(StringUtils.UPLOAD_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("image_name", selectedFilePath);
                connection.setRequestProperty("image_text", strImageName);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"image_name\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "File Upload Completed", Toast.LENGTH_SHORT).show();
//                            tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/"+ fileName);
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            return serverResponseCode;
        }

    }

//    public int uploadFile(String sourceFileUri) {
//
//        String fileName = sourceFileUri;
//
//
//
//        HttpURLConnection conn = null;
//        DataOutputStream dos = null;
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1 * 1024 * 1024;
//        File sourceFile = new File(sourceFileUri);
//
//        if (!sourceFile.isFile()) {
//            dialog.dismiss();
//            Log.e("uploadFile", "Source File not exist :" +imagePath);
//            return 0;
//        }
//        else
//        {
//            try {
//
//                // open a URL connection to the Servlet
//                FileInputStream fileInputStream = new FileInputStream(sourceFile);
//                URL url = new URL(upLoadServerUri);
//
//                // Open a HTTP  connection to  the URL
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setDoInput(true); // Allow Inputs
//                conn.setDoOutput(true); // Allow Outputs
//                conn.setUseCaches(false); // Don't use a Cached Copy
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Connection", "Keep-Alive");
//                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//                conn.setRequestProperty("image_name", fileName);
//
//                dos = new DataOutputStream(conn.getOutputStream());
//
//                dos.writeBytes(twoHyphens + boundary + lineEnd);
//                dos.writeBytes("Content-Disposition: form-data; name=\"image_name\";filename="+ fileName + "" + lineEnd);
//                dos.writeBytes(lineEnd);
//
//                // create a buffer of  maximum size
//                bytesAvailable = fileInputStream.available();
//
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                buffer = new byte[bufferSize];
//
//                // read file and write it into form...
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                while (bytesRead > 0) {
//
//                    dos.write(buffer, 0, bufferSize);
//                    bytesAvailable = fileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                }
//
//                // send multipart form data necesssary after file data...
//                dos.writeBytes(lineEnd);
//                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//                // Responses from the server (code and message)
//                serverResponseCode = conn.getResponseCode();
//                String serverResponseMessage = conn.getResponseMessage();
//
//                Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);
//
//                if(serverResponseCode == 200){
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        public void run() {
//
//                            Toast.makeText(getActivity(), "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//
//                //close the streams //
//                fileInputStream.close();
//                dos.flush();
//                dos.close();
//
//            } catch (MalformedURLException ex) {
//
//                dialog.dismiss();
//                ex.printStackTrace();
//
//                getActivity().runOnUiThread(new Runnable() {
//                    public void run() {
//
//                        Toast.makeText(getActivity(), "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
//            } catch (Exception e) {
//
//                dialog.dismiss();
//                e.printStackTrace();
//
//                getActivity().runOnUiThread(new Runnable() {
//                    public void run() {
//
//                        Toast.makeText(getActivity(), "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//                Log.d("Upload file to server Exception", "Exception : "
//                        + e.getMessage(), e);
//            }
//            dialog.dismiss();
//            return serverResponseCode;
//
//        } // End else block
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        filePath = data.getData();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }


                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(getActivity(), selectedFileUri);
                Log.i(TAG, "Selected File Path:" + selectedFilePath);

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
//                    tvFileName.setText(selectedFilePath);
                } else {
                    Toast.makeText(getActivity(), "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }
            }
        }

//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK ) {
//            filePath = data.getData();
//            try {
//                //Getting the Bitmap from Gallery
//                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
////                //Setting the Bitmap to ImageView
//                imageView.setImageBitmap(bitmap);
//
//
//                FileInputStream in = new FileInputStream(destination);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 10;
//                imagePath = destination.getAbsolutePath();
//                //tvPath.setText(imagePath);
//                Log.d("INFO", "PATH === " +imagePath);
//                Bitmap bmp = BitmapFactory.decodeStream(in, null, options);
//                imageView.setImageBitmap(bmp);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }

    private void uploadImage() {
        //Showing the progress dialog
//        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);

        String name = etVendorImageName.getText().toString().trim();
        Log.d(TAG, "image name: " + name);

        String image = getStringImage(bitmap);
        Log.d(TAG, "base64Image: " + image);


        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("mode", "vendorImageUploadSubmit");
            jsonObject.put("vendorid", VendorHomeAcivity.strVendorId);
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
                etVendorImageName.getText().clear();
                imageView.setImageBitmap(null);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class getImageCategory extends AsyncTask<String, String, String> {
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
                strImageCategoryResponse = buffer.toString();
                Log.i(TAG, strImageCategoryResponse);
                //send to post execute
                return strImageCategoryResponse;
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

            return strImageCategoryResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                JSONObject Object = new JSONObject(strImageCategoryResponse);
                String strStutus = Object.getString("status");
                Log.d(TAG, "status: " + strStutus);
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);


                JSONArray jsonArray = Object.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String strId = jsonObject.getString("id");
                    Log.d(TAG, "image id: " + strId);
                    String strCategoryName = jsonObject.getString("productname");
                    Log.d(TAG, "product name: " + strCategoryName);

                    listImageCategoryId.add(strId);
                    listImageCategoryName.add(strCategoryName);

                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listImageCategoryName);
                spnImageCategory.setAdapter(arrayAdapter);
                spnImageCategory.setPrompt("Choose a category");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
