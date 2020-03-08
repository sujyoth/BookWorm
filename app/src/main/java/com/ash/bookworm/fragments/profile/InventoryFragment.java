package com.ash.bookworm.fragments.profile;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.helpers.callbacks.SwipeToDeleteCallback;
import com.ash.bookworm.helpers.list_adapters.InventoryListAdapter;
import com.ash.bookworm.helpers.models.BaseFragment;
import com.ash.bookworm.helpers.models.Book;
import com.ash.bookworm.helpers.models.User;
import com.ash.bookworm.helpers.utilities.FirebaseUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends BaseFragment {

    private View root;
    private FloatingActionButton fab;
    private RecyclerView booksRv;
    private List<Book> books = new ArrayList<>();
    private TextView noBooksTv;

    private InventoryListAdapter adapter;
    private ShimmerFrameLayout shimmerContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_inventory, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Inventory");

        findViews();

        adapter = new InventoryListAdapter(books, getParentFragmentManager());
        booksRv.setHasFixedSize(true);
        booksRv.setLayoutManager(new LinearLayoutManager(getContext()));
        booksRv.setAdapter(adapter);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(booksRv.getContext(),
                DividerItemDecoration.VERTICAL);
        booksRv.addItemDecoration(mDividerItemDecoration);

        enableSwipeToDeleteAndUndo();

        FirebaseUtil.getBooksFromInventory(FirebaseAuth.getInstance().getUid(), this, adapter, books);

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

        booksRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab.show();
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown())
                    fab.hide();
            }
        });
        return root;
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final Book book = adapter.getData().get(position);

                FirebaseUtil.removeBookFromInventory(adapter.getData().get(position));
                adapter.removeBook(position);
                updateUI(new User());

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

    @Override
    public void updateUI() {
        shimmerContainer.setVisibility(View.GONE);
        if (!books.isEmpty()) {
            booksRv.setVisibility(View.VISIBLE);
        } else {
            noBooksTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateUI(User user) { }


    private void findViews() {
        fab = root.findViewById(R.id.fab);
        booksRv = root.findViewById(R.id.rv_books);
        noBooksTv = root.findViewById(R.id.tv_no_books);
        shimmerContainer = root.findViewById(R.id.shimmer_list_container);
    }

}
