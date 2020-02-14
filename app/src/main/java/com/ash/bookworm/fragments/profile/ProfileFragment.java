package com.ash.bookworm.fragments.profile;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ash.bookworm.activities.LoginActivity;
import com.ash.bookworm.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {


    private View root;
    private ListView profileLv;
    private CircleImageView userImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");

        findViews();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference userImageRef = storageRef.child("images/" + FirebaseAuth.getInstance().getUid());

        userImageRef.getBytes(2048 * 2048).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                userImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        });

        profileLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                switch (pos) {
                    case 0:
                        break;
                    case 1:
                        Fragment fragment = new InventoryFragment();
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.replace(R.id.nav_host_fragment, fragment);
                        transaction.addToBackStack("Profile");
                        transaction.commit();
                        break;
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
        userImage = root.findViewById(R.id.user_image);
    }
}