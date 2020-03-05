package com.ash.bookworm.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ash.bookworm.R;
import com.ash.bookworm.helpers.utilities.FirebaseUtil;
import com.ash.bookworm.helpers.utilities.Util;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private EditText fNameEt, lNameEt, emailEt, passwordEt;
    private Button registerBtn, locationBtn, imageBtn;
    private Button hasRegisteredTv;
    private ImageView userImage;

    private Double latitude, longitude;
    private Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(RegisterActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }


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

                FirebaseUtil.writeNewUser(RegisterActivity.this, email, password, fname, lname, latitude, longitude, imagePath);
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
        if (requestCode == 2 && resultCode == RESULT_OK) {
            latitude = data.getDoubleExtra("LATITUDE", 0.0f);
            longitude = data.getDoubleExtra("LONGITUDE", 0.0f);

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
                Toast.makeText(this, "Error choosing image. Please try again." + requestCode, Toast.LENGTH_SHORT).show();
            }
        }
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
}
