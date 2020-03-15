package com.ash.bookworm.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.helpers.list_adapters.SearchListAdapter;
import com.ash.bookworm.helpers.models.BaseFragment;
import com.ash.bookworm.helpers.models.Book;
import com.ash.bookworm.helpers.models.User;
import com.ash.bookworm.helpers.utilities.BooksUtil;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class AddBookFragment extends BaseFragment {

    private View root;
    private SearchView searchBar;
    private RecyclerView resultsRv;
    private ShimmerFrameLayout shimmerContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_add_book, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add Books");
        findViews();

        final List<Book> books = new ArrayList<>();
        final SearchListAdapter adapter = new SearchListAdapter(books, 1, getParentFragmentManager());
        resultsRv.setHasFixedSize(true);
        resultsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsRv.setAdapter(adapter);

        searchBar.setActivated(true);
        searchBar.onActionViewExpanded();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                BooksUtil.searchBooks(AddBookFragment.this, adapter, s.trim());
                shimmerContainer.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                return false;
            }
        });

        return root;
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void updateUI(User user) {
        shimmerContainer.setVisibility(View.GONE);
    }

    private void findViews() {
        searchBar = root.findViewById(R.id.search_bar);
        resultsRv = root.findViewById(R.id.rv_results);
        shimmerContainer = root.findViewById(R.id.shimmer_list_container);
    }

}
