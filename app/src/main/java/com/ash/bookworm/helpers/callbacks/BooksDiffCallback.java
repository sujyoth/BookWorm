package com.ash.bookworm.helpers.callbacks;

import androidx.recyclerview.widget.DiffUtil;

import com.ash.bookworm.helpers.models.Book;

import java.util.List;

public class BooksDiffCallback extends DiffUtil.Callback {
    private List<Book> oldBooks, newBooks;

    public BooksDiffCallback(List<Book> oldBooks, List<Book> newBooks) {
        this.oldBooks = oldBooks;
        this.newBooks = newBooks;
    }

    @Override
    public int getOldListSize() {
        return oldBooks == null ? 0 : oldBooks.size();
    }

    @Override
    public int getNewListSize() {
        return newBooks == null ? 0 : newBooks.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldBooks.get(oldItemPosition).equals(newBooks.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldBooks.get(oldItemPosition).getBookId().equals(newBooks.get(newItemPosition).getBookId());
    }
}
