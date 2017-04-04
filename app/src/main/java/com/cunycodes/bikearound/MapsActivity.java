package com.cunycodes.bikearound;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

//import com.google.android.gms.identity.intents.Address;

public class MapsActivity extends AppCompatActivity //FragmentActivity - changed by Jody
                          implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener,
                          NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MapsActivity";
    final String CITI_API_URL = "https://gbfs.citibikenyc.com/gbfs/en/station_information.json";
    final String STATION_STATUS_URL = "https://gbfs.citibikenyc.com/gbfs/en/station_status.json";
    final String GOOGLE_PLACES_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";    //Added by Mike, requires additional search criteria after url
    final String GOOGLE_PLACES_KEY = "AIzaSyDVaebTQTxdveWdMCzwAC2yj55aep6-roU";                         //added by Mike. API key to google places API
    private FirebaseUser user;                                                                         // added by Jody --do not delete, comment out if you need to operate without user
    private FirebaseAuth mAuth;                                                                        // added by Jody --do not delete, comment out if you need to operate without user
    private TextView nav_name;                                                                        // added by Jody --do not delete, comment out if you need to operate without user
    private TextView nav_membership;                                                                  // added by Jody --do not delete, comment out if you need to operate without user
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSION_LOCATION = 111;
    private LatLng currrentLatLng;
    private Location currentLocation;
    double currentLatitudeTEST = 40.716974;
    double currentLongitudeTEST = -74.012360;
    double currentLatitude;
    double currentLongitude;
    private String text;
    private EditText textAddress;
    private Button btnSearch;
    private UserDBHelper helper;
    private SQLiteDatabase database;
    private String userMembership;

    StationInformation stationInformation = new StationInformation(); //Create a new class to hold Station information.
    Timer newTimer = new Timer();




    ///////////////////////////////////////////////////////////////////////////////////////////////


