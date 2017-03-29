package com.cunycodes.bikearound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;


public class BronxPaths extends AppCompatActivity {

    private String[] name = {"Van Cortland Park", "Pelham Bay Park", "Thain Family Forest",
            "New York Botanical Gardens", "Highbridge Park", "Bronx River Park"};
    private int[] image = {R.mipmap.vancortlandpark, R.mipmap.pelhambaypark, R.mipmap.thainlyfamilyforest,
            R.mipmap.newyorkbotanical, R.mipmap.highbridgepark, R.mipmap.bronxriver};
    private String[] address = {"Broadway and Vancortlandt Park S, Bronx, NY 10462", "Pelham Bay Park, Bronx, NY 10465",
            "Bronx Park Rd, Bronx, NY 10458", "2900 Southern Blvd, Bronx, NY 10458",
            "2301 Amsterdam Ave, New York, NY 10033", "2 Bronx River Pkwy, Scarsdale, NY 10583" };

    private RecyclerView mRecyclerView;
    private ArrayList<PopularPaths> bikepath = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cardview);
        initializeList();

        RecyclerView cardList = (RecyclerView) findViewById(R.id.card_view);
        cardList.setHasFixedSize(true);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(mLinearLayoutManager);

        BikePathAdapter adapter = new BikePathAdapter(bikepath);
        cardList.setAdapter(adapter);
    }

    public void initializeList() {
        bikepath.clear();

        for (int i = 0; i < name.length; i++) {
            PopularPaths path = new PopularPaths();
            path.setCardName(name[i]);
            path.setImageResourceId(image[i]);
            path.setAddress(address[i]);

            bikepath.add(path);
        }
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = mRecyclerView.indexOfChild(v);
            Log.d("Brooklyn Path Activity", String.valueOf(itemPosition));
        }
    }
}

