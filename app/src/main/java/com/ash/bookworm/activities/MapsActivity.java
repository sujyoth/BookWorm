package com.ash.bookworm.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.ash.bookworm.helpers.utilities.Util;
import com.ash.bookworm.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Button confirmBtn;
    private Marker locationMarker;
    private ImageView markerImg;
    private TextView locationNameTV;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        findViews();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnResult();
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        // Assigning default location
        userLocation = new LatLng(19.4, 72.84);
        // Add a marker and move the camera
        locationMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("User Location Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_placeholder)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f));

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                            // Remove default placed marker
                            locationMarker.remove();

                            // Add a marker and move the camera
                            locationMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("User Location Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_placeholder)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f));
                        }
                    }
                });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                locationMarker.setVisible(false);
                markerImg.setVisibility(View.VISIBLE);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                locationMarker.setPosition(mMap.getCameraPosition().target);
                markerImg.setVisibility(View.INVISIBLE);
                locationMarker.setVisible(true);
                locationNameTV.setText(Util.getLocationName(MapsActivity.this, mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude));
            }
        });

    }

    private void returnResult() {
        Double latitude = locationMarker.getPosition().latitude;
        Double longitude = locationMarker.getPosition().longitude;

        Intent i = new Intent();
        i.putExtra("LATITUDE", latitude);
        i.putExtra("LONGITUDE", longitude);
        setResult(2, i);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnResult();
    }

    private void findViews() {
        confirmBtn = findViewById(R.id.btn_confirm);
        markerImg = findViewById(R.id.marker_placeholder);
        locationNameTV = findViewById(R.id.et_location_name);
    }
}
