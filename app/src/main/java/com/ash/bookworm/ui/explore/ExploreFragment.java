package com.ash.bookworm.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.Utilities.Book;
import com.ash.bookworm.Utilities.BooksUtil;
import com.ash.bookworm.Utilities.SearchListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {
    private View root;
    private SearchView searchBar;
    private RecyclerView resultsRv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_explore, container, false);

        findViews();

        final List<Book> books = new ArrayList<>();
        final SearchListAdapter adapter = new SearchListAdapter(books, 2, getParentFragmentManager());
        resultsRv.setHasFixedSize(true);
        resultsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsRv.setAdapter(adapter);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(resultsRv.getContext(),
                DividerItemDecoration.VERTICAL);
        resultsRv.addItemDecoration(mDividerItemDecoration);

        searchBar.setActivated(true);
        searchBar.onActionViewExpanded();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!s.trim().equals(""))
                    BooksUtil.searchBooks(getContext(), adapter, s.trim());
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