package com.cunycodes.bikearound;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class LoginEmail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mUserReference;
    private ValueEventListener mUserListener;
    private String membership;
    private String name;
    private String time;
    private String email;
    private EditText mEmail, mPassword;
    private Button btnLogin;
    private Button btnForogt;
    private UserDBHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        mAuth =FirebaseAuth.getInstance();

        btnLogin = (Button) findViewById(R.id.login);
        mEmail = (EditText) findViewById(R.id.emailText);
        mPassword =(EditText) findViewById(R.id.passwordText);
        btnForogt = (Button) findViewById(R.id.forgot_password);


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
                                    user = mAuth.getCurrentUser();
                                    mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                                    addListener();

                                }
                            }
                        });
            }
        });

        btnForogt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginEmail.this, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    public boolean isExist(Context context, String database){
        File databaseFile = context.getDatabasePath(database);
        return databaseFile.exists();
    }

    public void restore(){
        if (isExist(LoginEmail.this, "USERINFO.db")){
            Log.d("LoginEmail", "Database exists");
        } else {
            makeLocalDB();
        }

        Intent intent = new Intent(LoginEmail.this, MapsActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public String getTime(String membership){
        if (membership.equals("Annual Member")){
            return "45";
        } else
             return "25";

    }

    public void makeLocalDB(){
        String membership = this.membership;
        String time = getTime(membership);
        String name = this.name;
        String email = this.email;

        helper = new UserDBHelper(this);
        db = helper.getWritableDatabase();
        helper.addUserInfo(name, email, membership, time, db);
        Toast.makeText(getBaseContext(), "Data Restored", Toast.LENGTH_SHORT).show();
        helper.close();
    }

    public  void addListener(){
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                email = user.email;
                name = user.username;
                membership = user.membership;
                restore();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginEmail.this, "Failed to load post", Toast.LENGTH_SHORT).show();
            }
        };
        mUserReference.addValueEventListener(userListener);

        mUserListener = userListener;

    }
}
