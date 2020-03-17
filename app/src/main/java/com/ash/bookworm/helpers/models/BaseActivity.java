package com.ash.bookworm.helpers.models;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    public abstract void updateUI();

    public abstract void updateUI(Bundle bundle, int code);
}
