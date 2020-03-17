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
import androidx.recyclerview.widget.RecyclerView;

import com.ash.bookworm.R;
import com.ash.bookworm.fragments.BookDetailsFragment;
import com.ash.bookworm.helpers.models.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InventoryListAdapter extends RecyclerView.Adapter<InventoryListAdapter.ViewHolder> {
    private List<Book> books;
    private FragmentManager fragmentManager;

    public InventoryListAdapter(List<Book> books) {
        this.books = books;
    }

    public InventoryListAdapter(List<Book> books, FragmentManager fragmentManager) {
        this.books = books;
        this.fragmentManager = fragmentManager;
    }


    @Override
    public InventoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View searchItem = layoutInflater.inflate(R.layout.search_item, parent, false);
        InventoryListAdapter.ViewHolder viewHolder = new InventoryListAdapter.ViewHolder(searchItem);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Book book = books.get(position);
        if (books.get(position) == null)
            return;

        holder.infoBtn.setVisibility(View.GONE);
        holder.bookNameTv.setText(books.get(position).getBookName());
        holder.authorNameTv.setText(books.get(position).getAuthorName());
        Picasso.get()
                .load(books.get(position).getImageUrl().replace("http", "https"))
                .placeholder(R.drawable.book_placeholder)
                .into(holder.bookImage);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BookDetailsFragment();

                Bundle args = new Bundle();
                args.putString("bookId", book.getBookId());
                fragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.nav_host_fragment, fragment);
                transaction.addToBackStack("Inventory");
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void removeBook(int position) {
        books.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreBook(Book book, int position) {
        books.add(position, book);
        notifyItemInserted(position);
    }

    public List<Book> getData() {
        return books;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView bookImage, infoBtn;
        public TextView bookNameTv, authorNameTv;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.book_image);
            infoBtn = itemView.findViewById(R.id.btn_info);
            bookNameTv = itemView.findViewById(R.id.tv_book_name);
            authorNameTv = itemView.findViewById(R.id.tv_author_name);
            linearLayout = itemView.findViewById(R.id.linear_layout);
        }
    }


}
