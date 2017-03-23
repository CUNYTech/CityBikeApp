package com.cunycodes.bikearound;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final String TAG = "ProfilePageActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;
  //  private FirebaseUser user;
    private ValueEventListener mUserListener;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileMembership;
    private TextView nav_name;
    private TextView nav_membership;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uid;
    private String email;
    private String identifier;
    private Button signOut;
    private String userName;
    private String userMembership;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);

       // user = FirebaseAuth.getInstance().getCurrentUser();

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

        mAuth = FirebaseAuth.getInstance();
        profileName = (TextView) findViewById(R.id.profile_name);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileMembership = (TextView) findViewById(R.id.membership);
        signOut = (Button) findViewById(R.id.sign_out);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
              FirebaseUser  user = FirebaseAuth.getInstance().getCurrentUser();    //previously firebaseUser user
                if (user != null){
                     uid = user.getUid();
                     email = user.getEmail();
                     identifier = email.replaceAll("[^a-zA-Z0-9]","");
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + uid + user.getDisplayName());
                    profileEmail.setText(user.getEmail());
                    profileName.setText(user.getDisplayName());
                    nav_name.setText(user.getDisplayName());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                }
            }
        };

        mUserReference = FirebaseDatabase.getInstance().getReference().child("users/");

        mUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "PARENT: "+ childDataSnapshot.getKey());
                    Log.d(TAG,""+ childDataSnapshot.child("membership").getValue());
                //   if (uid.equals(childDataSnapshot.getKey())) {
                    //  userMembership = childDataSnapshot.child("membership").getValue().toString();
                     nav_membership.setText(childDataSnapshot.child("membership").getValue().toString());
                     profileMembership.setText(childDataSnapshot.child("membership").getValue().toString());
                  //  }
                }
              //  recreate();
                // Log.d(TAG, String.valueOf(user1.username));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
            }
        };

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(ProfilePageActivity.this, LoginEmail.class);
                startActivity(intent);
            }
        });
        //mUserReference.orderByChild("identifier").equalTo(identifier).addValueEventListener(mUserListener);
/*
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user1 = dataSnapshot.getValue(User.class);
              //  Log.d(TAG, String.valueOf(user1));
                Log.d(TAG, "onDatabaseReference:userInfo" + user1.email + user1.username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
            }
        }); */




    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        mUserReference.orderByChild("identifier").equalTo(identifier).addListenerForSingleValueEvent(mUserListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }


        if (mUserListener != null){
            mUserReference.removeEventListener(mUserListener);
     }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_history){
            Intent intent = new Intent(this, ProfilePageActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_explore){
            Intent intent = new Intent(this, ExploreActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
