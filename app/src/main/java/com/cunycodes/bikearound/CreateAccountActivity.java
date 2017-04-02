package com.cunycodes.bikearound;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {

    private final int ANNUALDOCKTIME = 45;
    private final int DAYPASSDOCKTIME = 25;
    private final String TAG = "CreateAccountActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button btnCreate;
    private RadioButton rbtnAnnual, rbtnDayPass;
    private EditText eName, eEmail, ePassword;
   // private Context context;
    private UserDBHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

      //  context = this;
        btnCreate = (Button) findViewById(R.id.btnCreate);
        rbtnAnnual = (RadioButton) findViewById(R.id.rbtnAnnual);
        rbtnDayPass = (RadioButton) findViewById(R.id.rbtnDayPass);
        eName = (EditText) findViewById(R.id.users_name);
        eEmail = (EditText) findViewById(R.id.users_email);
        ePassword = (EditText) findViewById(R.id.users_password);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if(!validate()){
                    return;
                } */

                addContact();

                String email = eEmail.getText().toString();
                String password = ePassword.getText().toString();

                //createUser Method
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(getApplicationContext(), "createUser:onComplete"+ task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                if(!task.isSuccessful()){
                                  Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    onAuthSuccess(task.getResult().getUser());
                                }
                            }
                        });
                mAuth.createUserWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            Log.e("CreateAccount Activiy", "Unable to create account", e);
                    }
                });
            }
        });


    }

    //return membership
    public String getMembership(){
        String membership;

        if(rbtnAnnual.isChecked()){
            membership = "Annual Member";
        } else {
            membership = "Day Pass Member";
        }
        return membership;
    }

    public int getDockTime(){
        if (rbtnAnnual.isChecked()){
            return ANNUALDOCKTIME;
        } else {
            return DAYPASSDOCKTIME;
        }
    }

    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser();
        }
    }

    private void writeNewUser(String userId, String name, String email, String membership) {
        String identifier = email.replaceAll("[^a-zA-Z0-9]","");

        User user = new User(name, email, membership, identifier );

        mDatabase.child("users").child(userId).setValue(user);
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = eName.getText().toString();
        String membership = this.getMembership();

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail(), membership);

        //update Profile

        FirebaseUser newUser =FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        newUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });

        //to set membership


        //clear fields
        eEmail.setText("");
        ePassword.setText("");
        eName.setText(" ");
        rbtnAnnual.setChecked(false);
        rbtnDayPass.setChecked(false);

        // Go to MainActivity
        startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
        finish();
    }


    public boolean validate() {
        String email = eEmail.getText().toString();
        String password = ePassword.getText().toString();
        String name = eName.getText().toString();

        if (!(rbtnDayPass.isChecked() || rbtnAnnual.isChecked())){
            Toast.makeText(getApplicationContext(), "Select a Membership", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Email field is empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8) {
            Toast.makeText(getApplicationContext(), "Password is too short", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(name)){
            Toast.makeText(getApplicationContext(), "Name field is empty", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    public void addContact(){
        String name = eName.getText().toString();
        String email = eEmail.getText().toString();
        String member = getMembership();
        String time =  getDockTime()+"";

        helper = new UserDBHelper(this);
        db = helper.getWritableDatabase();
        helper.addUserInfo(name, email, member, time, db);
        Toast.makeText(getBaseContext(), "Data Saved", Toast.LENGTH_SHORT).show();
        helper.close();

    }
}
