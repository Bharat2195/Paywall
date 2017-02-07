package com.tornado.cphp.awhitepaid;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tornado.cphp.awhitepaid.googlemaputils.GetNearbyPlacesData;
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

public class AddAddressMapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private GoogleMap mMap;
    double latitude;
    double longitude;
    private int PROXIMITY_RADIUS = 10000;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    SharedPreferences sharedPreferences;
    int locationCount = 0;
    private String JsonResponse="";
    private RelativeLayout mLayoutSave;
    private Toolbar mToolbarAddress;
    private TextView txtAddress,txtTitle;
    private String strTitle,strDescription;
    private int i=0;
    private static final String TAG=AddAddressMapsActivity.class.getSimpleName();
    private ProgressDialog pd;
    ArrayList<Double> listLangitude=new ArrayList<>();
    ArrayList<Double> listLatitude=new ArrayList<>();
    ArrayList<String> listTitle=new ArrayList<>();
    ArrayList<String> listDesription=new ArrayList<>();

    private boolean isClicked=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mLayoutSave=(RelativeLayout)findViewById(R.id.mLayoutSave);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mToolbarAddress=(Toolbar)findViewById(R.id.mToolbarAddress);
        txtAddress=(TextView)mToolbarAddress.findViewById(R.id.txtAddress);
        txtTitle=(TextView)mToolbarAddress.findViewById(R.id.txtTitle);
        txtTitle.setText("Map");

        txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker();
            }
        });

        mLayoutSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i<listLangitude.size(); i++){
                    JSONObject jsonObject=new JSONObject();
                    try{
                        jsonObject.put("mode","vendorAddressUpdation");
                        jsonObject.put("vendorid",VendorHomeAcivity.strVendorId);
                        jsonObject.put("vendor_address",listTitle.get(i));
                        jsonObject.put("latitude",listLatitude.get(i));
                        jsonObject.put("longitude",listLangitude.get(i));
                        jsonObject.put("title",listTitle.get(i));
                        jsonObject.put("discription",listDesription.get(i));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if (jsonObject.length()>0){
                        new setAddress().execute(String.valueOf(jsonObject));

                    }
                }
            }
        });

        // Setting a click event handler for the map

    }


    private class setAddress extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(AddAddressMapsActivity.this);
            pd.setMessage("Please Wait....");
            pd.setIndeterminate(false);
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
            if (pd.isShowing()){
                pd.dismiss();
            }
            try {
                JSONObject Object = new JSONObject(JsonResponse);
                String strStutus = Object.getString("status");
                Log.d(TAG, "status: " + strStutus);
                String strMessage = Object.getString("message");
                Log.d(TAG, "message: " + strMessage);
                JSONArray jsonArray=Object.getJSONArray("response");
                String strReseponse=jsonArray.getString(0);
                Toast.makeText(AddAddressMapsActivity.this,strReseponse , Toast.LENGTH_SHORT).show();
                listTitle.clear();
                listLangitude.clear();
                listLatitude.clear();
                listDesription.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void addMarker() {
        if(mMap != null){

            //create custom LinearLayout programmatically
            LinearLayout layout = new LinearLayout(AddAddressMapsActivity.this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText titleField = new EditText(AddAddressMapsActivity.this);
            titleField.setHint("Title");

            final EditText descField = new EditText(AddAddressMapsActivity.this);
            descField.setHint("Description");
//            descField.setInputType(InputType.TYPE_CLASS_NUMBER
//                    | InputType.TYPE_NUMBER_FLAG_DECIMAL
//                    | InputType.TYPE_NUMBER_FLAG_SIGNED);

//            final EditText lonField = new EditText(MapsActivity.this);
//            lonField.setHint("Longitude");
//            lonField.setInputType(InputType.TYPE_CLASS_NUMBER
//                    | InputType.TYPE_NUMBER_FLAG_DECIMAL
//                    | InputType.TYPE_NUMBER_FLAG_SIGNED);

            layout.addView(titleField);
            layout.addView(descField);
//            layout.addView(lonField);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Marker");
            builder.setView(layout);
            AlertDialog alertDialog = builder.create();

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean parsable = true;
                    Double lat = null, lon = null;

                    strTitle = titleField.getText().toString();
                    strDescription= descField.getText().toString();


                    Toast.makeText(AddAddressMapsActivity.this, "Add marker on map", Toast.LENGTH_SHORT).show();
                    isClicked=true;
//                    try{
//                        lat = Double.parseDouble(strLat);
//                    }catch (NumberFormatException ex){
//                        parsable = false;
//                        Toast.makeText(MapsActivity.this,
//                                "Latitude does not contain a parsable double",
//                                Toast.LENGTH_LONG).show();
//                    }
//
//                    try{
//                        lon = Double.parseDouble(strLon);
//                    }catch (NumberFormatException ex){
//                        parsable = false;
//                        Toast.makeText(MapsActivity.this,
//                                "Longitude does not contain a parsable double",
//                                Toast.LENGTH_LONG).show();
//                    }
//
//                    if(parsable){
//                        LatLng targetLatLng = new LatLng(lat, lon);
//                        MarkerOptions markerOptions =
//                                new MarkerOptions().position(targetLatLng).title(strTitle);
//                        mMap.addMarker(markerOptions);
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(targetLatLng));
//                    }
                }
            });
            builder.setNegativeButton("Cancel", null);

            builder.show();
        }else{
            Toast.makeText(AddAddressMapsActivity.this, "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mToolbarAddress=(Toolbar)findViewById(R.id.mToolbarAddress);
        mToolbarAddress.inflateMenu(R.menu.map_main);
        mToolbarAddress.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
//        getMenuInflater().inflate(R.menu.map_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        switch (item.getItemId()){
            case R.id.menunormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case R.id.menustatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case R.id.menuterrian:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;

            case R.id.menurefresh:
                mMap.clear();
                break;

            case R.id.menuhybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Opening the sharedPreferences object
        sharedPreferences = getSharedPreferences("location", 0);

        // Getting number of locations already stored
        locationCount = sharedPreferences.getInt("locationCount", 0);

        // Getting stored zoom level if exists else return 0
        String zoom = sharedPreferences.getString("zoom", "0");

        // If locations are already saved
        if(locationCount!=0){

            String lat = "";
            String lng = "";

            // Iterating through all the locations stored
            for(int i=0;i<locationCount;i++){

                // Getting the latitude of the i-th location
                lat = sharedPreferences.getString("lat"+i,"0");

                // Getting the longitude of the i-th location
                lng = sharedPreferences.getString("lng"+i,"0");

                // Drawing marker on the map
                drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
            }

            // Moving CameraPosition to last clicked position
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));

            // Setting the zoom level in the map on last position  is clicked
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));
        }


