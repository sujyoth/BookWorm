package com.ash.bookworm.Utilities;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private List<User> users;

    public UserListAdapter(List<User> users) {
        this.users = users;
    }

    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View searchItem = layoutInflater.inflate(R.layout.nearby_item, parent, false);
        UserListAdapter.ViewHolder viewHolder = new UserListAdapter.ViewHolder(searchItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserListAdapter.ViewHolder holder, int position) {
        if (users.get(position) == null)
            return;
        final User user = users.get(position);
        holder.userNameTv.setText(user.getFname() + " " + user.getLname());
        holder.userDistanceTv.setText("5 kms away");

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference userImageRef = storageRef.child("images/" + FirebaseAuth.getInstance().getUid());

        userImageRef.getBytes(2048 * 2048).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                holder.userImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView userImage;
        public TextView userNameTv, userDistanceTv;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.userImage = itemView.findViewById(R.id.user_image);
            this.userNameTv = itemView.findViewById(R.id.tv_user_name);
            this.userDistanceTv = itemView.findViewById(R.id.tv_distance);
            linearLayout = itemView.findViewById(R.id.linear_layout);
        }
    }

}
