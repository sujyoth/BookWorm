
package com.ash.bookworm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ash.bookworm.Utilities.User;
import com.ash.bookworm.Utilities.Util;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText fNameEt, lNameEt, emailEt, passwordEt;
    private Button registerBtn, locationBtn, imageBtn;
    private TextView hasRegisteredTv;
    private ImageView userImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Double latitude, longitude;
    private Uri imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        findViews();

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(RegisterActivity.this)
                        .crop(1f, 1f)
                        .compress(400)
                        .maxResultSize(300, 300)
                        .start();
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
            startActivityForResult(intent, 2); // Arbitrarily selected 2 as request code
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (!allFieldsValid())
                return;

            final String email = emailEt.getText().toString();
            final String password = passwordEt.getText().toString();
            final String fname = fNameEt.getText().toString();
            final String lname = lNameEt.getText().toString();

            writeNewUser(email, password, fname, lname, latitude, longitude);
            }
        });

        hasRegisteredTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed, here it is 2
        if (requestCode == 2) {
            latitude = data.getDoubleExtra("LATITUDE", 0.0f);
            longitude = data.getDoubleExtra("LONGITUDE", 0.0f);
            Toast.makeText(RegisterActivity.this, latitude.toString() + longitude.toString(), Toast.LENGTH_SHORT).show();

            String locationName = Util.getLocationName(this.getApplicationContext(), latitude, longitude);
            locationBtn.setText(locationName);
        }

        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == RESULT_OK) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Error choosing image. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void findViews() {
        fNameEt = findViewById(R.id.et_first_name);
        lNameEt = findViewById(R.id.et_last_name);
        emailEt = findViewById(R.id.et_email);
        passwordEt = findViewById(R.id.et_password);

        registerBtn = findViewById(R.id.btn_register);
        locationBtn = findViewById(R.id.btn_location);
        imageBtn = findViewById(R.id.btn_image);

        userImage = findViewById(R.id.user_image);

        hasRegisteredTv = findViewById(R.id.tv_has_registered);
    }

    private boolean allFieldsValid() {
        if (Util.isEmpty(fNameEt)) {
            fNameEt.setError("First name cannot be empty");
            return false;
        }
        if (Util.isEmpty(lNameEt)) {
            lNameEt.setError("Last name cannot be empty");
            return false;
        }
        if (Util.isEmpty(emailEt)) {
            emailEt.setError("Email cannot be empty");
            return false;
        }
        if (Util.isEmpty(passwordEt)) {
            passwordEt.setError("Password cannot be empty");
            return false;
        }

        if (latitude == null || longitude == null) {
            locationBtn.setError("Select location before proceeding");
            return false;
        }

        if (!Util.isValidEmail(emailEt.getText().toString())) {
            emailEt.setError("Email must be valid");
            return false;
        }

        if (passwordEt.getText().toString().length() < 6) {
            passwordEt.setError("Password must have at least 6 characters");
        }

        return true;
    }

    private void writeNewUser(String email, String password, final String fname, final String lname, final Double latitude, final Double longitude) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(RegisterActivity.this, "Registration successful.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Adding user details to database
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("users").child(user.getUid()).setValue(new User(fname, lname, latitude, longitude));

                            GeoFire geoFire = new GeoFire(mDatabase.child("geofire"));
                            geoFire.setLocation(user.getUid(), new GeoLocation(latitude, longitude));

                            if (imagePath != null) {
                                // Adding user image to database
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                StorageReference userImageRef = storageRef.child("images/" + user.getUid());

                                userImageRef.putFile(imagePath);
                            }

                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
