package com.ash.bookworm.ui.profile.profile_options;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ash.bookworm.R;

public class AddBookFragment extends Fragment {

    private View root;
    private SearchView searchBar;
    private RecyclerView resultsRv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_add_book, container, false);

        findViews();

        searchBar.setActivated(true);
        searchBar.onActionViewExpanded();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return root;
    }

    private void findViews() {
        searchBar = root.findViewById(R.id.search_bar);
        resultsRv = root.findViewById(R.id.rv_results);
    }

}
