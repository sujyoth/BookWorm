package com.ash.bookworm.fragments;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.helpers.list_adapters.NearbyListAdapter;
import com.ash.bookworm.helpers.models.BaseFragment;
import com.ash.bookworm.helpers.models.User;
import com.ash.bookworm.helpers.utilities.FirebaseUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class NearbyFragment extends BaseFragment {
    private View root;
    private RecyclerView nearbyRv;
    private String bookId, bookName, authorName, imageUrl;
    private TextView noNearbyUsersTv;
    private ShimmerFrameLayout listContainer;

    private NearbyListAdapter adapter;

    private List<User> nearbyUsers;
    private ActionBar actionBar;
    private String prevTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_nearby, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bookId = bundle.getString("bookId");
            bookName = bundle.getString("bookName");
            authorName = bundle.getString("authorName");
            imageUrl = bundle.getString("imageUrl");
        }

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        prevTitle = actionBar.getTitle().toString();
        actionBar.setTitle("Nearby users who have " + bookName);

        findViews();
        FirebaseUtil.getUserDetails(this, FirebaseAuth.getInstance().getUid());

        nearbyRv.setHasFixedSize(true);
        nearbyRv.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        actionBar.setTitle(prevTitle);
    }

    @Override
    public void updateUI() {
        if (!nearbyUsers.isEmpty()) {
            listContainer.setVisibility(View.GONE);
            noNearbyUsersTv.setVisibility(View.GONE);
            nearbyRv.setVisibility(View.VISIBLE);
        } else {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (nearbyUsers.isEmpty()) {
                        listContainer.setVisibility(View.GONE);
                        noNearbyUsersTv.setVisibility(View.VISIBLE);
                    }
                }
            }, 2000);
        }
    }

    @Override
    public void updateUI(User currentUser) {
        Location currentUserLocation = new Location("point A");
        currentUserLocation.setLatitude(currentUser.getLatitude());
        currentUserLocation.setLongitude(currentUser.getLongitude());

        nearbyUsers = new ArrayList<>();
        adapter = new NearbyListAdapter(currentUserLocation, nearbyUsers);
        nearbyRv.setAdapter(adapter);

        FirebaseUtil.getNearbyUsersWithBook(FirebaseAuth.getInstance().getUid(), bookId, this, nearbyUsers, adapter);
    }

    private void findViews() {
        nearbyRv = root.findViewById(R.id.rv_nearby);
        noNearbyUsersTv = root.findViewById(R.id.tv_no_nearby_users);
        listContainer = root.findViewById(R.id.shimmer_list_container);
    }

}
