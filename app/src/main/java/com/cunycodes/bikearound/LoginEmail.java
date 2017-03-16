package com.cunycodes.bikearound;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginEmail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEmail, mPassword;
    private Button btnLogin;
  ///  private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        mAuth =FirebaseAuth.getInstance();

        btnLogin = (Button) findViewById(R.id.login);
        mEmail = (EditText) findViewById(R.id.emailText);
        mPassword =(EditText) findViewById(R.id.passwordText);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

               /* if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Email field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 8) {
                    Toast.makeText(getApplicationContext(), "Password is too short", Toast.LENGTH_SHORT).show();
                    return;
                } */

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginEmail.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(getApplicationContext(), "loginUser:onComplete"+ task.isSuccessful(), Toast.LENGTH_SHORT).show();
//                                progressBar.setVisibility(View.GONE);

                                if(!task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    mEmail.setText("");
                                    mPassword.setText("");
                                    Intent intent = new Intent(LoginEmail.this, MapsActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });
    }
}
