package com.ash.bookworm.Fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.Helpers.Models.Book;
import com.ash.bookworm.Helpers.Utilities.BooksUtil;
import com.ash.bookworm.Helpers.ListAdapters.SearchListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddBookFragment extends Fragment {

    private View root;
    private SearchView searchBar;
    private RecyclerView resultsRv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_add_book, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add Books");
        findViews();

        final List<Book> books = new ArrayList<>();
        final SearchListAdapter adapter = new SearchListAdapter(books, 1);
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
