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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.data.QuotesContract;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class BondDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "BOND_DETAIL_URI";
    static final String BOND_ID = "BOND_ID";

    private static final int BOND_DETAIL_LOADER = 4;
    private static final int BOND_INTRADAY_LOADER = 6;

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

    private static final String[] BOND_INTRADAY_PRICE_COLUMNS = {
            QuotesContract.BondIntradayPriceEntry._ID,
            QuotesContract.BondIntradayPriceEntry.COLUMN_BOND_ID,
            QuotesContract.BondIntradayPriceEntry.COLUMN_TRADE_TIME,
            QuotesContract.BondIntradayPriceEntry.COLUMN_TRADE_PRICE,
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

    static final int COL_INT_ID = 0;
    static final int COL_INT_BOND_ID = 1;
    static final int COL_INT_TRADE_TIME = 2;
    static final int COL_INT_TRADE_PRICE = 3;

    private Uri mUri;
    private int bondId;


    public BondDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BOND_DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(BOND_INTRADAY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(BondDetailFragment.DETAIL_URI);
            bondId = arguments.getInt(BondDetailFragment.BOND_ID);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bond_detail, container, false);

        LineChart lineChart = (LineChart) rootView.findViewById(R.id.bond_detail_chart);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisLeft().setStartAtZero(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getLegend().setEnabled(false);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.setDescription("");
        lineChart.setTouchEnabled(false);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == BondDetailFragment.BOND_DETAIL_LOADER) {
            if (null != mUri) {
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
        }
        if(id == BondDetailFragment.BOND_INTRADAY_LOADER){
            return new CursorLoader(
                    getActivity(),
                    QuotesContract.BondIntradayPriceEntry.CONTENT_URI,
                    BOND_INTRADAY_PRICE_COLUMNS,
                    QuotesContract.BondIntradayPriceEntry.TABLE_NAME + "." +
                            QuotesContract.BondIntradayPriceEntry.COLUMN_BOND_ID + "= ?",
                    new String[]{Integer.toString(bondId)},
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case BOND_DETAIL_LOADER:

                if (cursor != null && cursor.moveToFirst()) {

                    Date quoteDate = null;
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
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

                    if (cursor.getDouble(BondDetailFragment.COL_LAST_CHANGE) > 0) {
                        change.setTextColor(Color.GREEN);
                        changeText = "+" + changeText;
                    } else {
                        change.setTextColor(Color.RED);
                    }

                    toolbar.setTitle(cursor.getString(BondDetailFragment.COL_FULLNAME));
                    quote.setText(cursor.getString(BondDetailFragment.COL_SYMBOL));
                    change.setText(changeText);
                    price.setText(cursor.getString(BondDetailFragment.COL_CURRENCY) + cursor.getString(BondDetailFragment.COL_LAST_PRICE));
                    datetv.setText(outputFormat.format(quoteDate));
                }
                break;

            case BOND_INTRADAY_LOADER:

                LineChart lineChart = (LineChart) getView().findViewById(R.id.bond_detail_chart);
                ArrayList<Entry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<String>();

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");

                while (cursor.moveToNext()) {
                    Date quoteDate = null;

                    Log.d("INTRADAY PRICE", "Price " + cursor.getString(COL_INT_TRADE_PRICE) + " "
                            + "Datetime " + cursor.getString(COL_INT_TRADE_TIME));
                    try {
                        quoteDate = inputFormat.parse(cursor.getString(COL_INT_TRADE_TIME));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    entries.add(new Entry(cursor.getFloat(COL_INT_TRADE_PRICE), cursor.getPosition()));
                    labels.add(outputFormat.format(quoteDate));
                }
                LineDataSet dataset = new LineDataSet(entries, "Precio");
                LineData data = new LineData(labels, dataset);
                dataset.setDrawFilled(true);
                dataset.setColor(Color.GRAY);
                dataset.setFillAlpha(30);
                dataset.setFillColor(Color.GRAY);
                dataset.setDrawCircles(false);
                dataset.setDrawHighlightIndicators(false);
                data.setDrawValues(false);
                lineChart.setData(data);
                lineChart.invalidate();
                break;

            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

}
