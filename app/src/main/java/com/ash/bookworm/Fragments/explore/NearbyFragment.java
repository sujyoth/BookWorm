package com.ash.bookworm.Fragments.explore;

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
import com.ash.bookworm.Helpers.Utilities.FirebaseUtil;
import com.ash.bookworm.Helpers.Models.User;
import com.ash.bookworm.Helpers.ListAdapters.UserListAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class NearbyFragment extends Fragment {
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

        List<User> nearbyUsers = new ArrayList<>();
        adapter = new UserListAdapter(nearbyUsers);
        nearbyRv.setHasFixedSize(true);
        nearbyRv.setLayoutManager(new LinearLayoutManager(getContext()));
        nearbyRv.setAdapter(adapter);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(nearbyRv.getContext(),
                DividerItemDecoration.VERTICAL);
        nearbyRv.addItemDecoration(mDividerItemDecoration);

        FirebaseUtil.getNearbyUsersWithBook(FirebaseAuth.getInstance().getUid(), bookId, nearbyUsers, adapter);

        return root;
    }

    private void findViews() {
        nearbyRv = root.findViewById(R.id.rv_nearby);
    }

}
