package com.ash.bookworm.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ash.bookworm.R;
import com.ash.bookworm.activities.MapsActivity;
import com.ash.bookworm.helpers.models.BaseFragment;
import com.ash.bookworm.helpers.models.User;
import com.ash.bookworm.helpers.utilities.FirebaseUtil;
import com.ash.bookworm.helpers.utilities.Util;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class EditProfileFragment extends BaseFragment {
    private View root;

    private CoordinatorLayout coordinatorLayout;
    private EditText fnameEt, lnameEt;
    private TextInputLayout fnameTil, lnameTil;
    private Button confirmBtn, locationBtn, imageBtn;
    private ImageView userImage;

    private Double latitude, longitude;
    private Uri imagePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Edit Profile");

        findViews();

        FirebaseUtil.getUserDetails(this, FirebaseAuth.getInstance().getUid());

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(EditProfileFragment.this)
                        .crop(1f, 1f)
                        .compress(400)
                        .maxResultSize(300, 300)
                        .start();
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(EditProfileFragment.this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, so asking for it
                    ActivityCompat.requestPermissions(EditProfileFragment.this.getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }


                Intent intent = new Intent(EditProfileFragment.this.getActivity(), MapsActivity.class);
                startActivityForResult(intent, 2); // Arbitrarily selected 2 as request code
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allFieldsValid())
                    return;

                final String fname = fnameEt.getText().toString();
                final String lname = lnameEt.getText().toString();

                FirebaseUtil.writeUserDetails(EditProfileFragment.this, FirebaseAuth.getInstance().getUid(), fname, lname, latitude, longitude, imagePath);
            }
        });

        return root;
    }

    @Override
    public void updateUI() {
        Snackbar.make(coordinatorLayout, "Your profile has been updated.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void updateUI(User user) {
        fnameEt.setText(user.getFname());
        lnameEt.setText(user.getLname());

        latitude = user.getLatitude();
        longitude = user.getLongitude();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference userImageRef = storageRef.child("images/" + user.getuId());

        userImageRef.getBytes(2048 * 2048)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        userImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                });

        String locationName = Util.getLocationName(this.getContext(), latitude, longitude);
        locationBtn.setText(locationName);
        locationBtn.setError(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed, here it is 2
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            latitude = data.getDoubleExtra("LATITUDE", 0.0f);
            longitude = data.getDoubleExtra("LONGITUDE", 0.0f);

            String locationName = Util.getLocationName(this.getContext(), latitude, longitude);
            locationBtn.setText(locationName);
            locationBtn.setError(null);
        }

        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagePath);
                userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error choosing image. Please try again." + requestCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void findViews() {
        coordinatorLayout = root.findViewById(R.id.linear_layout);

        fnameEt = root.findViewById(R.id.et_first_name);
        lnameEt = root.findViewById(R.id.et_last_name);

        fnameTil = root.findViewById(R.id.til_fname);
        lnameTil = root.findViewById(R.id.til_lname);

        confirmBtn = root.findViewById(R.id.btn_confirm);
        locationBtn = root.findViewById(R.id.btn_location);
        imageBtn = root.findViewById(R.id.btn_image);

        userImage = root.findViewById(R.id.user_image);
    }

    private boolean allFieldsValid() {
        if (Util.isEmpty(fnameEt)) {
            fnameTil.setError("First name can't be empty");
            return false;
        } else {
            fnameTil.setError(null);
        }

        if (Util.isEmpty(lnameEt)) {
            lnameTil.setError("Last name can't be empty");
            return false;
        } else {
            lnameTil.setError(null);
        }

        if (latitude == null || longitude == null) {
            locationBtn.setError("Select location before proceeding");
            return false;
        } else {
            locationBtn.setError(null);
        }

        return true;
    }
}
