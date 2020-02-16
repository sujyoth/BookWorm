package com.ash.bookworm.fragments.explore;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.helpers.list_adapters.UserListAdapter;
import com.ash.bookworm.helpers.models.BaseFragment;
import com.ash.bookworm.helpers.models.User;
import com.ash.bookworm.helpers.utilities.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class NearbyFragment extends BaseFragment {
    private View root;
    private RecyclerView nearbyRv;
    private String bookId, bookName, authorName, imageUrl;

    private UserListAdapter adapter;

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Nearby users who have " + bookName);

        findViews();
        FirebaseUtil.getUserDetails(this, FirebaseAuth.getInstance().getUid());

        nearbyRv.setHasFixedSize(true);
        nearbyRv.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(nearbyRv.getContext(),
                DividerItemDecoration.VERTICAL);
        nearbyRv.addItemDecoration(mDividerItemDecoration);

        return root;
    }

    @Override
    public void updateUI(User currentUser) {
        Location currentUserLocation = new Location("point A");
        currentUserLocation.setLatitude(currentUser.getLatitude());
        currentUserLocation.setLongitude(currentUser.getLongitude());

        List<User> nearbyUsers = new ArrayList<>();
        adapter = new UserListAdapter(currentUserLocation, nearbyUsers);
        nearbyRv.setAdapter(adapter);

        FirebaseUtil.getNearbyUsersWithBook(FirebaseAuth.getInstance().getUid(), bookId, nearbyUsers, adapter);
    }

    private void findViews() {
        nearbyRv = root.findViewById(R.id.rv_nearby);
    }

}
