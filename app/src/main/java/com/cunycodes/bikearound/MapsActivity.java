package com.cunycodes.bikearound;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.location.Address;
import android.widget.Toast;

//import com.google.android.gms.identity.intents.Address;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Permission;
import java.util.List;

import static android.R.attr.data;

public class  MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    final String CITI_API_URL = "https://gbfs.citibikenyc.com/gbfs/en/station_information.json";
    final String STATION_STATUS_URL = "https://gbfs.citibikenyc.com/gbfs/en/station_status.json";


    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSION_LOCATION = 111;

    StationInformation stationInformation = new StationInformation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        new FetchLocations().execute();

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
        LatLng latLng = new LatLng( 40.718981, -74.011736);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }



        mMap.setMyLocationEnabled(true);

    }

    public void onSearch(View view) throws JSONException {
        EditText address = (EditText) findViewById(R.id.textAddress);
        String location = address.getText().toString();
        List<Address> addressList = null;

        if(location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address1 = addressList.get(0);
            LatLng latLng = new LatLng(address1.getLatitude(), address1.getLongitude());
            int destID = stationInformation.getNearestLocationID(latLng);
            LatLng destination = stationInformation.getLatLng(destID);
            int bikeQty = stationInformation.getBikeQuantity(destID);
            mMap.addMarker(new MarkerOptions().position(destination).title(stationInformation.getName(destID)).snippet(String.valueOf(bikeQty) + " bikes available"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            Log.d("NEARESTSTATION", String.valueOf(stationInformation.getName(destID)));
        }
    }
    public void downloadCitiLocationsData() {
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, CITI_API_URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject data = response.getJSONObject("data");
                    JSONArray list = data.getJSONArray("stations");
                    int lastUpdate = response.getInt("last_updated");
                    long currentTimeSecs =  System.currentTimeMillis() / 1000;

                    int timeElapsedUpdate = (int) (currentTimeSecs - lastUpdate);

                    stationInformation.setStationLocationList(list);  //Add a JSONArray to class StationInformation for easy retrieval
                    Log.d("BIKE72", String.valueOf(stationInformation.getBikeQuantity(522)));

                    for(int i = 0; i < list.length(); i++) {
                        JSONObject obj = list.getJSONObject(i);
                        double lat = obj.getDouble("lat");
                        double lon = obj.getDouble("lon");
                        String name = obj.getString("name");
                        int stationID = obj.getInt("station_id");
                        Log.d("STATION_ID", String.valueOf(stationID));
                        LatLng latLng = new LatLng(lat, lon);
                        mMap.addMarker(new MarkerOptions().position(latLng).title(name).snippet(getTimeSinceUpdateString(timeElapsedUpdate)));

                    }


                } catch (JSONException e) {
                    Log.v("TEST_API_RESPONSE", "ERR: " );
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("TEST_API_RESPONSE", "ERR: " + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    /// fetches station status, returns information to class StationInformation
    public void downloadCitiStatusData() {

        final JsonObjectRequest jsonRequestStatus = new JsonObjectRequest(Request.Method.GET, STATION_STATUS_URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject data = response.getJSONObject("data");
                    JSONArray list = data.getJSONArray("stations");
                    int lastUpdate = response.getInt("last_updated");

                    stationInformation.setStationUpdate(lastUpdate);
                    stationInformation.setStationStatusList(list);

                } catch (JSONException e) {
                    Log.v("TEST_API_RESPONSE", "ERR: " );
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("TEST_API_RESPONSE", "ERR: " + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(this).add(jsonRequestStatus);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("LOCATION", String.valueOf(location));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);

        } else {
            startLocationServices();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void startLocationServices() {
        try {
            LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, req, this);

        } catch (SecurityException exception) {

        }
    }

    public  String getTimeSinceUpdateString(int timeElapsedUpdate){
        String updateTimeString = "";
        if (timeElapsedUpdate == 0)
        {
            updateTimeString = "now.";

        }
        else if (timeElapsedUpdate < 60)
        {
            updateTimeString = " < 1 min ago.";
        }
        else
        {
            updateTimeString = String.valueOf((int) timeElapsedUpdate / 60) + "minutes ago.";
        }

        return updateTimeString;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationServices();

                } else {
                    // Show a dialog saying something like, "I can't run your location - you denied permission!"
                    Toast.makeText(this, "I can't run your location - you denied permission!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public class StationInformation{
        private JSONArray stationStatusList;
        private JSONArray stationLocationList;
        private long updateTime;

        public void setStationStatusList(JSONArray arr){
            stationStatusList = arr;
            Log.d("SETSTATUSLIST", "STATUSLIST ARRAY SET");

        }

        public void setStationUpdate(long time){
            updateTime = time;
        }

        public void setStationLocationList(JSONArray arr){
            stationLocationList = arr;
            Log.d("SETLOCATIONLIST", "LOCATIONLIST ARRAY SET");
        }

        public int getBikeQuantity(int stationID) throws JSONException {
            try {
                for (int i = 0; i < stationStatusList.length(); i++) {
                    JSONObject obj = stationStatusList.getJSONObject(i);
                    int currentID = obj.getInt("station_id");
                    int bikeQuantity = obj.getInt("num_bikes_available");

                    if (currentID == stationID){
                        return bikeQuantity;
                    }
                }

            }
            catch (JSONException e){
                Log.e("JSONEXCEPTION", "ERROR FINDING QUANTITY OF BIKES");
            }
            Log.e("GETBIKEQUANTITY", "Couldnt find information from JSON, problem with ID?");
            return -1;
        }

        public int getNearestLocationID(LatLng latLng){
            try {
                int nearestID = -1;
                double nearestDistance = 9999;
                for (int i = 0; i < stationLocationList.length(); i++) {
                    JSONObject obj = stationLocationList.getJSONObject(i);
                    double stationLat = obj.getDouble("lat");
                    double stationLng = obj.getDouble("lon");
                    int ID = obj.getInt("station_id");
                    double myLat = latLng.latitude;
                    double myLng = latLng.longitude;
                    double distance = Math.sqrt((Math.pow((stationLat - myLat), 2)) + (Math.pow((stationLng - myLng), 2)));
                    Log.d("DISTANCE", String.valueOf(distance));
                    if (distance < nearestDistance ){
                        nearestDistance = distance;
                        nearestID = ID;
                    }
                    Log.d("NEARESTDISTANCE", String.valueOf(nearestDistance));

                }

                return nearestID;
            }
            catch (JSONException e){
                Log.e("JSONEXCEPTION", "ERROR FINDING NEAREST LOCATION");
            }
            return -1;

        }

        public LatLng getLatLng(int stationID){
            double lat = -1;
            double lng = -1;

            try {
                for (int i = 0; i < stationLocationList.length(); i++) {
                    JSONObject obj = stationLocationList.getJSONObject(i);
                    int currentID = obj.getInt("station_id");

                    if (currentID == stationID){
                        lat = obj.getDouble("lat");
                        lng = obj.getDouble("lon");
                    }

                }
                LatLng coordinates = new LatLng(lat, lng);
                Log.d("LATLNG OF GIVEN ID", String.valueOf(coordinates));
                return coordinates;
            }
            catch (JSONException e){
                Log.e("JSONEXCEPTION", "ERROR FINDING LATLNG STATION FROM ID");
            }
            LatLng coordinates = new LatLng(-1,-1);
            return coordinates;

        }

        public String getName(int stationID){
            String name = "name";
            try {
                for (int i = 0; i < stationLocationList.length(); i++) {
                    JSONObject obj = stationLocationList.getJSONObject(i);
                    int currentID = obj.getInt("station_id");
                    if (currentID == stationID){
                        name = obj.getString("name");
                    }
                }
            }
            catch (JSONException e){
                Log.e("JSONEXCEPTION", "ERROR FINDING LATLNG STATION FROM ID");
            }
            return name;
        }



    }

    private class FetchLocations extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            downloadCitiStatusData();
            downloadCitiLocationsData();
            return null;
        }
    }


}


