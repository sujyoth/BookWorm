package com.ash.bookworm.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ash.bookworm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatActivity extends AppCompatActivity {
    private FloatingActionButton sendBtn;
    private ImageView setImageBtn, backBtn;
    private EditText messageEt;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void findViews() {
        sendBtn = findViewById(R.id.btn_send);
        setImageBtn = findViewById(R.id.btn_set_image);
        messageEt = findViewById(R.id.et_message);

        backBtn = findViewById(R.id.btn_back);

        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
                finish();
                return true;
        }
    }
}
