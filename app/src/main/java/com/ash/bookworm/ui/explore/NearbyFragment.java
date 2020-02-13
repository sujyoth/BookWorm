package com.ash.bookworm.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ash.bookworm.R;

public class NearbyFragment extends Fragment {
    private View root;
    private String bookId, bookName, authorName, imageUrl;

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Nearby users with " + bookName);


        return root;
    }

}
