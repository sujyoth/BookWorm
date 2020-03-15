package com.ash.bookworm.fragments;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ash.bookworm.R;
import com.ash.bookworm.activities.LoginActivity;
import com.ash.bookworm.helpers.models.BaseFragment;
import com.ash.bookworm.helpers.models.User;
import com.ash.bookworm.helpers.utilities.FirebaseUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends BaseFragment {

    private View root;
    private ListView profileLv;
    private CircleImageView userImage;
    private TextView userNameTv;

    private ShimmerFrameLayout imageContainer, textContainer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");

        findViews();
        FirebaseUtil.getUserDetails(this, FirebaseAuth.getInstance().getUid());

        profileLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                switch (pos) {
                    case 0:
                        Fragment fragment1 = new EditProfileFragment();
                        FragmentTransaction transaction1 = getParentFragmentManager().beginTransaction();
                        transaction1.replace(R.id.nav_host_fragment, fragment1);
                        transaction1.addToBackStack("Profile");
                        transaction1.commit();
                        break;
                    case 1:
                        Fragment fragment2 = new InventoryFragment();
                        FragmentTransaction transaction2 = getParentFragmentManager().beginTransaction();
                        transaction2.replace(R.id.nav_host_fragment, fragment2);
                        transaction2.addToBackStack("Profile");
                        transaction2.commit();
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

    @Override
    public void updateUI() {
    }

    @Override
    public void updateUI(User user) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference userImageRef = storageRef.child("images/" + user.getuId());

        userImageRef.getBytes(2048 * 2048)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        imageContainer.hideShimmer();
                        userImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        imageContainer.hideShimmer();
                    }
                });

        textContainer.hideShimmer();
        textContainer.setVisibility(View.GONE);
        userNameTv.setVisibility(View.VISIBLE);
        userNameTv.setText(user.getFname() + " " + user.getLname());
    }

    private void findViews() {
        profileLv = root.findViewById(R.id.lv_profile);
        userImage = root.findViewById(R.id.user_image);
        userNameTv = root.findViewById(R.id.tv_user_name);
        imageContainer = root.findViewById(R.id.shimmer_image_container);
        textContainer = root.findViewById(R.id.shimmer_text_container);
    }
}