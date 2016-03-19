package com.example.matias.tprof.detail;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.StockQuotesFragment;
import com.example.matias.tprof.data.QuotesContract;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "STOCK_DETAIL_URI";

    private static final int STOCK_DETAIL_LOADER = 3;

    private Uri mUri;

    // This has to change when details table and columns are implemented.

    private static final String[] STOCK_QUOTE_COLUMNS = {
            QuotesContract.StockEntry.TABLE_NAME + "." + QuotesContract.StockEntry._ID,
            QuotesContract.StockEntry.COLUMN_FULLNAME,
            QuotesContract.StockEntry.COLUMN_SYMBOL,
            QuotesContract.StockQuotesEntry.TABLE_NAME + "." + QuotesContract.StockQuotesEntry._ID,
            QuotesContract.StockQuotesEntry.COLUMN_LAST_TRADE_PRICE,
            QuotesContract.StockQuotesEntry.COLUMN_LAST_TRADE_DATE,
            QuotesContract.StockQuotesEntry.COLUMN_LAST_CHANGE,
            QuotesContract.StockQuotesEntry.COLUMN_LAST_CHANGE_PERCENTAGE,
            QuotesContract.StockQuotesEntry.COLUMN_CURRENCY,
    };

    static final int COL_STOCK_ID = 0;
    static final int COL_FULLNAME = 1;
    static final int COL_SYMBOL = 2;
    static final int COL_STOCK_QUOTE_ID = 3;
    static final int COL_LAST_PRICE = 4;
    static final int COL_LAST_TRADE_DATE = 5;
    static final int COL_LAST_CHANGE = 6;
    static final int COL_LAST_CHANGE_PERCENTAGE = 7;
    static final int COL_CURRENCY = 8;

    public StockDetailFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(STOCK_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(StockDetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_stock_detail, container, false);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    STOCK_QUOTE_COLUMNS,
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
            String date = cursor.getString(StockDetailFragment.COL_LAST_TRADE_DATE);
            String changeText = cursor.getString(StockDetailFragment.COL_LAST_CHANGE) + " "
                    + "(" + cursor.getString(StockDetailFragment.COL_LAST_CHANGE_PERCENTAGE) + "%)";

            try {
                quoteDate = inputFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            TextView quote = (TextView) getView().findViewById(R.id.stock_detail_ticker);
            TextView change = (TextView) getView().findViewById(R.id.stock_detail_lastchange);
            TextView price = (TextView) getView().findViewById(R.id.stock_detail_price);
            TextView datetv = (TextView) getView().findViewById(R.id.stock_detail_datetime);

            if(cursor.getDouble(StockDetailFragment.COL_LAST_CHANGE) > 0){
                change.setTextColor(Color.GREEN);
                changeText = "+" + changeText;
            }else{
                change.setTextColor(Color.RED);
            }

            toolbar.setTitle(cursor.getString(StockDetailFragment.COL_FULLNAME));
            quote.setText(cursor.getString(StockDetailFragment.COL_SYMBOL));
            change.setText(changeText);
            price.setText(cursor.getString(StockDetailFragment.COL_CURRENCY) + cursor.getString(StockDetailFragment.COL_LAST_PRICE));
            datetv.setText(outputFormat.format(quoteDate));


            //Charting
            LineChart lineChart = (LineChart) getView().findViewById(R.id.stock_detail_chart);
            // creating list of entry
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(4f, 0));
            entries.add(new Entry(8f, 1));
            entries.add(new Entry(6f, 2));
            entries.add(new Entry(2f, 3));
            entries.add(new Entry(18f, 4));
            entries.add(new Entry(9f, 5));
            entries.add(new Entry(9f, 15));
            LineDataSet dataset = new LineDataSet(entries, "# of Calls");
            ArrayList<String> labels = new ArrayList<String>();
            labels.add("January");
            labels.add("January");
            labels.add("January");
            labels.add("February");
            labels.add("February");
            labels.add("February");
            labels.add("March");
            labels.add("March");
            labels.add("March");
            labels.add("April");
            labels.add("April");
            labels.add("April");
            labels.add("May");
            labels.add("May");
            labels.add("May");
            labels.add("June");
            labels.add("June");
            labels.add("June");
            LineData data = new LineData(labels, dataset);
            lineChart.getXAxis().setLabelsToSkip(1);

            lineChart.setData(data); // set the data and list of lables into chart


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
