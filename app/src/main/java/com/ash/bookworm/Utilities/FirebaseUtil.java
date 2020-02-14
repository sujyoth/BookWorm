package com.ash.bookworm.Utilities;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public final class FirebaseUtil {

    public static void getUserLocation(final UserListAdapter adapter, String uId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Location location = new Location("point A");
                location.setLatitude(user.getLatitude());
                location.setLongitude(user.getLongitude());

                adapter.setCurrentUserLocation(location);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void addBookToInventory(Book book) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).child("inventory").child(book.getBookId()).setValue(book);
    }

    public static void removeBookFromInventory(Book book) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).child("inventory").child(book.getBookId()).removeValue();
    }


    public static void getBooksFromInventory(String uId, final InventoryListAdapter adapter, final List<Book> newBooks) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uId).child("inventory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds1) {
                newBooks.clear();
                for (DataSnapshot ds2 : ds1.getChildren()) {
                    newBooks.add(ds2.getValue(Book.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getNearbyUsersWithBook(String uId, final String searchedBookId, final List<User> nearbyUsers, final UserListAdapter adapter) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds1) {
                User user = ds1.getValue(User.class);

                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                GeoFire geoFire = new GeoFire(ref.child("geofire"));

                // creates a new query around retrieved user location with a radius of 5.0 kilometers
                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(user.getLatitude(), user.getLongitude()), 5.0);
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        Log.d("This user is nearby", key);
                        ref.child("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot ds2) {
                                User nearbyUser = ds2.getValue(User.class);
                                if (ds2.child("inventory").hasChild(searchedBookId)) {
                                    Log.d("This user has book", nearbyUser.fname);
                                    nearbyUsers.add(nearbyUser);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onKeyExited(String key) {

                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {

                    }

                    @Override
                    public void onGeoQueryReady() {

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
