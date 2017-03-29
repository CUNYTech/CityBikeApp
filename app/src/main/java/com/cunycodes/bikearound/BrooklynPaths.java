package com.cunycodes.bikearound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;


public class BrooklynPaths extends AppCompatActivity {

    private String[] name = {"Prospect Park", "Brooklyn Bridge Park", "Brooklyn Heights Promenade",
                             "Canarsie Pier", "Coney Island", "Eastern Parkway"};
    private int[] image = {R.mipmap.prospectpark, R.mipmap.brooklynbridgepark, R.mipmap.brooklynpromenade,
                            R.mipmap.carnersiepier, R.mipmap.coneyisland, R.mipmap.easternparkway};
    private String[] address = {"Prospect Park, Brooklyn, NY", "Main St (Plymouth St), Brooklyn, NY",
                                "Brooklyn Bridge, Brooklyn, NY", "Shore Pkwy, Brooklyn, NY",
                                "Riegelmann Boardwalk (Stillwell Ave), Brooklyn, NY", "Eastern Parkway, Brooklyn, NY" };

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
