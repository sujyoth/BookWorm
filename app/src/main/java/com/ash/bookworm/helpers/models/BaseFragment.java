package com.ash.bookworm.helpers.models;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    public abstract void updateUI();

    public abstract void updateUI(User user);
}
