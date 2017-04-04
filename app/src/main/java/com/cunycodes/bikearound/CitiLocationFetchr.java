package com.cunycodes.bikearound;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francely on 4/2/17.
 */

public class CitiLocationFetchr {

    public byte[] getURLBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getURLBytes(urlSpec));
    }

    public List<CitiBikeLocations> fetchItems() {

        List<CitiBikeLocations> location = new ArrayList<>();

        try {
            String url = Uri.parse("https://gbfs.citibikenyc.com/gbfs/en/station_information.json").buildUpon().build().toString();
            String jsonString = getUrlString(url);
            Log.i("TEST_TEST_FETCH_ITEMS", "Recieved JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(location, jsonBody);


        } catch (JSONException je) {
            Log.e("TEST_TEST_FETCH_ITEMS", "Failed to parse JSON", je);

        } catch (IOException ioe) {
            Log.e("TEST_TEST_FETCH_ITEMS", "Failed to fetsh JSON: " + ioe);
        }

        return location;
    }

    private void parseItems(List<CitiBikeLocations> items, JSONObject jsonBody) throws IOException, JSONException {
        JSONObject dataObject = jsonBody.getJSONObject("data");
        JSONArray stationsArray = dataObject.getJSONArray("stations");


        for(int i = 0; i < stationsArray.length(); i++) {
            JSONObject stationLocation = stationsArray.getJSONObject(i);
            CitiBikeLocations location = new CitiBikeLocations();

            location.setLat(stationLocation.getDouble("lat"));
            location.setLon(stationLocation.getDouble("lon"));
            location.setName(stationLocation.getString("name"));

            items.add(location);
        }
    }
}
