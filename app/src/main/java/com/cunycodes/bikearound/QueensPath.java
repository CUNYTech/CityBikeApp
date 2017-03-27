package com.cunycodes.bikearound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;


public class QueensPath extends AppCompatActivity {
    private String[] name = {"Kissena Park", "Cunningham Park", "Forest Park",
            "Flushing Meadows–Corona Park", "Alley Pond Park"};
    private int[] image = {R.mipmap.kissenapark, R.mipmap.cunnighampark, R.mipmap.forestpark,
            R.mipmap.flushingmeadows, R.mipmap.alleypondpark};
    private String[] address = {"164-15 Booth Memorial Ave, Fresh Meadows, NY 11365", " 196-10 Union Tpke, Fresh Meadows, NY 11366",
            "Forest Park, Richmond Hill, NY 11418", "Flushing Meadows–Corona Park,  Queens, NY 11375",
            "Union Tpke, Oakland Gardens, NY 11364"};

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
