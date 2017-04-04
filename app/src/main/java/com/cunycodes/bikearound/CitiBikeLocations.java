package com.cunycodes.bikearound;

/**
 * Created by Francely on 4/2/17.
 */

public class CitiBikeLocations {
    private double mLat;
    private double mLon;
    private String mName;

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