//    private LocationManager locationManager = (LocationManager)
//            getSystemService(Context.LOCATION_SERVICE);
//    Criteria criteria = new Criteria();
//    private Location location = locationManager.getLastKnownLocation(locationManager
//            .getBestProvider(criteria, false));


    ///////////////////////////////////////////////////////////////////////////////////////////////


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps); //changed from activity_maps

        //added the code below - Jody
        mAuth = FirebaseAuth.getInstance();  //getInstance    added by Jody --do not delete
        user = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // edit the Navigation Bar
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        nav_name = (TextView) header.findViewById(R.id.user_name);
        nav_membership = (TextView) header.findViewById(R.id.user_membership);
        nav_name.setText(user.getDisplayName());
        setUP();
      //  nav_email.setText(user.getEmail());

        textAddress = (EditText) findViewById(R.id.textAddress);
        btnSearch = (Button) findViewById(R.id.searchBtn);

        //added the code above -Jody

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

        text = getIntent().getStringExtra("address");
        if (text != null){
            textAddress.setText(text, TextView.BufferType.EDITABLE);
            Log.d(TAG, text);
           // btnSearch.performClick();
        }

        //newTimer.start();

    }

    public void setUP(){
        String userName = user.getDisplayName();
        helper = new UserDBHelper(getApplicationContext());
        database = helper.getReadableDatabase();
        Cursor cursor = helper.getMembership(userName, database);
        if (cursor.moveToFirst()){
            userMembership = cursor.getString(0);
            nav_membership.setText(userMembership);

        }
    }


    private void handleNewLocation(Location location) {
        //Log.d("TEST TEST TEST TEST", location.toString());

        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        currrentLatLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(currrentLatLng)
                .title("I am here!");
        mMap.addMarker(options);
        float zoomLevel = 14.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currrentLatLng, zoomLevel));


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

        //downloadCitiLocationsData();

        mMap.setMyLocationEnabled(true);


        //float zoomLVL = 16.0f;
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, zoomLVL));

        //Log.v("CURRENT_LOCATON", "ERR: " + curLocation);
        //mMap.getMyLocation();
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
            Log.d("SEARCH", String.valueOf(latLng));
            int destID = stationInformation.getNearestLocationID(latLng);
            Log.d("SEARCH", String.valueOf(destID));
            LatLng destination = stationInformation.getLatLng(destID);
            int bikeQty = stationInformation.getBikeQuantity(destID);
            mMap.addMarker(new MarkerOptions().position(destination).title(stationInformation.getName(destID)).snippet(String.valueOf(bikeQty) + " bikes available")); //This should display the number of bikes. I need to resolve this bug --Mike
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));



            directions = "https://maps.googleapis.com/maps/api/directions/json?origin=" + currentLatitude + "," + currentLongitude + "&destination=" + latLng.latitude + "," + latLng.longitude + "&mode=bicycling&key=AIzaSyBuwP1BalG9FdpoU0F5LCmHvkJOlULK6to";

            new FetchDirections().execute();

            currentLatitude = latLng.latitude;
            currentLongitude = latLng.longitude;

            new FetchLocations().execute();

        }
        Log.d("TIMER", String.valueOf(newTimer.getTimeRemaining()));
    }

    public void downloadCitiLocationsData() {

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, CITI_API_URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject data = response.getJSONObject("data");
                    JSONArray list = data.getJSONArray("stations");

                    Location nearestLocation = new Location("Nearest Location");

                    JSONObject defaultObj = list.getJSONObject(0);
                    double defaultLat = defaultObj.getDouble("lat");
                    double defaultLon = defaultObj.getDouble("lon");

                    float[] nearDist = new float[1];
                    Location.distanceBetween(currentLatitude,currentLongitude,defaultLat,defaultLon,nearDist);

                    stationInformation.setStationLocationList(list);  //Add a JSONArray to class StationInformation for easy retrieval

                    for(int i = 0; i < list.length(); i++) {
                        JSONObject obj = list.getJSONObject(i);
                        double lat = obj.getDouble("lat");
                        double lon = obj.getDouble("lon");
                        String name = obj.getString("name");
                        int ID = obj.getInt("station_id");
                        int bikeQty = stationInformation.getBikeQuantity(ID);
//                        System.out.println(lat);
//                        System.out.println(lon);

                        Location newLocation = new Location("New Location");
                        newLocation.setLatitude(lat);
                        newLocation.setLongitude(lon);

                        float[] dist = new float[1];



                        Location.distanceBetween(currentLatitude,currentLongitude,lat,lon,dist);

                        if(dist[0] < nearDist[0]) {
                            nearDist[0] = dist[0];
                            nearestLocation.setLatitude(lat);
                            nearestLocation.setLongitude(lon);
                        }



                            if(i == (list.length() - 1)) {
                                LatLng latLngNear = new LatLng(nearestLocation.getLatitude(), nearestLocation.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(latLngNear).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(name).snippet(bikeQty + " NEAR bikes available."));

                            }

                        if(dist[0] < 300) {
                                LatLng latLng = new LatLng(lat, lon);
                                mMap.addMarker(new MarkerOptions().position(latLng).title(name).snippet(bikeQty + " bikes available."));
                            }





                        System.out.println(dist[0]);
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


//Class by Mike. Timer, displays nearest Location when Timer is below 5 min.
    public class Timer {
        double startTime = System.currentTimeMillis() ;
        double currentTime;

        public void start(){
            startTime = System.currentTimeMillis();
        }

        public String getTimeRemaining(){
            currentTime= System.currentTimeMillis();
            double secondsElapsed = currentTime - startTime;
            secondsElapsed = secondsElapsed / 1000;
            return (String.valueOf((int) (secondsElapsed / 60)) + " : " + String.valueOf((int)secondsElapsed % 60));
            //return (String.valueOf((int)secondsElapsed));

        }

        public int getTimeRemainingSecs(){
            currentTime= System.currentTimeMillis();
            double secondsElapsed = currentTime - startTime;
            secondsElapsed = secondsElapsed / 1000;
            return (int) secondsElapsed;
        }

        public void checkTimer(Location location){
            double myLatitude = location.getLatitude();
            double myLongitude = location.getLongitude();
            LatLng myLatLng = new LatLng(myLatitude, myLongitude);

            if (this.getTimeRemainingSecs() < 300 ){
                int nearestId = stationInformation.getNearestLocationID(myLatLng);
                stationInformation.getLatLng(nearestId);
                mMap.addMarker(new MarkerOptions().position(myLatLng).title("NearestLocation"));
            }
    }
    }





    String directions = "https://maps.googleapis.com/maps/api/directions/json?origin=" + currentLatitude + "," + currentLongitude + "&destination=41.418976,%20-81.399025&mode=bicycling&key=AIzaSyBuwP1BalG9FdpoU0F5LCmHvkJOlULK6to";

    public void downloadDestinationRoute() {

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, directions, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    //JSONObject data = response.getJSONObject("data");
                    JSONArray routes = response.getJSONArray("routes");
                    JSONObject routeInfo = routes.getJSONObject(0);

                    //JSONArray legs = routeInfo.getJSONArray("legs");
                    //JSONObject legsInfo = legs.getJSONObject(0);

                    JSONObject overview_polyline = routeInfo.getJSONObject("overview_polyline");
                    String points = overview_polyline.getString("points");

                    List<LatLng> decodePath = PolyUtil.decode(points);



                    mMap.addPolyline(new PolylineOptions().addAll(decodePath).width(3).color(0x7FFF0000));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude,currentLongitude), 12));
                    Log.v("TEST_TEST_TEST_TEST____", "ERR: " + points );


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


























