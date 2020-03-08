package com.ash.bookworm.fragments.other;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ash.bookworm.R;
import com.ash.bookworm.fragments.explore.NearbyFragment;
import com.ash.bookworm.helpers.models.BaseFragment;
import com.ash.bookworm.helpers.models.Book;
import com.ash.bookworm.helpers.models.User;
import com.ash.bookworm.helpers.utilities.BooksUtil;
import com.ash.bookworm.helpers.utilities.FirebaseUtil;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

public class BookDetailsFragment extends BaseFragment {
    private Bundle bundle;
    private View root;

    private ImageView bookImg;
    private TextView bookTitleTv, bookDescTv, authorNameTv;
    private Button addBtn, searchBtn;

    private String bookId, bookTitle, imageUrl, authorName;
    private String prevTitle;

    private ActionBar actionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_book_details, container, false);

        actionBar =  ((AppCompatActivity) getActivity()).getSupportActionBar();
        prevTitle = actionBar.getTitle().toString();
        actionBar.setTitle("Book Details");

        findViews();

        bookId = getArguments().getString("bookId");

        Toast.makeText(getContext(), bookId, Toast.LENGTH_SHORT).show();


        bundle = new Bundle();
        BooksUtil.getBookDetails(this, bookId, bundle);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtil.addBookToInventory(new Book(bookId, bookTitle, authorName, imageUrl));
                Snackbar.make(view, bookTitle + " by " + authorName + " added to inventory", Snackbar.LENGTH_SHORT).show();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new NearbyFragment();

                Bundle args = new Bundle();
                args.putString("bookId", bookId);
                args.putString("bookName", bookTitle);
                args.putString("authorName", authorName);
                args.putString("imageUrl", imageUrl);
                fragment.setArguments(args);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.add(R.id.nav_host_fragment, fragment);
                transaction.addToBackStack("Explore");
                transaction.commit();
            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        actionBar.setTitle(prevTitle);
    }

    @Override
    public void updateUI() {
        bookTitle = bundle.getString("bookTitle");
        authorName = bundle.getString("authorName");
        imageUrl = bundle.getString("imageUrl");

        bookTitleTv.setText(bookTitle);
        authorNameTv.setText(authorName);

        if (bundle.getString("bookDesc").equals("")) {
            bookDescTv.setText("No description found for this book");
        } else {
            bookDescTv.setText(Html.fromHtml(bundle.getString("bookDesc")));
        }

        Picasso.get()
                .load(imageUrl.replace("http", "https"))
                .placeholder(R.drawable.book_placeholder)
                .into(bookImg);
    }

    @Override
    public void updateUI(User user) {

    }

    private void findViews() {
        bookImg = root.findViewById(R.id.book_image);
        bookTitleTv = root.findViewById(R.id.tv_book_title);
        authorNameTv = root.findViewById(R.id.tv_author_name);
        bookDescTv = root.findViewById(R.id.tv_book_desc);
        addBtn = root.findViewById(R.id.btn_add);
        searchBtn = root.findViewById(R.id.btn_search);
    }
}
