package com.ash.bookworm.ui.profile.profile_options;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ash.bookworm.R;
import com.ash.bookworm.Utilities.Book;
import com.ash.bookworm.Utilities.FirebaseUtil;
import com.ash.bookworm.Utilities.InventoryListAdapter;
import com.ash.bookworm.Utilities.SwipeToDeleteCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {

    private View root;
    private FloatingActionButton fab;
    private RecyclerView booksRv;

    private InventoryListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_inventory, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Inventory");
        findViews();

        List<Book> books = new ArrayList<>();
        adapter = new InventoryListAdapter(books);
        booksRv.setHasFixedSize(true);
        booksRv.setLayoutManager(new LinearLayoutManager(getContext()));
        booksRv.setAdapter(adapter);
        enableSwipeToDeleteAndUndo();

        FirebaseUtil.getBooksFromInventory(FirebaseAuth.getInstance().getUid(), adapter, books);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AddBookFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, fragment);
                transaction.addToBackStack("Inventory");
                transaction.commit();
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final Book book = adapter.getData().get(position);

                FirebaseUtil.removeBookFromInventory(adapter.getData().get(position));
                adapter.removeBook(position);

                Snackbar snackbar = Snackbar
                        .make(getView(), book.getBookName() + " by " + book.getAuthorName() + " was removed from inventory.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUtil.addBookToInventory(book);
                        adapter.restoreBook(book, position);
                        booksRv.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(booksRv);
    }


    private void findViews() {
        fab = root.findViewById(R.id.fab);
        booksRv = root.findViewById(R.id.rv_books);
    }

}
