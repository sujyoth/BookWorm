package com.ash.bookworm.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ash.bookworm.LoginActivity;
import com.ash.bookworm.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    private View root;
    private ListView profileLv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        findViews();

        profileLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                switch(pos) {
                    case 2:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                        break;
                    default:
                        break;
                }
            }
        });
        
        return root;
    }

    private void findViews() {
        profileLv = root.findViewById(R.id.lv_profile);
    }
}