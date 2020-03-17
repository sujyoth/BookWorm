package com.ash.bookworm.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.ash.bookworm.R;
import com.ash.bookworm.helpers.models.BaseActivity;
import com.ash.bookworm.helpers.models.Message;
import com.ash.bookworm.helpers.models.User;
import com.ash.bookworm.helpers.utilities.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ChatActivity extends BaseActivity {
    private FloatingActionButton sendBtn;
    private ImageView setImageBtn, backBtn, userImage;
    private EditText messageEt;
    private Toolbar toolbar;
    private TextView nameTv;

    private String yourUID, theirUID;
    private User you, them;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        yourUID = FirebaseAuth.getInstance().getUid();
        theirUID = getIntent().getStringExtra("theirUID");

        FirebaseUtil.getUserDetails(this, theirUID);

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
                if(messageText.length() > 0) {
                    Message message = new Message(messageText, yourUID, theirUID, null);
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

    }

    @Override
    public void updateUI(Bundle bundle, int code) {
        if (code == 0) {
            String name = bundle.getString("fname") + " " + bundle.getString("lname");
            nameTv.setText(name);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference userImageRef = storageRef.child("images/" + theirUID);

            userImageRef.getBytes(2048 * 2048)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            userImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imagePath = data.getData();
            FirebaseUtil.sendImageMessage(new Message(null, yourUID, theirUID, UUID.randomUUID().toString()), imagePath);
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
    }

}