//        if (!isClicked==false){
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latLng) {
                    locationCount++;

                    // Drawing marker on the map
                    drawMarker(latLng);

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Storing the latitude for the i-th location
                    editor.putString("lat"+ Integer.toString((locationCount-1)), Double.toString(latLng.latitude));

                    // Storing the longitude for the i-th location
                    editor.putString("lng"+ Integer.toString((locationCount-1)), Double.toString(latLng.longitude));

                    // Storing the count of locations or marker count
                    editor.putInt("locationCount", locationCount);

                    /** Storing the zoom level to the shared preferences */
                    editor.putString("zoom", Float.toString(mMap.getCameraPosition().zoom));

                    /** Saving the values stored in the shared preferences */
                    editor.commit();

                    Toast.makeText(getBaseContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();
                    isClicked=false;
                    strTitle="";
                    strDescription="";

                }
            });


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {

                // Removing the marker and circle from the Google Map
                mMap.clear();

                // Opening the editor object to delete data from sharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Clearing the editor
                editor.clear();

                // Committing the changes
                editor.commit();

                // Setting locationCount to zero
                locationCount=0;

            }
        });





        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        Button btnRestaurant = (Button) findViewById(R.id.btnRestaurant);
        btnRestaurant.setOnClickListener(new View.OnClickListener() {
            String Restaurant = "restaurant";
            @Override
            public void onClick(View v) {
                Log.d("onClick", "Button is Clicked");
                mMap.clear();
                String url = getUrl(latitude, longitude, Restaurant);
                Object[] DataTransfer = new Object[2];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                Toast.makeText(AddAddressMapsActivity.this,"Nearby Restaurants", Toast.LENGTH_LONG).show();
            }
        });

        Button btnHospital = (Button) findViewById(R.id.btnHospital);
        btnHospital.setOnClickListener(new View.OnClickListener() {
            String Hospital = "hospital";
            @Override
            public void onClick(View v) {
                Log.d("onClick", "Button is Clicked");
                mMap.clear();
                String url = getUrl(latitude, longitude, Hospital);
                Object[] DataTransfer = new Object[2];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                Toast.makeText(AddAddressMapsActivity.this,"Nearby Hospitals", Toast.LENGTH_LONG).show();
            }
        });

        Button btnSchool = (Button) findViewById(R.id.btnSchool);
        btnSchool.setOnClickListener(new View.OnClickListener() {
            String School = "school";
            @Override
            public void onClick(View v) {
                Log.d("onClick", "Button is Clicked");
                mMap.clear();
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                String url = getUrl(latitude, longitude, School);
                Object[] DataTransfer = new Object[2];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                Toast.makeText(AddAddressMapsActivity.this,"Nearby Schools", Toast.LENGTH_LONG).show();
            }
        });

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    private void drawMarker(LatLng point){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);
//        markerOptions.title(point.latitude + " : " + point.longitude);

        markerOptions.title(strTitle);
        markerOptions.snippet(strDescription);

        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
        listTitle.add(strTitle);
        listLangitude.add(point.longitude);
        listLatitude.add(point.latitude);
        listDesription.add(strDescription);
    }

    @Override
    public void onConnected( Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.
                zoomTo(11));
        Toast.makeText(AddAddressMapsActivity.this,"Your Current Location", Toast.LENGTH_LONG).show();

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f",latitude,longitude));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");

    }
    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
}
