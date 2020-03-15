package com.ash.bookworm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.helpers.models.BaseFragment;
import com.ash.bookworm.helpers.models.User;
import com.facebook.shimmer.ShimmerFrameLayout;

public class ChatsFragment extends BaseFragment {
    private View root;
    private RecyclerView chatsRv;
    private TextView noChatsTv;

    private ShimmerFrameLayout listContainer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_chats, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chats");

        findViews();

        return root;
    }

    public void updateUI() {
    }

    public void updateUI(User user) {
    }

    private void findViews() {
        chatsRv = root.findViewById(R.id.rv_chats);
        noChatsTv = root.findViewById(R.id.tv_no_chats);
        listContainer = root.findViewById(R.id.shimmer_list_container);
    }
}