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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.StockQuotesFragment;
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
 * A placeholder fragment containing a simple view.
 */
public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "STOCK_DETAIL_URI";
    static final String STOCK_ID = "STOCK_ID";

    private static final int STOCK_DETAIL_LOADER = 3;
    private static final int STOCK_INTRADAY_LOADER = 5;

    private LineData intradayData;
    private Uri mUri;
    private int stockId;

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

    private static final String[] STOCK_INTRADAY_PRICE_COLUMNS = {
            QuotesContract.StockIntradayPriceEntry._ID,
            QuotesContract.StockIntradayPriceEntry.COLUMN_STOCK_ID,
            QuotesContract.StockIntradayPriceEntry.COLUMN_TRADE_TIME,
            QuotesContract.StockIntradayPriceEntry.COLUMN_TRADE_PRICE,
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

    static final int COL_INT_ID = 0;
    static final int COL_INT_STOCK_ID = 1;
    static final int COL_INT_TRADE_TIME = 2;
    static final int COL_INT_TRADE_PRICE = 3;

    public StockDetailFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(STOCK_DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(STOCK_INTRADAY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(StockDetailFragment.DETAIL_URI);
            stockId = arguments.getInt(StockDetailFragment.STOCK_ID);
        }

        View rootView = inflater.inflate(R.layout.fragment_stock_detail, container, false);

        Button buttonDay = (Button) rootView.findViewById(R.id.button_stock_detail_day);
        Button buttonWeek = (Button) rootView.findViewById(R.id.button_stock_detail_week);
        Button buttonMonth = (Button) rootView.findViewById(R.id.button_stock_detail_month);
        Button button6Month = (Button) rootView.findViewById(R.id.button_stock_detail_sixmonth);
        Button buttonYear = (Button) rootView.findViewById(R.id.button_stock_detail_year);

        final LineChart lineChart = (LineChart) rootView.findViewById(R.id.stock_detail_chart);
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


        buttonDay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                lineChart.setData(intradayData);
                lineChart.invalidate();
            }
        });

        buttonWeek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
            }
        });

        buttonMonth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                ArrayList<Entry> entries = new ArrayList<>();
                entries.add(new Entry(4f, 0));
                entries.add(new Entry(8f, 1));
                entries.add(new Entry(6f, 2));
                entries.add(new Entry(2f, 3));
                entries.add(new Entry(18f, 4));
                entries.add(new Entry(9f, 5));

                LineDataSet dataset = new LineDataSet(entries, "# of Calls");

                ArrayList<String> labels = new ArrayList<String>();
                labels.add("January");
                labels.add("February");
                labels.add("March");
                labels.add("April");
                labels.add("May");
                labels.add("June");

                LineData data = new LineData(labels, dataset);
                lineChart.setData(data); // set the data and list of lables into chart
                lineChart.invalidate();
            }
        });

        button6Month.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
            }
        });

        buttonYear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

       if(id == StockDetailFragment.STOCK_DETAIL_LOADER) {
           if (null != mUri) {
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
       }
        if(id == StockDetailFragment.STOCK_INTRADAY_LOADER){
            return new CursorLoader(
                    getActivity(),
                    QuotesContract.StockIntradayPriceEntry.CONTENT_URI,
                    STOCK_INTRADAY_PRICE_COLUMNS,
                    QuotesContract.StockIntradayPriceEntry.TABLE_NAME + "." +
                            QuotesContract.StockIntradayPriceEntry.COLUMN_STOCK_ID + "= ?",
                    new String[]{Integer.toString(stockId)},
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            switch (loader.getId()) {
                case STOCK_DETAIL_LOADER:
                    if (cursor != null && cursor.moveToFirst()) {
                        Date quoteDate = null;
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
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

                        if (cursor.getDouble(StockDetailFragment.COL_LAST_CHANGE) > 0) {
                            change.setTextColor(Color.GREEN);
                            changeText = "+" + changeText;
                        } else {
                            change.setTextColor(Color.RED);
                        }

                        toolbar.setTitle(cursor.getString(StockDetailFragment.COL_FULLNAME));
                        quote.setText(cursor.getString(StockDetailFragment.COL_SYMBOL));
                        change.setText(changeText);
                        price.setText(cursor.getString(StockDetailFragment.COL_CURRENCY) + cursor.getString(StockDetailFragment.COL_LAST_PRICE));
                        datetv.setText(outputFormat.format(quoteDate));
                    }
                    break;
                case STOCK_INTRADAY_LOADER:

                    LineChart lineChart = (LineChart) getView().findViewById(R.id.stock_detail_chart);
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
                    intradayData = new LineData(labels, dataset);
                    dataset.setDrawFilled(true);
                    dataset.setColor(Color.GRAY);
                    dataset.setFillAlpha(30);
                    dataset.setFillColor(Color.GRAY);
                    dataset.setDrawCircles(false);
                    dataset.setDrawHighlightIndicators(false);
                    intradayData.setDrawValues(false);
                    lineChart.setData(intradayData);
                    lineChart.invalidate();
                    break;

                default:
                    break;
            }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
