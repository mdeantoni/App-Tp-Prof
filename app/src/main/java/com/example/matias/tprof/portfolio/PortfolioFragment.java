package com.example.matias.tprof.portfolio;


import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.data.QuotesContract;
import com.example.matias.tprof.numbers.NumberFormat;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
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

    public static final int[] MATERIAL_COLORS = {
            Color.rgb(244, 67, 54), //Red
            Color.rgb(63, 81, 181), //Indigo
            Color.rgb(156, 39, 176), //Purple
            Color.rgb(3, 169, 244), //Light Blue
            Color.rgb(0, 150, 136), //Teal
            Color.rgb(139, 195, 74), //Light Green
            Color.rgb(255, 235, 59), //Yellow
            Color.rgb(255, 152, 0), //Orange
            Color.rgb(121, 85, 72), //Brown
            Color.rgb(96, 125, 139) //Blue Grey
    };

    public final String LOG_TAG = PortfolioFragment.class.getSimpleName();


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
        Log.d(LOG_TAG, "Portfolio Loader created.");

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
        Log.d(LOG_TAG, "Portfolio Loader load finished.");

        PieChart pieChart = (PieChart) getView().findViewById(R.id.portfolio_chart);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<TableRow> detailRows = new ArrayList<TableRow>();

        while (cursor.moveToNext()) {
            float currentValue = cursor.getFloat(V_HOLDING_TOTAL_VALUE);
            totalHoldings += currentValue;
            entries.add(new Entry(currentValue, cursor.getPosition()));
            labels.add(cursor.getString(V_HOLD_COL_SYMBOL));


            TableRow newRow = new TableRow(getActivity());
            TextView symbolTv = new TextView(getActivity());
            TextView holdingTv =  new TextView(getActivity());
            TextView lastPriceTv =  new TextView(getActivity());
            TextView totalValuedTv =  new TextView(getActivity());

            symbolTv.setText(cursor.getString(V_HOLD_COL_SYMBOL));
            holdingTv.setText(cursor.getString(V_HOLDING_COL_QTY));
            lastPriceTv.setText(NumberFormat.formattedValue("$", cursor.getString(V_HOLD_COL_CURRENT_PRICE)));
            totalValuedTv.setText(NumberFormat.formattedValue("$", cursor.getString(V_HOLDING_TOTAL_VALUE)));
            //lastPriceTv.setText("$" + String.format("%.2f", cursor.getFloat(V_HOLD_COL_CURRENT_PRICE)) );
            //totalValuedTv.setText("$" + String.format("%.2f", cursor.getFloat(V_HOLDING_TOTAL_VALUE)));
            TableLayout.LayoutParams tableRowParams =
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
            tableRowParams.setMargins(0,2,0,0);
            newRow.setLayoutParams(tableRowParams);

            symbolTv.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            symbolTv.setGravity(Gravity.LEFT);
            symbolTv.setTextAppearance(getActivity(), android.R.style.TextAppearance_Material_Medium);
            holdingTv.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            holdingTv.setGravity(Gravity.RIGHT);
            holdingTv.setTextAppearance(getActivity(), android.R.style.TextAppearance_Material_Medium);
            lastPriceTv.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 2f));
            lastPriceTv.setGravity(Gravity.RIGHT);
            lastPriceTv.setTextAppearance(getActivity(), android.R.style.TextAppearance_Material_Medium);
            totalValuedTv.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 3f));
            totalValuedTv.setGravity(Gravity.RIGHT);
            totalValuedTv.setTextAppearance(getActivity(), android.R.style.TextAppearance_Material_Medium);

            newRow.addView(symbolTv);
            newRow.addView(holdingTv);
            newRow.addView(lastPriceTv);
            newRow.addView(totalValuedTv);

            detailRows.add(newRow);
        }

        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(PortfolioFragment.MATERIAL_COLORS);
        pieChart.setCenterText("$ "+ String.format("%.2f", totalHoldings));
        //pieChart.setCenterText("Composición");
        pieChart.setCenterTextSize(20);
        pieChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
        PieData data = new PieData(labels, dataset); // initialize Piedata
        data.setValueTextSize(12f);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setUsePercentValues(true);
        pieChart.setDrawSliceText(false);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setData(data); //set data into chart
        pieChart.setDescription(" ");  // set the description
        pieChart.setTouchEnabled(false);
        pieChart.getLegend().setTextSize(12f);

        //TextView totalHoldingsTV = (TextView) getView().findViewById(R.id.portfolio_total);
        //totalHoldingsTV.setText("$ "+ String.format("%.2f", totalHoldings));

        TableLayout tableLayout = (TableLayout) getView().findViewById(R.id.detail_table);
        for (TableRow row: detailRows) {
            tableLayout.addView(row);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
