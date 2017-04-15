package com.cunycodes.bikearound;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class FoursquarePath extends AppCompatActivity {

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

    private ArrayList<String> venueID = new ArrayList<>();
    private ArrayList<String> imgURLS = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> addresses = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_explore);

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

    public void initializeList() {
        bikePaths.clear();

        for (int i = 0; i < imgURLS.size(); i++) {
            BikePath path = new BikePath();
            path.setCardName(names.get(i));
            path.setImageURL(imgURLS.get(i));
            path.setAddress(addresses.get(i));

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