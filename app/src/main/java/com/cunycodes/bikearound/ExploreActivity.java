package com.cunycodes.bikearound;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class ExploreActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG ="Clicked and Position is ";

    private String[] bikePaths = {"Central Park", "The High Line", "Fort Tyron", "Eastside River",
            "Hudson Walk", "Riverside Park"};

   private int[] images = {R.mipmap.centralpark, R.mipmap.highlinr, R.mipmap.forttyron, R.mipmap.eastriver,
            R.mipmap.hudson, R.mipmap.riverside};
   private String[] address = {"Central Park New York, NY 10024", "The High Line New York, NY 10011", "Fort Tyron Park Riverside Dr To Broadway, New York, NY 10040",
            "John V. Lindsay East River Park East River Promenade, New York, NY 10002", "Hudson River Greenway West Side Highway (Dyckman to Battery Park), New York, NY",
            "Riverside Park New York, NY 10025"};
    private RecyclerView mRecyclerView;
    ArrayList<PopularPaths> bikepath = new ArrayList<>();
    private FirebaseUser user;   // added by Jody --do not delete, comment out if you need to operate without user
    private FirebaseAuth mAuth;   // added by Jody --do not delete, comment out if you need to operate without user
    private TextView nav_name;     // added by Jody --do not delete, comment out if you need to operate without user
    private TextView title;
    private TextView nav_membership;
    private UserDBHelper helper;
    private SQLiteDatabase database;
    private String userMembership;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeList();
        //setTitle("Explore");
        setContentView(R.layout.activity_main_explore);
        title = (TextView) findViewById(R.id.titleTextView);
        title.setText("Explore");

        mAuth = FirebaseAuth.getInstance();  //getInstance    added by Jody --do not delete
        user = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // edit the Navigation Bar
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        nav_name = (TextView) header.findViewById(R.id.user_name);
        nav_membership = (TextView) header.findViewById(R.id.user_membership);
        nav_name.setText(user.getDisplayName());
        setUP();
       // nav_email.setText(user.getEmail());


        RecyclerView cardList = (RecyclerView) findViewById(R.id.card_view);
        cardList.setHasFixedSize(true);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(mLinearLayoutManager);

        BikePathAdapter adapter = new BikePathAdapter(bikepath);
        cardList.setAdapter(adapter);

    }
    public void setUP(){
        String userName = user.getDisplayName();
        helper = new UserDBHelper(getApplicationContext());
        database = helper.getReadableDatabase();
        Cursor cursor = helper.getMembership(userName, database);
        if (cursor.moveToFirst()){
            userMembership = cursor.getString(0);
            nav_membership.setText(userMembership);

        }
    }


    public void initializeList() {
        bikepath.clear();

        for (int i = 0; i < bikePaths.length; i++) {
            PopularPaths path = new PopularPaths();
            path.setCardName(bikePaths[i]);
            path.setImageResourceId(images[i]);
            path.setAddress(address[i]);

            bikepath.add(path);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_history){
            Intent intent = new Intent(this, ProfilePageActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, ProfilePageActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_map){
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = mRecyclerView.indexOfChild(v);
            Log.d(TAG, String.valueOf(itemPosition));
        }
    }
}
