package com.example.matias.tprof.detail;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.data.QuotesContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private NewsAdapter mNewsAdapter;
    private static final int NEWS_LOADER = 20;

    private static final String[] NEWS_COLUMNS = {
            QuotesContract.NewsEntry.TABLE_NAME + "." + QuotesContract.NewsEntry._ID,
            QuotesContract.NewsEntry.COLUMN_URL,
            QuotesContract.NewsEntry.COLUMN_TAGS,
            QuotesContract.NewsEntry.COLUMN_SOURCE,
            QuotesContract.NewsEntry.COLUMN_HEADLINE,
            QuotesContract.NewsEntry.COLUMN_DATE

    };

    static final int COL_ID = 0;
    static final int COL_URL = 1;
    static final int COL_TAGS = 2;
    static final int COL_SOURCE = 3;
    static final int COL_HEADLINE = 4;
    static final int COL_DATE = 5;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        mNewsAdapter = new NewsAdapter(getActivity(), null, 0);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_news);
        listView.setAdapter(mNewsAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String url = cursor.getString(COL_URL);
                    Intent webViewIntent = new Intent(getActivity(), WebViewActivity.class);
                    webViewIntent.putExtra(Intent.EXTRA_TEXT, url);
                    startActivity(webViewIntent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(NEWS_LOADER,  this.getArguments(), this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = QuotesContract.NewsEntry.COLUMN_DATE + " DESC";
        Uri newsURI = QuotesContract.NewsEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                newsURI,
                NEWS_COLUMNS,
                QuotesContract.NewsEntry.COLUMN_TAGS + " LIKE ?",
                new String[] { "%" + args.getString(StockDetailFragment.SYMBOL) + "%" },
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mNewsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNewsAdapter.swapCursor(null);
    }

}
