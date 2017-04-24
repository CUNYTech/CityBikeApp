package com.cunycodes.bikearound;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class FoursquarePath extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String  TAG = "FoursquarePath";

    private final String CLIENT_ID = "BYBLRWV500ZLF0YZWITNZKG44E4CDRSJV4GCCB0ZS3LJGMPP";
    private final String CLIENT_SECRET ="UQI4ENKEX31XI0G2KCKBAMIKTIOSVET3QQXJUISQV3EOMSMD";
    private final String API_IMG_URL = "https://api.foursquare.com/v2/venues/";
    private final String API_URL = "https://api.foursquare.com/v2/venues/412d2800f964a520df0c1fe3/similar?";
    private final String V = "v=20161016";
    private final String QUERY = "bike+path";

    private final String latitude = "40.7463956";
    private final String longtitude = "-73.9852992";

    private RecyclerView cardList;
    private RecyclerView mRecyclerView;
    private ArrayList<BikePath> bikePaths = new ArrayList<>();
    private BikePathAdapterII adapter;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private UserDBHelper helper;
    private SQLiteDatabase database;
    private String userMembership;
    private TextView nav_name;                                                                        // added by Jody --do not delete, comment out if you need to operate without user
    private TextView nav_membership;
    private Uri uri;
    private String stringUri;
    private ImageView mUsers_photo;

    private ArrayList<String> venueID = new ArrayList<>();
    private ArrayList<String> imgURLS = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> addresses = new ArrayList<>();
    private ArrayList<Double> lats = new ArrayList<>();
    private ArrayList<Double> lons = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_explore);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mUsers_photo = (ImageView) header.findViewById(R.id.users_photo);
        nav_name = (TextView) header.findViewById(R.id.user_name);
        nav_membership = (TextView) header.findViewById(R.id.user_membership);
        nav_name.setText(user.getDisplayName());
        setUP();

        new foursquare().execute();
        //new foursquareImage().execute();

      //  initializeList();

        cardList = (RecyclerView) findViewById(R.id.card_view);
        cardList.setHasFixedSize(true);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(FoursquarePath.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(mLinearLayoutManager);

        adapter = new BikePathAdapterII(FoursquarePath.this, bikePaths);
        cardList.setAdapter(adapter);


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_explore){
            Intent intent = new Intent(this, RecommendedFragmentExecutor.class);
            startActivity(intent);
            finish();
        }  else if (id == R.id.nav_settings){
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_plan){
            Intent intent = new Intent(this, PlanActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_about){
            Intent intent = new Intent(this, AboutUs.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
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

        Cursor cursor1 = helper.getPhotoURI(userName, database);
        if (cursor1.moveToFirst()){
            stringUri = cursor1.getString(0);
            uri = Uri.parse(stringUri);
            if (uri!= null){
                displayPhoto();
            } else {
                mUsers_photo.setImageResource(R.mipmap.placeholder_woman);
            }

        }
        helper.close();

    }

    public void displayPhoto(){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            Matrix matrix = new Matrix();
            int rotate = getOrientation(uri);
            if (rotate == 0 && (bitmap.getWidth()> bitmap.getHeight())){
                matrix.postRotate(90);
            } else {
                matrix.postRotate(rotate);
            }
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(),bitmap.getHeight(),matrix, true );
            mUsers_photo.setImageBitmap(newBitmap);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed To Load Photo", Toast.LENGTH_SHORT).show();
        }

    }

    public int getOrientation(Uri uri){
        ExifInterface exifInterface = null;
        int rotate = 0;
        try {
            exifInterface = new ExifInterface(uri.getPath());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                rotate = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                rotate = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                rotate = 270;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotate;
    }

    public void initializeList() {
        bikePaths.clear();

        for (int i = 0; i < imgURLS.size(); i++) {
            BikePath path = new BikePath();
            path.setCardName(names.get(i));
            path.setImageURL(imgURLS.get(i));
            path.setAddress(addresses.get(i));
            path.setLat(String.valueOf(lats.get(i)));
            path.setLon(String.valueOf(lons.get(i)));

            bikePaths.add(path);
        }
    }


    private class foursquare extends AsyncTask<View, Void, String> {

        String urlResponse;
        ProgressDialog dialog;

        @Override
        protected String doInBackground(View... views) {
            urlResponse = makeCall(API_URL + V + "&ll="+latitude+ "%2C%20"+longtitude + "&query=" + QUERY + "&radius=1800&venuePhotos=1&limit=10&intent=checkin&client_id=" + CLIENT_ID +"&client_secret="+CLIENT_SECRET);
            return "";
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(FoursquarePath.this);
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String result) {

          if (urlResponse == null){
              Log.d(TAG, "There was an error with the API Call");

          } else {
              parseJSON(urlResponse);
          }

            if(dialog.isShowing())
                dialog.dismiss();

            new foursquareImage().execute();
        }
    }

    private class foursquareImage extends AsyncTask<String, Void, String>{

        String imgResponse = "";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(FoursquarePath.this);
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < venueID.size(); i++) {
              imgResponse = makeCall(API_IMG_URL + venueID.get(i) + "/photos?" + V + "&limit=1&intent=checkin&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET);
              String url = parseImgURL(imgResponse);
               Log.d(TAG, "InBackGroundNow");
            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s) {
            if (imgResponse == null) {
                Log.d(TAG, "There was an error with the image API Call");
            } else {
                initializeList();
                adapter = new BikePathAdapterII(FoursquarePath.this, bikePaths);
                cardList.setAdapter(adapter);
                Log.d(TAG, imgResponse);
            }

            if(dialog.isShowing())
                dialog.dismiss();
        }
    }


    public String makeCall(String urlString){

        StringBuffer response = new StringBuffer("");

        try {
            URL url = new URL(urlString);
            Log.d(TAG, "Opening URL" + url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

             urlConnection.setRequestMethod("GET");
             urlConnection.setDoInput(true);
             urlConnection.setInstanceFollowRedirects(false);
             urlConnection.connect();

            InputStream  inputStream = urlConnection.getInputStream();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = buffer.readLine()) != null) {
                response.append(line);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return String.valueOf(response);
    }

    public void parseJSON(String response){

        ArrayList<BikePath> tempList = new ArrayList<>();
        String imgURL = "";

        try {
            JSONObject json = new JSONObject(response);
            Log.d(TAG, response);
            JSONObject jsonObject = json.getJSONObject("response");
            JSONObject similarVenues = jsonObject.getJSONObject("similarVenues");
            JSONArray items = similarVenues.getJSONArray("items");

                for (int i = 0; i< items.length(); i++) {
                    JSONObject item = (JSONObject) items.get(i);
                    if (item.equals(null)){
                        continue;
                    }
                    JSONObject address = item.getJSONObject("location");

                    String id = (String) item.get("id");
                    venueID.add(id);

                    String name = item.getString("name");
                    names.add(name);
                    String address1 = address.getString("formattedAddress");
                    addresses.add(address1);

                    Double lat = address.getDouble("lat");
                    lats.add(lat);

                    Double lon = address.getDouble("lng");
                    lons.add(lon);

                }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public String parseImgURL(String response){

        String url="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d(TAG, response);
            JSONObject json = jsonObject.getJSONObject("response");
            JSONObject jsonObject1 = json.getJSONObject("photos");


                JSONArray items = jsonObject1.getJSONArray("items");

                if (items.length() != 0) {

                    JSONObject item = (JSONObject) items.get(0);
                    String prefix = (String) item.get("prefix");
                    String suffix = (String) item.get("suffix");

                    url = prefix + "400x300" + suffix;
                    imgURLS.add(url);

                }

        } catch (Exception e){
            e.printStackTrace();
            return url=null;
        }

        return url;
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = mRecyclerView.indexOfChild(v);
            Log.d("Brooklyn Path Activity", String.valueOf(itemPosition));
        }
    }
}
