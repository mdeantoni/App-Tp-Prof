package com.example.matias.tprof.detail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.data.QuotesContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NewsFragment extends Fragment {

    private ArrayAdapter<String> mNewsAdapter;

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

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "PASO ALGO","PASO OTRA COSA","MAS COSAS"
        };
        List<String> testData = new ArrayList<String>(Arrays.asList(data));

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mNewsAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_news, // The name of the layout ID.
                        R.id.headline_news, // The ID of the textview to populate.
                        testData);

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_news);
        listView.setAdapter(mNewsAdapter);

        return rootView;
    }

}
