package com.ash.bookworm.helpers.list_adapters;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.activities.ChatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder> {
    private String currentUserId;
    private List<String> chatNames;

    public ChatsListAdapter(String currentUserId, List<String> chatNames) {
        this.chatNames = chatNames;
        this.currentUserId = currentUserId;
    }

    @Override
    public ChatsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View chatItem = layoutInflater.inflate(R.layout.chats_item, parent, false);
        ChatsListAdapter.ViewHolder viewHolder = new ChatsListAdapter.ViewHolder(chatItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ChatsListAdapter.ViewHolder holder, int position) {
        if (chatNames.get(position) == null)
            return;
        final String chatName = chatNames.get(position);

        String otherUserId = chatName.replace(currentUserId, "");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("users").child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fname = dataSnapshot.child("fname").getValue().toString();
                String lname = dataSnapshot.child("lname").getValue().toString();
                final String otherUserId = dataSnapshot.child("uId").getValue().toString();
                String name = fname + " "  + lname;

                holder.nameTv.setText(name);

                holder.userImage.setVisibility(View.VISIBLE);
                holder.nameTv.setVisibility(View.VISIBLE);
                holder.lastMessageTv.setVisibility(View.VISIBLE);

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference userImageRef = storageRef.child("images/" + otherUserId);

                userImageRef.getBytes(2048 * 2048).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        holder.userImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                });

                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(view.getContext(), ChatActivity.class);
                        i.putExtra("theirUID", otherUserId);
                        view.getContext().startActivity(i);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("chats").child(chatName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        holder.lastMessageTv.setText(ds.child("message").getValue().toString());
                    } catch (Exception e) {
                        holder.lastMessageTv.setText("\uD83D\uDCF7 Image");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public List<String> getData() {
        return chatNames;
    }

    public void removeChat(int position) {
        chatNames.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() { return chatNames.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView userImage;
        public TextView nameTv, lastMessageTv;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.userImage = itemView.findViewById(R.id.user_image);
            this.nameTv = itemView.findViewById(R.id.tv_name);
            this.lastMessageTv = itemView.findViewById(R.id.tv_last_msg);
            linearLayout = itemView.findViewById(R.id.linear_layout);
        }
    }

}
