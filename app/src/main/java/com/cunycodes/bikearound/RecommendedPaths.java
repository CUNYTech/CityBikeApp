package com.cunycodes.bikearound;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TextView;

public class RecommendedPaths extends TabActivity {

    private static final String BROOKLYN_SPEC = "BROOKLYN";
    private static final String BRONX_SPEC = "BRONX";
    private static final String QUEENS_SPEC = "QUEENS";
    private static final String MANHATAN_SPEC = "NEW YORK";
    private TextView nav_name;
    private TextView nav_membership;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_paths);
        setTitle("Recommended Paths");

        TabHost tabHost = getTabHost();

        TabHost.TabSpec brooklynSpec = tabHost.newTabSpec(BROOKLYN_SPEC);
        brooklynSpec.setIndicator(BROOKLYN_SPEC);
        Intent brooklynIntent = new Intent(this, BrooklynPaths.class);
        brooklynSpec.setContent(brooklynIntent);

        TabHost.TabSpec bronxSpec = tabHost.newTabSpec(BRONX_SPEC);
        bronxSpec.setIndicator(BRONX_SPEC);
        Intent bronxIntent = new Intent(this, BronxPaths.class);
        bronxSpec.setContent(bronxIntent);

        TabHost.TabSpec manhattanSpec = tabHost.newTabSpec(MANHATAN_SPEC);
        manhattanSpec.setIndicator(MANHATAN_SPEC);
        Intent manhattanIntent = new Intent(this, NewYorkPaths.class);
        manhattanSpec.setContent(manhattanIntent);

        TabHost.TabSpec queensSpec = tabHost.newTabSpec(QUEENS_SPEC);
        queensSpec.setIndicator(QUEENS_SPEC);
        Intent queensIntent = new Intent(this, QueensPath.class);
        queensSpec.setContent(queensIntent);

        tabHost.addTab(manhattanSpec);
        tabHost.addTab(bronxSpec);
        tabHost.addTab(brooklynSpec);
        tabHost.addTab(queensSpec);

    }


}
