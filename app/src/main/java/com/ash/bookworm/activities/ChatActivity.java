package com.ash.bookworm.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.ash.bookworm.R;

public class ChatActivity extends AppCompatActivity {
    private CoordinatorLayout sendBtn;
    private ImageView setImageBtn;
    private EditText messageEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        findViews();

    }

    private void findViews() {
        sendBtn = findViewById(R.id.btn_send);
        setImageBtn = findViewById(R.id.btn_set_image);
        messageEt = findViewById(R.id.et_message);
    }
}
