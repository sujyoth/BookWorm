package com.ash.bookworm.fragments.profile;

import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.ash.bookworm.helpers.list_adapters.SearchListAdapter;
import com.ash.bookworm.helpers.models.Book;
import com.ash.bookworm.helpers.utilities.BooksUtil;

import java.util.ArrayList;
import java.util.List;

public class AddBookFragment extends Fragment {

    private View root;
    private SearchView searchBar;
    private RecyclerView resultsRv;

    private int waitingTime = 200;
    private CountDownTimer timer;


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
            public boolean onQueryTextChange(final String s) {
                if (!s.trim().equals("") && s.trim().length() > 2) {
                    if(timer != null){
                        timer.cancel();
                    }
                    timer = new CountDownTimer(waitingTime, 500) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            BooksUtil.searchBooks(getContext(), adapter, s.trim());
                        }
                    };
                    timer.start();
                }
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
