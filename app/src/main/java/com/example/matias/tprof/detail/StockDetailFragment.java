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
    static final String SYMBOL = "SYMBOL";

    private static final int STOCK_DETAIL_LOADER = 3;
    private static final int STOCK_INTRADAY_LOADER = 5;
    private static final int STOCK_HISTORIC_LOADER = 7;

    private LineData intradayData;
    private LineData historicalData;
    private Uri mUri;
    private int stockId;
    private String tickerSymbol;

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
            QuotesContract.StockQuotesEntry.COLUMN_DAYS_LOW,
            QuotesContract.StockQuotesEntry.COLUMN_DAYS_HIGH,
            QuotesContract.StockQuotesEntry.COLUMN_YEAR_LOW,
            QuotesContract.StockQuotesEntry.COLUMN_YEAR_HIGH,
            QuotesContract.StockQuotesEntry.COLUMN_PREVIOUS_CLOSE,
            QuotesContract.StockQuotesEntry.COLUMN_OPEN,
            QuotesContract.StockQuotesEntry.COLUMN_VOLUME,
            QuotesContract.StockQuotesEntry.COLUMN_AVG_VOLUME,
            QuotesContract.StockQuotesEntry.COLUMN_MKT_CAP,
            QuotesContract.StockQuotesEntry.COLUMN_PRICE_SALES,
            QuotesContract.StockQuotesEntry.COLUMN_PRICE_BOOK,
            QuotesContract.StockQuotesEntry.COLUMN_PRICE_EARNINGS
    };

    private static final String[] STOCK_INTRADAY_PRICE_COLUMNS = {
            QuotesContract.StockIntradayPriceEntry._ID,
            QuotesContract.StockIntradayPriceEntry.COLUMN_STOCK_ID,
            QuotesContract.StockIntradayPriceEntry.COLUMN_TRADE_TIME,
            QuotesContract.StockIntradayPriceEntry.COLUMN_TRADE_PRICE,
    };

    private static final String[] STOCK_HISTORICAL_QUOTES_COLUMNS = {
            QuotesContract.HistoricalQuoteEntry._ID,
            QuotesContract.HistoricalQuoteEntry.COLUMN_VOLUME,
            QuotesContract.HistoricalQuoteEntry.COLUMN_DATE,
            QuotesContract.HistoricalQuoteEntry.COLUMN_CLOSE_PRICE,
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
    static final int COL_DAYS_LOW =9;
    static final int COL_DAYS_HIGH = 10;
    static final int COL_YEAR_LOW= 11;
    static final int COL_YEAR_HIGH = 12;
    static final int COL_PREVIOUS_CLOSE = 13;
    static final int COL_OPEN = 14;
    static final int COL_VOLUME = 15;
    static final int COL_AVG_VOLUME = 16;
    static final int COL_MKT_CAP= 17;
    static final int COL_PRICE_SALES = 18;
    static final int COL_PRICE_BOOK = 19;
    static final int COL_PRICE_EARNINGS=  20;

    static final int COL_INT_ID = 0;
    static final int COL_INT_STOCK_ID = 1;
    static final int COL_INT_TRADE_TIME = 2;
    static final int COL_INT_TRADE_PRICE = 3;

    static final int HIST_COL_ID = 0;
    static final int HIST_COL_VOL = 1;
    static final int HIST_COL_DATE = 2;
    static final int HIST_COL_CLOSE_PRICE = 3;

    public StockDetailFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(STOCK_DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(STOCK_INTRADAY_LOADER, null, this);
        getLoaderManager().initLoader(STOCK_HISTORIC_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(StockDetailFragment.DETAIL_URI);
            stockId = arguments.getInt(StockDetailFragment.STOCK_ID);
            tickerSymbol = arguments.getString(StockDetailFragment.SYMBOL);
        }

        View rootView = inflater.inflate(R.layout.fragment_stock_detail, container, false);

        Button buttonDay = (Button) rootView.findViewById(R.id.button_stock_detail_day);
        Button buttonWeek = (Button) rootView.findViewById(R.id.button_stock_detail_week);
        Button buttonMonth = (Button) rootView.findViewById(R.id.button_stock_detail_month);
        Button button6Month = (Button) rootView.findViewById(R.id.button_stock_detail_sixmonth);
        Button buttonYear = (Button) rootView.findViewById(R.id.button_stock_detail_year);

        final LineChart lineChart = (LineChart) rootView.findViewById(R.id.stock_detail_chart);
        lineChart.getAxisRight().setEnabled(true);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisLeft().setStartAtZero(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getLegend().setEnabled(false);
        //lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.setDescription("");
        lineChart.setTouchEnabled(false);


        buttonDay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lineChart.setData(intradayData);
                lineChart.fitScreen();
                lineChart.invalidate();
            }
        });

        buttonWeek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lineChart.setData(historicalData);
                lineChart.setVisibleXRangeMaximum(5);
                lineChart.setVisibleXRangeMinimum(5);
                lineChart.moveViewToX(0);
                lineChart.invalidate();
            }
        });

        buttonMonth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                  lineChart.setData(historicalData);
                lineChart.setVisibleXRangeMaximum(20);
                lineChart.setVisibleXRangeMinimum(20);
                  lineChart.moveViewToX(0);
                  lineChart.invalidate();
            }
        });

        button6Month.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lineChart.setData(historicalData);
                lineChart.setVisibleXRangeMaximum(120);
                lineChart.setVisibleXRangeMinimum(120);
                lineChart.moveViewToX(0);
                lineChart.invalidate();
            }
        });

        buttonYear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lineChart.setData(historicalData);
                lineChart.fitScreen();
                lineChart.invalidate();
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
        if(id == StockDetailFragment.STOCK_HISTORIC_LOADER){
            return new CursorLoader(
                    getActivity(),
                    QuotesContract.HistoricalQuoteEntry.CONTENT_URI,
                    STOCK_HISTORICAL_QUOTES_COLUMNS,
                    QuotesContract.HistoricalQuoteEntry.TABLE_NAME + "." +
                            QuotesContract.HistoricalQuoteEntry.COLUMN_TICKER_SYMBOL + "= ?",
                    new String[]{this.tickerSymbol},
                    QuotesContract.HistoricalQuoteEntry.TABLE_NAME + "." +
                            QuotesContract.HistoricalQuoteEntry.COLUMN_DATE + " ASC"
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

                        TextView dayMinTv = (TextView) getView().findViewById(R.id.stock_detail_min_day);
                        TextView dayMaxTv = (TextView) getView().findViewById(R.id.stock_detail_max_day);
                        TextView yearMinTv = (TextView) getView().findViewById(R.id.stock_detail_low_year);
                        TextView yearMaxTv = (TextView) getView().findViewById(R.id.stock_detail_max_year);
                        TextView volumeTv = (TextView) getView().findViewById(R.id.stock_detail_volume);
                        TextView avgVolumeTv = (TextView) getView().findViewById(R.id.stock_detail_avg_volume);
                        TextView lastCloseTv = (TextView) getView().findViewById(R.id.stock_detail_last_close);
                        TextView openPriceTv = (TextView) getView().findViewById(R.id.stock_detail_open_price);
                        TextView changeTv = (TextView) getView().findViewById(R.id.stock_detail_change);
                        TextView changePercentTv = (TextView) getView().findViewById(R.id.stock_detail_change_percentage);
                        TextView marketCapTv = (TextView) getView().findViewById(R.id.stock_detail_market_cap);
                        TextView priceBookTv = (TextView) getView().findViewById(R.id.stock_detail_price_book);
                        TextView priceSalesTv = (TextView) getView().findViewById(R.id.stock_detail_price_sales);
                        TextView priceEarningsTv = (TextView) getView().findViewById(R.id.stock_detail_price_earnings);

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
                        dayMinTv.setText(cursor.getString(StockDetailFragment.COL_CURRENCY) + cursor.getString(StockDetailFragment.COL_DAYS_LOW));
                        dayMaxTv.setText(cursor.getString(StockDetailFragment.COL_CURRENCY) + cursor.getString(StockDetailFragment.COL_DAYS_HIGH));
                        yearMinTv.setText(cursor.getString(StockDetailFragment.COL_CURRENCY) + cursor.getString(StockDetailFragment.COL_YEAR_LOW));
                        yearMaxTv.setText(cursor.getString(StockDetailFragment.COL_CURRENCY) + cursor.getString(StockDetailFragment.COL_YEAR_HIGH));
                        volumeTv.setText( cursor.getString(StockDetailFragment.COL_VOLUME));
                        avgVolumeTv.setText( cursor.getString(StockDetailFragment.COL_AVG_VOLUME));
                        lastCloseTv.setText(cursor.getString(StockDetailFragment.COL_CURRENCY) + cursor.getString(StockDetailFragment.COL_PREVIOUS_CLOSE));
                        openPriceTv.setText(cursor.getString(StockDetailFragment.COL_CURRENCY) + cursor.getString(StockDetailFragment.COL_OPEN));
                        changeTv.setText(cursor.getString(StockDetailFragment.COL_CURRENCY) + cursor.getString(StockDetailFragment.COL_LAST_CHANGE));
                        changePercentTv.setText(cursor.getString(StockDetailFragment.COL_LAST_CHANGE_PERCENTAGE));
                        marketCapTv.setText(cursor.getString(StockDetailFragment.COL_CURRENCY) + cursor.getString(StockDetailFragment.COL_MKT_CAP));
                        priceBookTv.setText(cursor.getString(StockDetailFragment.COL_PRICE_BOOK));
                        priceSalesTv.setText(cursor.getString(StockDetailFragment.COL_PRICE_SALES));
                        priceEarningsTv.setText( cursor.getString(StockDetailFragment.COL_PRICE_EARNINGS));

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

                case STOCK_HISTORIC_LOADER:

                    ArrayList<Entry> historicalEntries = new ArrayList<>();
                    ArrayList<String> historicalLabels = new ArrayList<String>();
                    SimpleDateFormat inputFormatHistorical = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat outputFormatHistorical = new SimpleDateFormat("dd-MM");

                    while (cursor.moveToNext()) {
                        Date quoteDate = null;

                        Log.d("HistoricalQuote ", "Close Price " + cursor.getString(HIST_COL_CLOSE_PRICE) + " "
                                + "Date " + cursor.getString(HIST_COL_DATE));
                        try {
                            quoteDate = inputFormatHistorical.parse(cursor.getString(HIST_COL_DATE));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        historicalEntries.add(new Entry(cursor.getFloat(COL_INT_TRADE_PRICE), cursor.getPosition()));
                        historicalLabels.add(outputFormatHistorical.format(quoteDate));
                    }
                    LineDataSet historicalDataSet = new LineDataSet(historicalEntries, "Precio");
                    historicalData = new LineData(historicalLabels, historicalDataSet);
                    historicalDataSet.setDrawFilled(true);
                    historicalDataSet.setColor(Color.GRAY);
                    historicalDataSet.setFillAlpha(30);
                    historicalDataSet.setFillColor(Color.GRAY);
                    historicalDataSet.setDrawCircles(false);
                    historicalDataSet.setDrawHighlightIndicators(false);
                    historicalData.setDrawValues(false);
                    break;

                default:
                    break;
            }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
