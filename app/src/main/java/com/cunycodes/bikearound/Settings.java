package com.cunycodes.bikearound;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Settings extends AppCompatActivity {

    private TextView title;
    private ImageButton back;
    private ImageButton changePassword;
    private ImageButton delete;
    private ImageButton share;
    private SQLiteDatabase db;
    private UserDBHelper helper;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        title = (TextView) findViewById(R.id.titleText);
        title.setText(R.string.nav_settings);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        delete = (ImageButton) findViewById(R.id.btndelete);
        back = (ImageButton) findViewById(R.id.btnBack);
        share = (ImageButton) findViewById(R.id.btnShare);

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
                                        Log.d("Settings", "User password updated.");
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
}
