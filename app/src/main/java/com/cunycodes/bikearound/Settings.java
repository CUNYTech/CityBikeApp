package com.cunycodes.bikearound;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;


public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final int ANNUALDOCKTIME = 45;
    private final int DAYPASSDOCKTIME = 25;
    private TextView title;
    private ImageButton back;
    private ImageButton changePassword;
    private ImageButton delete;
    private ImageButton share;
    private SQLiteDatabase db;
    private UserDBHelper helper;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private String previousName;
    private String newName;
    private RadioButton rbtnAnnual;
    private RadioButton rbtnDay;
    private String userMembership;
    private TextView nav_name;                                                                        // added by Jody --do not delete, comment out if you need to operate without user
    private TextView nav_membership;
    private Uri uri;
    private String stringUri;
    private ImageView mUsers_photo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

     //   title = (TextView) findViewById(R.id.titleText);
      //  title.setText(R.string.nav_settings);
        Firebase.setAndroidContext(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mUsers_photo = (ImageView) header.findViewById(R.id.users_photo);
        nav_name = (TextView) header.findViewById(R.id.user_name);
        nav_membership = (TextView) header.findViewById(R.id.user_membership);
        nav_name.setText(user.getDisplayName());
        setUP();

        previousName = user.getDisplayName();

        delete = (ImageButton) findViewById(R.id.btndelete);
        back = (ImageButton) findViewById(R.id.btnBack);
        share = (ImageButton) findViewById(R.id.btnShare);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_explore){
            Intent intent = new Intent(this, RecommendedFragmentExecutor.class);
            startActivity(intent);
            finish();
        }  else if (id == R.id.nav_recommend){
            Intent intent = new Intent(this, FoursquarePath.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_plan){
            Intent intent = new Intent(this, PlanActivity.class);
            startActivity(intent);
            finish();
        } else if(id == R.id.nav_about){
            Intent intent = new Intent(this, AboutUs.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void setUP(){
        String userName = user.getDisplayName();
        helper = new UserDBHelper(getApplicationContext());
        db = helper.getReadableDatabase();
        Cursor cursor = helper.getMembership(userName, db);
        if (cursor.moveToFirst()){
            userMembership = cursor.getString(0);
            nav_membership.setText(userMembership);
        }

        Cursor cursor1 = helper.getPhotoURI(userName, db);
        if (cursor1.moveToFirst()){
            stringUri = cursor1.getString(0);
            uri = Uri.parse(stringUri);
            if (uri!= null){
                displayPhoto();
            } else {
                mUsers_photo.setImageResource(R.mipmap.placeholder_woman);
            }

        }
        helper.close();
    }

    public void displayPhoto(){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            Matrix matrix = new Matrix();
            int rotate = getOrientation(uri);
            if (rotate == 0 && (bitmap.getWidth()> bitmap.getHeight())){
                matrix.postRotate(90);
            } else {
                matrix.postRotate(rotate);
            }
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(),bitmap.getHeight(),matrix, true );
            mUsers_photo.setImageBitmap(newBitmap);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed To Load Photo", Toast.LENGTH_SHORT).show();
            mUsers_photo.setImageResource(R.mipmap.placeholder_woman);
        }

    }

    public int getOrientation(Uri uri){
        ExifInterface exifInterface = null;
        int rotate = 0;
        try {
            exifInterface = new ExifInterface(uri.getPath());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                rotate = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                rotate = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                rotate = 270;
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Orientation not defined", Toast.LENGTH_SHORT).show();
        }

        return rotate;
    }

    public void onClick(View v){
        final AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
        alert
                .setTitle("Delete History")
                .setMessage("This will delete all your bike routes. Are you sure you want to delete your history?")
                 .setCancelable(true)
             .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              /*  //delete user history from local storage
                helper = new UserDBHelper(Settings.this);
                db = helper.getWritableDatabase();
                db.delete("HISTORY", null, null);
                db.close();  */
                Toast.makeText(getApplicationContext(),"No history", Toast.LENGTH_SHORT).show();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).create().show();
    }

    public void onBack(View v){
        onBackPressed();
    }

    public void onShare(View v){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bike Around");
        intent.putExtra(Intent.EXTRA_TEXT, "Download the Bike Around App from the play store h//bity.ly");
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    public void  onChangeName(View v){
        LayoutInflater inflater = LayoutInflater.from(Settings.this);
        View promptView = inflater.inflate(R.layout.name_prompt, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setView(promptView);

        final EditText name = (EditText) promptView.findViewById(R.id.name_prompt);
        builder.setCancelable(false)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newName = name.getText().toString();
                        updateNameFirebase();
                        updateNameLocally();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void updateNameLocally(){

        helper = new UserDBHelper(this);
        db = helper.getWritableDatabase();
        helper.updateName(previousName, newName, db);
        Toast.makeText(getApplicationContext(), "User Updated", Toast.LENGTH_SHORT).show();
        helper.close();
    }

    public void updateNameFirebase(){
        UserProfileChangeRequest nameUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        user.updateProfile(nameUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Username Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onSignOut(View v){
        Firebase.setAndroidContext(this);
        SharedPreferences preferences = getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        mAuth.signOut();
        Intent intent = new Intent(Settings.this, LoginEmail.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onChange(View v){
        LayoutInflater inflate = LayoutInflater.from(Settings.this);
        View promptView = inflate.inflate(R.layout.prompt, null);

        AlertDialog.Builder alert  = new AlertDialog.Builder(Settings.this);
        alert.setView(promptView);

        EditText oldPassword = (EditText) promptView.findViewById(R.id.passwordInput);
        final EditText newPassword = (EditText) promptView.findViewById(R.id.passwordNew);
        final EditText confirmPassword = (EditText) promptView.findViewById(R.id.passwordConfirm);

        alert
                .setCancelable(false)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (newPassword.getText().equals(confirmPassword.getText())){
                            user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Settings", "Password updated.");
                                    }
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                             dialogInterface.cancel();
                    }
                });

        AlertDialog dialog = alert.create();

        dialog.show();;
    }

    public void onChangeMembership(View v){
        LayoutInflater inflater = LayoutInflater.from(Settings.this);
        View promptView = inflater.inflate(R.layout.membership_prompt, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setView(promptView);

        rbtnDay = (RadioButton) findViewById(R.id.rbtnDayPassChange);
        rbtnAnnual = (RadioButton) findViewById(R.id.rbtnAnnualChange);

        builder.setCancelable(false)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!(rbtnDay.isChecked() || rbtnAnnual.isChecked()))
                            Toast.makeText(getApplicationContext(), "Select a Membership", Toast.LENGTH_SHORT).show();
                        else
                             updateTime();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public String getMembership(){

        String membership;

        if(rbtnAnnual.isChecked()){
            membership = "Annual Member";
        } else {
            membership = "Day Pass Member";
        }
        return membership;
    }

    public int getTime(){
        if (rbtnAnnual.isChecked()){
            return ANNUALDOCKTIME;
        } else {
            return DAYPASSDOCKTIME;}

    }
    public void updateTime(){
        String time = getTime()+"";
        String membership = getMembership();
        reference.child("users").child(user.getUid()).child("membership").setValue(membership);

        helper = new UserDBHelper(this);
        db = helper.getWritableDatabase();
        helper.updateTime(user.getDisplayName(), time, db);
        helper.updateMembership(user.getDisplayName(), membership, db);
        helper.close();
    }
}
