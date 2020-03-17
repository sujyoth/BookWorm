package com.ash.bookworm.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.helpers.callbacks.SwipeToDeleteCallback;
import com.ash.bookworm.helpers.list_adapters.ChatsListAdapter;
import com.ash.bookworm.helpers.models.BaseFragment;
import com.ash.bookworm.helpers.models.User;
import com.ash.bookworm.helpers.utilities.FirebaseUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends BaseFragment {
    private View root;
    private RecyclerView chatsRv;
    private TextView noChatsTv;

    private ChatsListAdapter adapter;
    private List<String> chatNames;

    private ShimmerFrameLayout listContainer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_chats, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chats");

        findViews();

        chatNames = new ArrayList<>();
        adapter = new ChatsListAdapter(FirebaseAuth.getInstance().getUid(), chatNames);
        chatsRv.setHasFixedSize(true);
        chatsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        chatsRv.setAdapter(adapter);

        enableSwipeToDeleteAndUndo();

        FirebaseUtil.getChatUsers(this, adapter, chatNames, FirebaseAuth.getInstance().getUid());

        return root;
    }

    public void updateUI() {
        if (!chatNames.isEmpty()) {
            listContainer.setVisibility(View.GONE);
            noChatsTv.setVisibility(View.GONE);
            chatsRv.setVisibility(View.VISIBLE);
        } else {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (chatNames.isEmpty()) {
                        listContainer.setVisibility(View.GONE);
                        noChatsTv.setVisibility(View.VISIBLE);
                    }
                }
            }, 2500);
        }
    }

    public void updateUI(User user) {
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final String chatName = adapter.getData().get(position);

                FirebaseUtil.deleteChat(chatName);
                adapter.removeChat(position);
                updateUI();

                Snackbar.make(getView(), "Chat deleted.", Snackbar.LENGTH_LONG).show();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(chatsRv);
    }


    private void findViews() {
        chatsRv = root.findViewById(R.id.rv_chats);
        noChatsTv = root.findViewById(R.id.tv_no_chats);
        listContainer = root.findViewById(R.id.shimmer_list_container);
    }
}