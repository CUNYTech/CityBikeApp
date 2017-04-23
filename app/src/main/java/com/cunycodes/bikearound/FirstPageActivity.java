package com.cunycodes.bikearound;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.firebase.client.Firebase;


public class FirstPageActivity extends AppCompatActivity {

    private ImageButton btnLogin, btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        Firebase.setAndroidContext(this);
        SharedPreferences preferences = getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
        if (preferences.getBoolean("is_logged_before", false)) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            finish();
        } else {

            btnLogin = (ImageButton) findViewById(R.id.btnLogin);
            btnCreate = (ImageButton) findViewById(R.id.btnCreateAccount);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), LoginEmail.class);
                    startActivity(intent);


                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();

                }
            });

            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                    startActivity(intent);
                    //finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
            });
        }

    }
}
