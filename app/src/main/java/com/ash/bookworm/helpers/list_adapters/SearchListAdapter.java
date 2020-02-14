package com.ash.bookworm.helpers.list_adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.fragments.explore.NearbyFragment;
import com.ash.bookworm.helpers.callbacks.BooksDiffCallback;
import com.ash.bookworm.helpers.models.Book;
import com.ash.bookworm.helpers.utilities.FirebaseUtil;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private List<Book> books;
    private Integer listenerType;
    private FragmentManager fragmentManager;

    public SearchListAdapter(List<Book> books, Integer listenerType) {
        this.books = books;
        this.listenerType = listenerType;
    }

    public SearchListAdapter(List<Book> books, Integer listenerType, FragmentManager fragmentManager) {
        this.books = books;
        this.listenerType = listenerType;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View searchItem = layoutInflater.inflate(R.layout.search_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(searchItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Book book = books.get(position);
        if (books.get(position) == null)
            return;
        //holder.userNameTv.setTag(R.string.TAG_BOOK_ID, books[position].getBookId());
        holder.bookNameTv.setText(books.get(position).getBookName());
        holder.authorNameTv.setText(books.get(position).getAuthorName());
        Picasso.get()
                .load(books.get(position).getImageUrl().replace("http", "https"))
                .placeholder(R.drawable.book_placeholder)
                .into(holder.bookImage);

        if (listenerType == 1) {
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseUtil.addBookToInventory(book);
                    Snackbar.make(view, book.getBookName() + " by " + book.getAuthorName() + " added to inventory", Snackbar.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new NearbyFragment();

                    Bundle args = new Bundle();
                    args.putString("bookId", book.getBookId());
                    args.putString("bookName", book.getBookName());
                    args.putString("authorName", book.getAuthorName());
                    args.putString("imageUrl", book.getImageUrl());
                    fragment.setArguments(args);

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, fragment);
                    transaction.addToBackStack("Explore");
                    transaction.commit();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void updateList(List<Book> newBooks) {
        BooksDiffCallback booksDiffCallback = new BooksDiffCallback(this.books, newBooks);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(booksDiffCallback);

        this.books.clear();
        this.books.addAll(newBooks);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView bookImage;
        public TextView bookNameTv, authorNameTv;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.bookImage = itemView.findViewById(R.id.book_image);
            this.bookNameTv = itemView.findViewById(R.id.tv_book_name);
            this.authorNameTv = itemView.findViewById(R.id.tv_author_name);
            linearLayout = itemView.findViewById(R.id.linear_layout);
        }
    }
}
