package com.cunycodes.bikearound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class NewYorkPaths extends AppCompatActivity {


    private String[] name = {"Central Park", "The High Line", "Fort Tyron", "Eastside River",
            "Hudson Walk", "Riverside Park"};

    private int[] image = {R.mipmap.centralpark, R.mipmap.highlinr, R.mipmap.forttyron, R.mipmap.eastriver,
            R.mipmap.hudson, R.mipmap.riverside};
    private String[] address = {"Central Park New York, NY 10024", "The High Line New York, NY 10011", "Fort Tyron Park Riverside Dr To Broadway, New York, NY 10040",
            "John V. Lindsay East River Park East River Promenade, New York, NY 10002", "Hudson River Greenway West Side Highway (Dyckman to Battery Park), New York, NY",
            "Riverside Park New York, NY 10025"};
    private RecyclerView mRecyclerView;
    ArrayList<PopularPaths> bikepath = new ArrayList<>();

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
