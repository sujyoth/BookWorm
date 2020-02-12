package com.ash.bookworm.ui.profile.profile_options;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ash.bookworm.R;
import com.ash.bookworm.Utilities.SearchListAdapter;
import com.ash.bookworm.Utilities.SearchListData;

public class AddBookFragment extends Fragment {

    private View root;
    private SearchView searchBar;
    private RecyclerView resultsRv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_add_book, container, false);

        findViews();

        searchBar.setActivated(true);
        searchBar.onActionViewExpanded();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        SearchListData searchListData[] = new SearchListData[] {
            new SearchListData("Hola","b", "https://2uiipg1ex7ap1ro233smnm15-wpengine.netdna-ssl.com/wp-content/uploads/2015/04/google-analytics-ghost-referrals-150x150.jpg"),
            new SearchListData("Bola","b", "https://2uiipg1ex7ap1ro233smnm15-wpengine.netdna-ssl.com/wp-content/uploads/2015/04/google-analytics-ghost-referrals-150x150.jpg"),
            new SearchListData("Cola","b", "https://2uiipg1ex7ap1ro233smnm15-wpengine.netdna-ssl.com/wp-content/uploads/2015/04/google-analytics-ghost-referrals-150x150.jpg"),
            new SearchListData("Sola","b", "https://2uiipg1ex7ap1ro233smnm15-wpengine.netdna-ssl.com/wp-content/uploads/2015/04/google-analytics-ghost-referrals-150x150.jpg"),
            new SearchListData("Mola","b", "https://2uiipg1ex7ap1ro233smnm15-wpengine.netdna-ssl.com/wp-content/uploads/2015/04/google-analytics-ghost-referrals-150x150.jpg"),
            new SearchListData("Lola","b", "https://2uiipg1ex7ap1ro233smnm15-wpengine.netdna-ssl.com/wp-content/uploads/2015/04/google-analytics-ghost-referrals-150x150.jpg"),
            new SearchListData("Rola","b", "https://2uiipg1ex7ap1ro233smnm15-wpengine.netdna-ssl.com/wp-content/uploads/2015/04/google-analytics-ghost-referrals-150x150.jpg"),
            new SearchListData("Nola","b", "https://2uiipg1ex7ap1ro233smnm15-wpengine.netdna-ssl.com/wp-content/uploads/2015/04/google-analytics-ghost-referrals-150x150.jpg"),
            new SearchListData("Kola","b", "https://2uiipg1ex7ap1ro233smnm15-wpengine.netdna-ssl.com/wp-content/uploads/2015/04/google-analytics-ghost-referrals-150x150.jpg"),
            new SearchListData("Pola","b", "https://2uiipg1ex7ap1ro233smnm15-wpengine.netdna-ssl.com/wp-content/uploads/2015/04/google-analytics-ghost-referrals-150x150.jpg"),
            new SearchListData("Dola","b", "https://2uiipg1ex7ap1ro233smnm15-wpengine.netdna-ssl.com/wp-content/uploads/2015/04/google-analytics-ghost-referrals-150x150.jpg")
        };

        SearchListAdapter adapter = new SearchListAdapter(searchListData);
        resultsRv.setHasFixedSize(true);
        resultsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsRv.setAdapter(adapter);

        return root;
    }

    private void findViews() {
        searchBar = root.findViewById(R.id.search_bar);
        resultsRv = root.findViewById(R.id.rv_results);
    }

}
