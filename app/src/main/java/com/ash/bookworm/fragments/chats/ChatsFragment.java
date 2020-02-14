package com.ash.bookworm.fragments.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ash.bookworm.R;

public class ChatsFragment extends Fragment {
    private View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_chats, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chats");

        return root;
    }
}