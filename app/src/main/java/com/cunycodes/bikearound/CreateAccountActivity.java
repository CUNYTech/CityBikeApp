package com.cunycodes.bikearound;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CreateAccountActivity extends AppCompatActivity {

    private static final int REQUEST_PHOTO = 2;
    private final int ANNUALDOCKTIME = 45;
    private final int DAYPASSDOCKTIME = 25;
    private final String TAG = "CreateAccountActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button btnCreate;
    private RadioButton rbtnAnnual, rbtnDayPass;
    private EditText eName, eEmail, ePassword;
    private Context context;
    private UserDBHelper helper;
    private SQLiteDatabase db;
    private SharedPreferences preferences;
    private ImageView mUsers_photo;
    private String mCurrentPhotoPath;
    private StorageReference storage;
    private Uri photoUri;
    private String lastPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        context = this;
        btnCreate = (Button) findViewById(R.id.btnCreate);
        rbtnAnnual = (RadioButton) findViewById(R.id.rbtnAnnual);
        rbtnDayPass = (RadioButton) findViewById(R.id.rbtnDayPass);
        eName = (EditText) findViewById(R.id.users_name);
        eEmail = (EditText) findViewById(R.id.users_email);
        ePassword = (EditText) findViewById(R.id.users_password);
        mUsers_photo = (ImageView) findViewById(R.id.users_photo);

        final Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mUsers_photo.setEnabled(true);

        mUsers_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivityForResult(takePicture, REQUEST_PHOTO);
                dispatchTakePictureIntent();
            }
        });



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

    private void writeNewUser(String userId, String name, String email, String membership,Uri uri) {
        String identifier = email.replaceAll("[^a-zA-Z0-9]","");

        User user = new User(name, email, membership, identifier, String.valueOf(uri));

        mDatabase.child("users").child(userId).setValue(user);
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = eName.getText().toString();
        String membership = this.getMembership();

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail(), membership, photoUri);

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

        //set SharedPreferences
        preferences = context.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userId", newUser.getUid());
        editor.putBoolean("is_logged_before", true);
        editor.commit();

        //clear fields
        eEmail.setText("");
        eEmail.setEnabled(false);
        ePassword.setText("");
        ePassword.setEnabled(false);
        eName.setText(" ");
        eName.setEnabled(false);
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
        helper.addUserInfo(name, email, member,String.valueOf(photoUri), time, db);
        Toast.makeText(getBaseContext(), "Data Saved", Toast.LENGTH_SHORT).show();
        helper.close();

    }

    private void dispatchTakePictureIntent(){
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Ensure that there is a camera activity to handle intent
        if(pictureIntent.resolveActivity(getPackageManager())!=null){
            //Create the file where the photo will go
            File photoFile  = null;
            try {
                photoFile = createImageFile();
            } catch (IOException E){
                //Error creating file
            }

            if (photoFile != null){
                photoUri = FileProvider.getUriForFile(this,
                        "com.cunycodes.bikearound.fileprovider",
                        photoFile);

                List<ResolveInfo> resolveInfoList =
                        context.getPackageManager().queryIntentActivities(pictureIntent,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo: resolveInfoList){
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, photoUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                pictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, REQUEST_PHOTO);

            }
        }

    }

    private File createImageFile() throws IOException{
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+ timestamp +"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg",        /* suffix */
                storageDir
        );

        //Save a file: path to be used with getActionView

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PHOTO && resultCode == this.RESULT_OK ) {
            // mUsers_photo.setImageURI(photoUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                round.setCircular(true);
                // round.setCornerRadius(50.0f);
                // round.setAntiAlias(true);
                mUsers_photo.setImageDrawable(round);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            StorageReference filepath = storage.child("images").child(uri.getLastPathSegment());

        }
    }

}