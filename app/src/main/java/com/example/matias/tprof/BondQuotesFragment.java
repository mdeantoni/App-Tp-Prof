package com.example.matias.tprof;


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

import com.example.matias.tprof.data.QuotesContract;


public class BondQuotesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BONDS_LOADER = 1;

    private static final String[] BOND_QUOTE_COLUMNS = {
            QuotesContract.BondEntry._ID,
            QuotesContract.BondEntry.COLUMN_FULLNAME,
            QuotesContract.BondEntry.COLUMN_SYMBOL,
    };

    static final int COL_BOND_QUOTE_ID = 0;
    static final int COL_FULLNAME = 1;
    static final int COL_SYMBOL = 2;

    private BondQuotesAdapter mQuotesAdapter;

    public BondQuotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String sortOrder = QuotesContract.BondEntry.COLUMN_FULLNAME + " ASC";
        Uri bondsUri = QuotesContract.BondEntry.CONTENT_URI;

        Cursor cur = getActivity().getContentResolver().query(bondsUri,
                null, null, null, sortOrder);

        mQuotesAdapter = new BondQuotesAdapter(getActivity(), cur, 0);

        View rootView = inflater.inflate(R.layout.fragment_bond_quotes, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_bond_quotes);
        listView.setAdapter(mQuotesAdapter);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BONDS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateQuotes();
    }

    private void updateQuotes() {
        FetchBondDataTask fetchTestDataTask = new FetchBondDataTask(getContext());
        fetchTestDataTask.execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = QuotesContract.BondEntry.COLUMN_FULLNAME + " ASC";
        Uri stocksUri = QuotesContract.BondEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                stocksUri,
                BOND_QUOTE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mQuotesAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mQuotesAdapter.swapCursor(null);
    }

}
