package com.example.matias.tprof.portfolio;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.data.QuotesContract;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PortfolioFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>  {

    private static final int VALUED_HOLDINGS_LOADER = 20;
    private float totalHoldings;

    private static final String[] VALUED_HOLDINGS_COLUMNS = {
            QuotesContract.ValuedHoldingsViewEntry.COLUMN_SYMBOL,
            QuotesContract.ValuedHoldingsViewEntry.COLUMN_QUANTITY,
            QuotesContract.ValuedHoldingsViewEntry.COLUMN_CURRENT_PRICE,
            QuotesContract.ValuedHoldingsViewEntry.COLUMN_TOTAL_VALUE,
    };

    static final int V_HOLD_COL_SYMBOL = 0;
    static final int V_HOLDING_COL_QTY = 1;
    static final int V_HOLD_COL_CURRENT_PRICE = 2;
    static final int V_HOLDING_TOTAL_VALUE = 3;

    public PortfolioFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(VALUED_HOLDINGS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_portfolio, container, false);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                QuotesContract.ValuedHoldingsViewEntry.CONTENT_URI,
                VALUED_HOLDINGS_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        PieChart pieChart = (PieChart) getView().findViewById(R.id.portfolio_chart);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        while (cursor.moveToNext()) {
            float currentValue = cursor.getFloat(V_HOLDING_TOTAL_VALUE);
            totalHoldings += currentValue;
            entries.add(new Entry(currentValue, cursor.getPosition()));
            labels.add(cursor.getString(V_HOLD_COL_SYMBOL));
        }

        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(ColorTemplate.PASTEL_COLORS);
        PieData data = new PieData(labels, dataset); // initialize Piedata
        data.setValueTextSize(10f);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(20f);
        pieChart.setData(data); //set data into chart
        pieChart.setDescription(" ");  // set the description
        pieChart.setTouchEnabled(false);

        TextView totalHoldingsTV = (TextView) getView().findViewById(R.id.portfolio_total);
        totalHoldingsTV.setText("$ "+ String.format("%.2f", totalHoldings));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

class DecimalFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public DecimalFormatter() {
        mFormat = new DecimalFormat("#.00"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return "$" + mFormat.format(value); // e.g. append a dollar-sign
    }
}