//Below Method by Mike
    //Downloads the status information from CitiBikes Status JSON
    public void downloadCitiStatusData() {

        final JsonObjectRequest jsonRequestStatus = new JsonObjectRequest(Request.Method.GET, STATION_STATUS_URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject data = response.getJSONObject("data");
                    JSONArray list = data.getJSONArray("stations");
                    int lastUpdate = response.getInt("last_updated");

                    stationInformation.setStationUpdate(lastUpdate); //Copies the last update info from JSon into an array in StationInformation Class
                    stationInformation.setStationStatusList(list);  //Copies the Stations' info objects from JSon into an array of objects in StationInformation Class

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
//// Below Method by Mike
    //Takes in the last update time as long epoch, returns a string that says when it was updated.
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

//Below Class by Mike
    //Holds all the station information of citibikes
    //Information is retrievable through methods.
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

    //Below Method by Mike. Retrieves and displays Nearest POI as markers.
    public void onPOIClick(View view) {
        try {
            //LatLng location = currrentLatLng;   //Use this line for ACTUAL location
            LatLng location = new LatLng(currentLatitude, currentLongitude);
            Log.d("PLACES", "ABOUT TO DISPLAY LATLNG");
            Log.d("PLACES", String.valueOf(location));
            String PARKS = "park";
            String CAFE = "cafe";
            Log.d("PLACES", "About to getPOI");
            getPOI(location, PARKS);
            getPOI(location, CAFE);
        }

        catch (Exception e){
            Log.e("POI", String.valueOf(e));
        }
    }

////Below Method by Mike. This method might be better as private in onPoiClick(). Throws error with Volley when in a class.
    //Method displays local cafes and parks as markers.
    public void getPOI(LatLng latLng, final String placeType) {

        double lat = latLng.latitude;
        double lng = latLng.longitude;
        String URL = GOOGLE_PLACES_URL + "?location=" + String.valueOf(lat) + "," + String.valueOf(lng) + "&radius=500&type=" + placeType + "&key=" + GOOGLE_PLACES_KEY;
        Log.d("PLACES", URL);
        final JsonObjectRequest jsonRequestStatus = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++){
                        JSONObject park = results.getJSONObject(i);
                        String name = park.getString("name");
                        JSONObject geometry = park.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        double lat = location.getDouble("lat");
                        double lng = location.getDouble("lng");


                        Log.d("PLACES", "Lat: " + location.getString("lat"));
                        Log.d("PLACES", "Name: " + name);

                        LatLng position = new LatLng(lat, lng);
                        Log.d("PLACES", String.valueOf(position));
                        int color = 0;
                        if (placeType == "park"){color = 90;}
                        if (placeType == "cafe"){color = 150;}

                        mMap.addMarker(new MarkerOptions().position(position).title(name).icon(BitmapDescriptorFactory.defaultMarker(color)).snippet(placeType)); //.


                    }
                } catch (JSONException e) {
                    Log.v("TEST_API_RESPONSE", "ERR: ");
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
        mMap.clear();
        handleNewLocation(location);
        newTimer.checkTimer(location);
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

    //Method by Jody
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_history){
            Intent intent = new Intent(this, ProfilePageActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, ProfilePageActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_explore) {
            Intent intent = new Intent(this, ExploreActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_recommend){
            Intent intent = new Intent(this, RecommendedPaths.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
 // Method Above by Jody

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class FetchLocations extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            downloadCitiStatusData();
            downloadCitiLocationsData();
            return null;
        }
    }


    private class FetchDirections extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            downloadDestinationRoute();
            return null;
        }
    }


}
