package com.ash.bookworm.Utilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.VolleyLog.TAG;

public class BooksUtil {
    private static String key = "AIzaSyDmfcF65dp6RZGVluwTaPiVR2t2NcR9u-E";

    public static void searchBooks(final Context context, final RecyclerView resultsRv, String searchTerm) {
        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        String url = String.format("https://www.googleapis.com/books/v1/volumes?q=%s&key=%s", searchTerm, key);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray books = response.getJSONArray("items");
                            SearchListData[] searchListData = new SearchListData[books.length()];

                            for (int i = 0; i < books.length(); i++) {
                                JSONObject book = books.getJSONObject(i);

                                String bookName = book.getJSONObject("volumeInfo")
                                        .getString("title");

                                String authorName = book.getJSONObject("volumeInfo")
                                        .getJSONArray("authors")
                                        .getString(0);

                                String imageUrl = book.getJSONObject("volumeInfo")
                                        .getJSONObject("imageLinks")
                                        .getString("thumbnail");

                                searchListData[i] = new SearchListData(bookName, authorName, imageUrl);
                            }
                            SearchListAdapter adapter = new SearchListAdapter(searchListData);
                            resultsRv.setHasFixedSize(true);
                            resultsRv.setLayoutManager(new LinearLayoutManager(context));
                            resultsRv.setAdapter(adapter);

                        } catch (JSONException e) {
                            //e.printStackTrace();
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
