package com.example.matias.tprof.detail;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    public final String LOG_TAG = BondDetailFragment.class.getSimpleName();

    static final String DETAIL_URI = "BOND_DETAIL_URI";
    static final String BOND_ID = "BOND_ID";
    static final String SYMBOL = "SYMBOL";

    private static final int BOND_DETAIL_LOADER = 4;
    private static final int BOND_INTRADAY_LOADER = 6;
    private static final int BOND_HOLDINGS_LOADER = 9;

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
            QuotesContract.BondQuotesEntry.COLUMN_DAYS_LOW,
            QuotesContract.BondQuotesEntry.COLUMN_DAYS_HIGH,
            QuotesContract.BondQuotesEntry.COLUMN_YEAR_LOW,
            QuotesContract.BondQuotesEntry.COLUMN_YEAR_HIGH,
            QuotesContract.BondQuotesEntry.COLUMN_PREVIOUS_CLOSE,
            QuotesContract.BondQuotesEntry.COLUMN_OPEN,
            QuotesContract.BondQuotesEntry.COLUMN_VOLUME,
            QuotesContract.BondQuotesEntry.COLUMN_AVG_VOLUME,
            QuotesContract.BondQuotesEntry.COLUMN_IIR,
            QuotesContract.BondQuotesEntry.COLUMN_PARITY
    };

    private static final String[] BOND_INTRADAY_PRICE_COLUMNS = {
            QuotesContract.BondIntradayPriceEntry._ID,
            QuotesContract.BondIntradayPriceEntry.COLUMN_BOND_ID,
            QuotesContract.BondIntradayPriceEntry.COLUMN_TRADE_TIME,
            QuotesContract.BondIntradayPriceEntry.COLUMN_TRADE_PRICE,
    };

    private static final String[] BOND_HOLDINGS_COLUMNS = {
            QuotesContract.HoldingsViewEntry.COLUMN_SYMBOL,
            QuotesContract.HoldingsViewEntry.COLUMN_QUANTITY,
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
    static final int COL_DAYS_LOW =9;
    static final int COL_DAYS_HIGH = 10;
    static final int COL_YEAR_LOW= 11;
    static final int COL_YEAR_HIGH = 12;
    static final int COL_PREVIOUS_CLOSE = 13;
    static final int COL_OPEN = 14;
    static final int COL_VOLUME = 15;
    static final int COL_AVG_VOLUME = 16;
    static final int COL_IIR = 17;
    static final int COL_PARITY = 18;

    static final int COL_INT_ID = 0;
    static final int COL_INT_BOND_ID = 1;
    static final int COL_INT_TRADE_TIME = 2;
    static final int COL_INT_TRADE_PRICE = 3;

    static final int HOLD_COL_SYMBOL = 0;
    static final int HOLDING_COL_QTY = 1;

    private Uri mUri;
    private int bondId;
    private String tickerSymbol;
    private int m_Bonds = 0;
    private Button buttonSell;
    private int mHolding;


    private final String TRADE_TYPE = "Bono";
    private final String BUY_TRADE_TYPE = "Compra";
    private final String SELL_TRADE_TYPE = "Venta";
    private String LastTradeDate;
    private Double LastTradePRice;


    public BondDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BOND_DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(BOND_INTRADAY_LOADER, null, this);
        getLoaderManager().initLoader(BOND_HOLDINGS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(BondDetailFragment.DETAIL_URI);
            bondId = arguments.getInt(BondDetailFragment.BOND_ID);
            tickerSymbol = arguments.getString(BondDetailFragment.SYMBOL);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bond_detail, container, false);

        Button buttonBuy = (Button) rootView.findViewById(R.id.button_buy_bond);
        buttonSell = (Button) rootView.findViewById(R.id.button_sell_bond);

        LineChart lineChart = (LineChart) rootView.findViewById(R.id.bond_detail_chart);
        lineChart.getAxisRight().setEnabled(true);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisLeft().setStartAtZero(false);
        lineChart.getAxisRight().setStartAtZero(false);
        lineChart.getAxisLeft().setStartAtZero(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getLegend().setEnabled(false);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.setDescription("");
        lineChart.setTouchEnabled(false);

        buttonBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Compra de Bonos:");
                builder.setMessage("Cantidad a adquirir:");

                final EditText input = new EditText(getActivity());

                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setPositiveButton("COMPRAR", null);
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                m_Bonds = Integer.parseInt(input.getText().toString());
                                if (m_Bonds == 0) {
                                    input.setError("La cantidad debe ser mayor a cero.");
                                } else {
                                    ContentValues newsQuoteValue = new ContentValues();
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_DATE, LastTradeDate);
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_OPERATION, BUY_TRADE_TYPE);
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_PRICE, LastTradePRice);
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_QUANTITY, m_Bonds);
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_SYMBOL, tickerSymbol);
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_TYPE, TRADE_TYPE);

                                    getContext().getContentResolver().insert(QuotesContract.TradesEntry.CONTENT_URI, newsQuoteValue);
                                    Log.d(LOG_TAG, "Bond buy inserted succesfully " + newsQuoteValue.toString());

                                    Toast toast = Toast.makeText(getActivity(), "Operación Realizada", Toast.LENGTH_SHORT);
                                    toast.show();
                                    dialog.dismiss();
                                    getLoaderManager().restartLoader(BOND_HOLDINGS_LOADER, null, BondDetailFragment.this);
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        buttonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Venta de Bonos:");
                builder.setMessage("Cantidad a vender:");

                final EditText input = new EditText(getActivity());

                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setPositiveButton("VENDER", null);
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                m_Bonds = Integer.parseInt(input.getText().toString());
                                if (m_Bonds == 0) {
                                    input.setError("La cantidad debe ser mayor a cero.");
                                } else if (m_Bonds > mHolding) {
                                    input.setError("La cantidad debe ser menor a la tenencia actual.");
                                } else {
                                    ContentValues newsQuoteValue = new ContentValues();
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_DATE, LastTradeDate);
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_OPERATION, SELL_TRADE_TYPE);
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_PRICE, LastTradePRice);
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_QUANTITY, m_Bonds);
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_SYMBOL, tickerSymbol);
                                    newsQuoteValue.put(QuotesContract.TradesEntry.COLUMN_TYPE, TRADE_TYPE);

                                    getContext().getContentResolver().insert(QuotesContract.TradesEntry.CONTENT_URI, newsQuoteValue);
                                    Log.d(LOG_TAG, "Bond sell inserted succesfully " + newsQuoteValue.toString());

                                    Toast toast = Toast.makeText(getActivity(), "Operación Realizada", Toast.LENGTH_SHORT);
                                    toast.show();
                                    dialog.dismiss();
                                    getLoaderManager().restartLoader(BOND_HOLDINGS_LOADER, null, BondDetailFragment.this);
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

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

        if (id == BondDetailFragment.BOND_HOLDINGS_LOADER) {
            return new CursorLoader(
                    getActivity(),
                    QuotesContract.HoldingsViewEntry.CONTENT_URI,
                    BOND_HOLDINGS_COLUMNS,
                    QuotesContract.HoldingsViewEntry.COLUMN_SYMBOL + " = ?",
                    new String[]{this.tickerSymbol},
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
                    TextView dayMinTv = (TextView) getView().findViewById(R.id.bond_detail_min_day);
                    TextView dayMaxTv = (TextView) getView().findViewById(R.id.bond_detail_max_day);
                    TextView yearMinTv = (TextView) getView().findViewById(R.id.bond_detail_low_year);
                    TextView yearMaxTv = (TextView) getView().findViewById(R.id.bond_detail_max_year);
                    TextView volumeTv = (TextView) getView().findViewById(R.id.bond_detail_volume);
                    TextView avgVolumeTv = (TextView) getView().findViewById(R.id.bond_detail_avg_volume);
                    TextView lastCloseTv = (TextView) getView().findViewById(R.id.bond_detail_last_close);
                    TextView openPriceTv = (TextView) getView().findViewById(R.id.bond_detail_open_price);
                    TextView changeTv = (TextView) getView().findViewById(R.id.bond_detail_change);
                    TextView changePercentTv = (TextView) getView().findViewById(R.id.bond_detail_change_percentage);
                    TextView bondIIRTv = (TextView) getView().findViewById(R.id.bond_detail_iir);
                    TextView bondParityTv = (TextView) getView().findViewById(R.id.bond_detail_parity);

                    if (cursor.getDouble(BondDetailFragment.COL_LAST_CHANGE) > 0) {
                        change.setTextColor(Color.GREEN);
                        changeText = "+" + changeText;
                    } else {
                        change.setTextColor(Color.RED);
                    }

                    LastTradeDate = cursor.getString(BondDetailFragment.COL_LAST_TRADE_DATE);
                    LastTradePRice = cursor.getDouble(BondDetailFragment.COL_LAST_PRICE);

                    toolbar.setTitle(cursor.getString(BondDetailFragment.COL_FULLNAME));
                    quote.setText(cursor.getString(BondDetailFragment.COL_SYMBOL));
                    change.setText(changeText);
                    price.setText(cursor.getString(BondDetailFragment.COL_CURRENCY) + cursor.getString(BondDetailFragment.COL_LAST_PRICE));
                    datetv.setText(outputFormat.format(quoteDate));
                    dayMinTv.setText(cursor.getString(BondDetailFragment.COL_CURRENCY) + cursor.getString(BondDetailFragment.COL_DAYS_LOW));
                    dayMaxTv.setText(cursor.getString(BondDetailFragment.COL_CURRENCY) + cursor.getString(BondDetailFragment.COL_DAYS_HIGH));
                    yearMinTv.setText(cursor.getString(BondDetailFragment.COL_CURRENCY) + cursor.getString(BondDetailFragment.COL_YEAR_LOW));
                    yearMaxTv.setText(cursor.getString(BondDetailFragment.COL_CURRENCY) + cursor.getString(BondDetailFragment.COL_YEAR_HIGH));
                    volumeTv.setText( cursor.getString(BondDetailFragment.COL_VOLUME));
                    avgVolumeTv.setText(cursor.getString(BondDetailFragment.COL_AVG_VOLUME));
                    lastCloseTv.setText(cursor.getString(BondDetailFragment.COL_CURRENCY) + cursor.getString(BondDetailFragment.COL_PREVIOUS_CLOSE));
                    openPriceTv.setText(cursor.getString(BondDetailFragment.COL_CURRENCY) + cursor.getString(BondDetailFragment.COL_OPEN));
                    changeTv.setText(cursor.getString(BondDetailFragment.COL_CURRENCY) + cursor.getString(BondDetailFragment.COL_LAST_CHANGE));
                    bondIIRTv.setText( cursor.getString(BondDetailFragment.COL_IIR));
                    bondParityTv.setText(cursor.getString(BondDetailFragment.COL_PARITY));
                    changePercentTv.setText(cursor.getString(BondDetailFragment.COL_LAST_CHANGE_PERCENTAGE));
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
                dataset.setDrawCubic(true);
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

            case BOND_HOLDINGS_LOADER:
                if (cursor != null && cursor.moveToFirst()) {
                    buttonSell.setEnabled(true);
                    mHolding = cursor.getInt(BondDetailFragment.HOLDING_COL_QTY);
                }else{
                    buttonSell.setEnabled(false);
                    mHolding = 0;
                }

            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

}
