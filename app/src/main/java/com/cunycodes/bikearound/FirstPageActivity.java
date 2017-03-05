package com.cunycodes.bikearound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstPageActivity extends AppCompatActivity {

    private Button btnLogin, btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCreate = (Button) findViewById(R.id.btnCreateAccount);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginEmail.class);
                startActivity(intent);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstPageActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }

}
