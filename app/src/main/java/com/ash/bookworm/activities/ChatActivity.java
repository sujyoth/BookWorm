package com.ash.bookworm.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.helpers.list_adapters.MessageListAdapter;
import com.ash.bookworm.helpers.models.BaseActivity;
import com.ash.bookworm.helpers.models.Message;
import com.ash.bookworm.helpers.utilities.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends BaseActivity {
    private FloatingActionButton sendBtn;
    private ImageView userImage;
    private ImageButton backBtn, setImageBtn;
    private EditText messageEt;
    private Toolbar toolbar;
    private TextView nameTv;

    private RecyclerView messagesRv;
    private MessageListAdapter adapter;
    private List<Message> messages;

    private String currentUserId, otherUserId, chatName;

    private RelativeLayout previewLayout;
    private ImageView previewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        currentUserId = FirebaseAuth.getInstance().getUid();
        otherUserId = getIntent().getStringExtra("theirUID");

        chatName = currentUserId + otherUserId;
        int compResult = currentUserId.compareTo(otherUserId);
        if (compResult > 0) {
            chatName = otherUserId + currentUserId;
        }

        FirebaseUtil.getUserDetails(this, otherUserId);

        messages = new ArrayList<>();
        adapter = new MessageListAdapter(currentUserId, messages, getSupportFragmentManager(), this);
        messagesRv.setAdapter(adapter);
        messagesRv.setHasFixedSize(true);
        messagesRv.setItemViewCacheSize(20);
        messagesRv.setDrawingCacheEnabled(true);
        messagesRv.setLayoutManager(new LinearLayoutManager(this));


        FirebaseUtil.getMessages(this, adapter, messages, chatName);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        messageEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    sendBtn.performClick();
                    return true;
                }
                return false;
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = messageEt.getText().toString().trim();
                if (messageText.length() > 0) {
                    Message message = new Message(messageText, currentUserId, otherUserId, null);
                    FirebaseUtil.sendTextMessage(message);
                    messageEt.setText("");
                }
            }
        });

        setImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(ChatActivity.this)
                        .crop()
                        .compress(500)
                        .maxResultSize(500, 500)
                        .start();
            }
        });

        previewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewLayout.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
                finish();
                return true;
        }
    }

    @Override
    public void updateUI() {
        messagesRv.scrollToPosition(messages.size() - 1);
    }

    @Override
    public void updateUI(Bundle bundle, int code) {
        if (code == 0) {
            String name = bundle.getString("fname") + " " + bundle.getString("lname");
            nameTv.setText(name);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference userImageRef = storageRef.child("images/" + otherUserId);

            userImageRef.getBytes(2048 * 2048)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(final byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            userImage.setImageBitmap(bitmap);

                            userImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // To show user's profile photo in fullscreen when clicked
                                    Bundle bundle = new Bundle();
                                    bundle.putByteArray("preview_image", bytes);
                                    updateUI(bundle, 1);
                                }
                            });
                        }
                    });
        } else if (code == 1) {
            byte[] bytes = bundle.getByteArray("preview_image");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            previewImage.setImageBitmap(bitmap);
            previewLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imagePath = data.getData();
            FirebaseUtil.sendImageMessage(new Message(null, currentUserId, otherUserId, UUID.randomUUID().toString()), imagePath);
        }
    }

    private void findViews() {
        sendBtn = findViewById(R.id.btn_send);
        setImageBtn = findViewById(R.id.btn_set_image);
        messageEt = findViewById(R.id.et_message);

        backBtn = findViewById(R.id.btn_back);
        userImage = findViewById(R.id.user_image);
        nameTv = findViewById(R.id.tv_name);

        toolbar = findViewById(R.id.toolbar);

        messagesRv = findViewById(R.id.rv_messages);

        previewLayout = findViewById(R.id.preview_layout);
        previewImage = findViewById(R.id.img_preview);
    }

    @Override
    public void onBackPressed() {
        if (previewLayout.getVisibility() == View.VISIBLE) {
            previewLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
