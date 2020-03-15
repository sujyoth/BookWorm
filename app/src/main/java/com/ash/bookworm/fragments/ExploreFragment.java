package com.ash.bookworm.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.activities.ScannerActivity;
import com.ash.bookworm.helpers.list_adapters.SearchListAdapter;
import com.ash.bookworm.helpers.models.BaseFragment;
import com.ash.bookworm.helpers.models.Book;
import com.ash.bookworm.helpers.models.User;
import com.ash.bookworm.helpers.utilities.BooksUtil;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends BaseFragment {
    private View root;
    private SearchView searchBar;
    private RecyclerView resultsRv;
    private ImageView scanBarcode;
    private ShimmerFrameLayout shimmerContainer;
    private String ISBN;
    private Bundle bundle;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_explore, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Explore");

        findViews();

        final List<Book> books = new ArrayList<>();
        final SearchListAdapter adapter = new SearchListAdapter(books, 2, getParentFragmentManager());
        resultsRv.setHasFixedSize(true);
        resultsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsRv.setAdapter(adapter);

        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, so asking for it
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, 1);
                    return;
                }


                startActivityForResult(new Intent(getActivity(), ScannerActivity.class), 32);
            }
        });

        searchBar.setActivated(true);
        searchBar.onActionViewExpanded();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                BooksUtil.searchBooks(ExploreFragment.this, adapter, s.trim());
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 32 && resultCode == Activity.RESULT_OK) {
            ISBN = data.getStringExtra("ISBN");

            bundle = new Bundle();
            BooksUtil.getBookByISBN(this, ISBN, bundle);
        }
    }

    @Override
    public void updateUI() {
        String bookId = bundle.getString("bookId");

        Fragment fragment = new BookDetailsFragment();

        Bundle args = new Bundle();
        args.putString("bookId", bookId);
        fragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.add(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack("Explore");
        transaction.commit();
    }

    @Override
    public void updateUI(User user) {
        shimmerContainer.setVisibility(View.GONE);
    }

    private void findViews() {
        searchBar = root.findViewById(R.id.search_bar);
        resultsRv = root.findViewById(R.id.rv_results);
        scanBarcode = root.findViewById(R.id.barcode_button);
        shimmerContainer = root.findViewById(R.id.shimmer_list_container);
    }
}