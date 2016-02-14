package com.example.matias.tprof.detail;


import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.data.QuotesContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class BondDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "BOND_DETAIL_URI";

    private static final int BOND_DETAIL_LOADER = 4;

    private static final String[] BOND_QUOTE_COLUMNS = {
            QuotesContract.BondEntry.TABLE_NAME + "." + QuotesContract.BondEntry._ID,
            QuotesContract.BondEntry.COLUMN_FULLNAME,
            QuotesContract.BondEntry.COLUMN_SYMBOL,
            QuotesContract.BondQuotesEntry.TABLE_NAME + "." + QuotesContract.BondQuotesEntry._ID,
            QuotesContract.BondQuotesEntry.COLUMN_LAST_TRADE_PRICE,
            QuotesContract.BondQuotesEntry.COLUMN_LAST_TRADE_DATE,
            QuotesContract.BondQuotesEntry.COLUMN_LAST_CHANGE,
            QuotesContract.BondQuotesEntry.COLUMN_LAST_CHANGE_PERCENTAGE,
            QuotesContract.BondQuotesEntry.COLUMN_CURRENCY,
    };

    static final int COL_BOND_ID = 0;
    static final int COL_FULLNAME = 1;
    static final int COL_SYMBOL = 2;
    static final int COL_BOND_QUOTE_ID = 3;
    static final int COL_LAST_PRICE = 4;
    static final int COL_LAST_TRADE_DATE = 5;
    static final int COL_LAST_CHANGE = 6;
    static final int COL_LAST_CHANGE_PERCENTAGE = 7;
    static final int COL_CURRENCY = 8;

    private Uri mUri;

    public BondDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BOND_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(BondDetailFragment.DETAIL_URI);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bond_detail, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    BOND_QUOTE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {

            Date quoteDate = null;
            SimpleDateFormat inputFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM");
            String date = cursor.getString(BondDetailFragment.COL_LAST_TRADE_DATE);
            String changeText = cursor.getString(BondDetailFragment.COL_LAST_CHANGE) + " "
                    + "(" + cursor.getString(BondDetailFragment.COL_LAST_CHANGE_PERCENTAGE) + "%)";

            try {
                quoteDate = inputFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            TextView quote = (TextView) getView().findViewById(R.id.bond_detail_ticker);
            TextView change = (TextView) getView().findViewById(R.id.bond_detail_lastchange);
            TextView price = (TextView) getView().findViewById(R.id.bond_detail_price);
            TextView datetv = (TextView) getView().findViewById(R.id.bond_detail_datetime);

            if(cursor.getDouble(BondDetailFragment.COL_LAST_CHANGE) > 0){
                change.setTextColor(Color.GREEN);
                changeText = "+" + changeText;
            }else{
                change.setTextColor(Color.RED);
            }

            toolbar.setTitle(cursor.getString(BondDetailFragment.COL_FULLNAME));
            quote.setText(cursor.getString(BondDetailFragment.COL_SYMBOL));
            change.setText(changeText);
            price.setText(cursor.getString(BondDetailFragment.COL_CURRENCY) + cursor.getString(BondDetailFragment.COL_LAST_PRICE));
            datetv.setText(outputFormat.format(quoteDate));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

}
