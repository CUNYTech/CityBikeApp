package com.cunycodes.bikearound;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;


public class FoursquarePath extends AppCompatActivity {

    private final String CLIENT_ID = "BYBLRWV500ZLF0YZWITNZKG44E4CDRSJV4GCCB0ZS3LJGMPP";
    private final String CLIENT_SECRET ="UQI4ENKEX31XI0G2KCKBAMIKTIOSVET3QQXJUISQV3EOMSMD";
    private final String API_URL = "https://api.foursquare.com/v2/venues/explore?";
    private final String V = "v=20161016";
    private final String QUERY = "bike_path";

    private final String latitude = "40.7463956";
    private final String longtitude = "-73.9852992";

    private RecyclerView mRecyclerView;
    private ArrayList<PopularPaths> bikePaths = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private class foursquare extends AsyncTask<View, Void, String> {

        String url;

        @Override
        protected String doInBackground(View... views) {
            url = API_URL + V + "&ll="+latitude+ "%2C%20"+longtitude + "&query=" + QUERY + "&intent=checkin&client_id=" + CLIENT_ID +"&client_secret"+CLIENT_SECRET;

            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

}
