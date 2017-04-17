package com.cunycodes.bikearound;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

public class AboutUs extends TabActivity {

    private static final String BROOKLYN_SPEC = "TEST";
    private static final String BRONX_SPEC = "TEST2";
    private static final String QUEENS_SPEC = "TEST3";
    private static final String MANHATAN_SPEC = "TEST4";
    private TextView title;
    private ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_paths);
        title = (TextView) findViewById(R.id.titleText);
        title.setText("About Us!");

        backButton = (ImageButton) findViewById(R.id.backImageButton);

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

    public void onClick(View v){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
