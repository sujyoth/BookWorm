package com.ash.bookworm.helpers.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ash.bookworm.helpers.list_adapters.SearchListAdapter;
import com.ash.bookworm.helpers.models.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class BooksUtil {
    private static String key = "AIzaSyDmfcF65dp6RZGVluwTaPiVR2t2NcR9u-E";

    public static void searchBooks(final Context context, final SearchListAdapter adapter, String searchTerm) {
        RequestQueue requestQueue;
        final List<Book> newBooks = new ArrayList<>();

        // Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024 * 10); // 10MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        String url = String.format("https://www.googleapis.com/books/v1/volumes?q=%s&key=%s&prettyPrint=true", searchTerm, key);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("items");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject book = data.getJSONObject(i);
                                try {
                                    String bookId = book.getJSONObject("volumeInfo")
                                            .getJSONArray("industryIdentifiers")
                                            .getJSONObject(0).getString("identifier");

                                    String bookName = book.getJSONObject("volumeInfo")
                                            .getString("title");

                                    String authorName = book.getJSONObject("volumeInfo")
                                            .getJSONArray("authors")
                                            .getString(0);

                                    String imageUrl = book.getJSONObject("volumeInfo")
                                            .getJSONObject("imageLinks")
                                            .getString("thumbnail");

                                    newBooks.add(new Book(bookId, bookName, authorName, imageUrl));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            adapter.updateList(newBooks);

                        } catch (JSONException e) {
                            Log.d(TAG, response.toString());
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
