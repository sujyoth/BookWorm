package com.ash.bookworm.helpers.utilities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ash.bookworm.activities.HomeActivity;
import com.ash.bookworm.helpers.list_adapters.InventoryListAdapter;
import com.ash.bookworm.helpers.list_adapters.UserListAdapter;
import com.ash.bookworm.helpers.models.BaseFragment;
import com.ash.bookworm.helpers.models.Book;
import com.ash.bookworm.helpers.models.User;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public final class FirebaseUtil {

    public static void writeNewUser(final Context context, String email, String password, final String fname, final String lname, final Double latitude, final Double longitude, final Uri imagePath) {

        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(context, "Registration successful.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Adding user details to database
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("users").child(user.getUid()).setValue(new User(fname, lname, user.getUid(), latitude, longitude));

                            GeoFire geoFire = new GeoFire(mDatabase.child("geofire"));
                            geoFire.setLocation(user.getUid(), new GeoLocation(latitude, longitude));

                            if (imagePath != null) {
                                // Adding user image to database
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                StorageReference userImageRef = storageRef.child("images/" + user.getUid());

                                userImageRef.putFile(imagePath);
                            }

                            context.startActivity(new Intent(context, HomeActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void getUserDetails(final BaseFragment fragment, String uId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fragment.updateUI(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("getUserDetailsError", databaseError.toString());
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


    public static void getBooksFromInventory(String uId, final BaseFragment fragment, final InventoryListAdapter adapter, final List<Book> newBooks) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uId).child("inventory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds1) {
                newBooks.clear();
                for (DataSnapshot ds2 : ds1.getChildren()) {
                    newBooks.add(ds2.getValue(Book.class));
                }
                fragment.updateUI();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getNearbyUsersWithBook(final String uId, final String searchedBookId, final BaseFragment fragment, final List<User> nearbyUsers, final UserListAdapter adapter) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds1) {
                User user = ds1.getValue(User.class);
                if (user == null)
                    return;

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
                                if (nearbyUser != null && ds2.child("inventory").hasChild(searchedBookId) && !uId.equals(nearbyUser.getuId())) {
                                    Log.d("This user has book", nearbyUser.getFname());
                                    nearbyUsers.add(nearbyUser);
                                    fragment.updateUI();
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                fragment.updateUI();
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
                        fragment.updateUI();
                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {
                        fragment.updateUI();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                fragment.updateUI();
            }
        });
    }
}
