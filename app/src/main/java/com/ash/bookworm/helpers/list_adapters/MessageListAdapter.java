package com.ash.bookworm.helpers.list_adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.helpers.models.BaseActivity;
import com.ash.bookworm.helpers.models.Message;
import com.ash.bookworm.helpers.utilities.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private static final int MSG_TYPE_LEFT_TEXT=0;
    private static final int MSG_TYPE_RIGHT_TEXT=1;
    private static final int MSG_TYPE_LEFT_IMG=2;
    private static final int MSG_TYPE_RIGHT_IMG=3;
    private final String currentUserId;
    private final List<Message> messages;
    private final FragmentManager fragmentManager;
    private final BaseActivity activity;

    public MessageListAdapter(String currentUserId, List<Message> messages, FragmentManager fragmentManager, BaseActivity activity) {
        this.currentUserId = currentUserId;
        this.messages = messages;
        this.fragmentManager = fragmentManager;
        this.activity = activity;
    }

    @Override
    public MessageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT_TEXT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item_left, parent, false);
            return new MessageListAdapter.ViewHolder(view);
        } else if (viewType == MSG_TYPE_RIGHT_TEXT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item_right, parent, false);
            return new MessageListAdapter.ViewHolder(view);
        } else if (viewType == MSG_TYPE_LEFT_IMG) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_msg_item_left, parent, false);
            return new MessageListAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_msg_item_right, parent, false);
            return new MessageListAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final MessageListAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message == null)
            return;
        if (message.getMessage() != null) {
            // This means message is a text message
            holder.messageTv.setText(message.getMessage());
        } else {
            // This means message is a image message
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("images/chats/" + message.getImageId());

            holder.messageImg.setImageBitmap(null);
            holder.progressBar.setVisibility(View.VISIBLE);
            imageRef.getBytes(2048 * 2048)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(final byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            holder.messageImg.setImageBitmap(bitmap);
                            holder.progressBar.setVisibility(View.GONE);

                            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // To show fullscreen preview of image when it is clicked
                                    Bundle bundle = new Bundle();
                                    bundle.putByteArray("preview_image", bytes);
                                    activity.updateUI(bundle, 1);
                                }
                            });
                        }
                    });
        }

        // For setting different margins when the sender for consecutive messages is the same
        if (position != 0) {
            Message prevMessage = messages.get(position-1);
            if (prevMessage.sender.equals(message.sender)) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                int topMargin = (int) Util.dpToPixels(activity, 2);
                int nearMargin = (int) Util.dpToPixels(activity, 16);
                int farMargin = (int) Util.dpToPixels(activity, 40);

                if (message.sender.equals(currentUserId)) {
                    params.setMargins(farMargin,topMargin,nearMargin,0);
                } else {
                    params.setMargins(nearMargin,topMargin,farMargin,0);
                }
                holder.linearLayout.setLayoutParams(params);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(currentUserId) && messages.get(position).getMessage() != null) {
            return MSG_TYPE_RIGHT_TEXT;
        } else if  (!messages.get(position).getSender().equals(currentUserId) && messages.get(position).getMessage() != null) {
            return MSG_TYPE_LEFT_TEXT;
        } else if (messages.get(position).getSender().equals(currentUserId)) {
            return MSG_TYPE_RIGHT_IMG;
        } else {
            return MSG_TYPE_LEFT_IMG;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTv;
        public ImageView messageImg;
        public RelativeLayout linearLayout;
        public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linear_layout);
            messageTv = itemView.findViewById(R.id.tv_message);
            messageImg = itemView.findViewById(R.id.img_message);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
