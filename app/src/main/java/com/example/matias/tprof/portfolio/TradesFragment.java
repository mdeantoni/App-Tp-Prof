package com.example.matias.tprof.portfolio;


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
import android.widget.ListView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.data.QuotesContract;
import com.example.matias.tprof.detail.StockDetailFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class TradesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private TradesAdapter mTradesAdapter;
    private static final int TRADES_LOADER = 40;

    private static final String[] TRADES_COLUMNS = {
            QuotesContract.TradesEntry.TABLE_NAME + "." + QuotesContract.TradesEntry._ID,
            QuotesContract.TradesEntry.COLUMN_TYPE,
            QuotesContract.TradesEntry.COLUMN_OPERATION,
            QuotesContract.TradesEntry.COLUMN_DATE,
            QuotesContract.TradesEntry.COLUMN_SYMBOL,
            QuotesContract.TradesEntry.COLUMN_QUANTITY,
            QuotesContract.TradesEntry.COLUMN_PRICE
    };

    static final int COL_ID = 0;
    static final int COL_TYPE = 1;
    static final int COL_OPERATION = 2;
    static final int COL_DATE = 3;
    static final int COL_SYMBOL = 4;
    static final int COL_QUANTITY = 5;
    static final int COL_PRICE = 6;


    public TradesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_trades, container, false);
        mTradesAdapter = new TradesAdapter(getActivity(), null, 0);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_trades);
        listView.setAdapter(mTradesAdapter);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TRADES_LOADER,  null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = QuotesContract.TradesEntry.COLUMN_DATE + " DESC";
        Uri tradesUri = QuotesContract.TradesEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                tradesUri,
                TRADES_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mTradesAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTradesAdapter.swapCursor(null);
    }
}
