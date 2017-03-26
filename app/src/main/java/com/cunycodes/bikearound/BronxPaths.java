package com.cunycodes.bikearound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class BronxPaths extends AppCompatActivity {

    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_card);

        //title = (TextView) findViewById(R.id.titleTextView);
    }
}

