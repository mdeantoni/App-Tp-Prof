package com.example.matias.tprof.search;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.StockQuotesFragment;
import com.example.matias.tprof.numbers.NumberFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mati on 5/30/2016.
 */
public class SearchResultsAdapter extends CursorAdapter {
    public SearchResultsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Date quoteDate = null;
        SimpleDateFormat inputFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM");
        String date = cursor.getString(SearchResultsActivity.COL_LAST_TRADE_DATE);
        String changeText = NumberFormat.formattedValue(cursor.getString(SearchResultsActivity.COL_LAST_CHANGE)) + " "
                + "(" + NumberFormat.formattedValue(cursor.getString(SearchResultsActivity.COL_LAST_CHANGE_PERCENTAGE)) + "%)";

        try {
            quoteDate = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView quote = (TextView) view.findViewById(R.id.list_item_quote_textview);
        TextView change = (TextView) view.findViewById(R.id.list_item_changes_textview);
        TextView price = (TextView) view.findViewById(R.id.list_item_lastprice_textview);
        TextView datetv = (TextView) view.findViewById(R.id.list_item_datetime_textview);

        if(cursor.getDouble(SearchResultsActivity.COL_LAST_CHANGE) > 0){
            change.setTextColor(Color.GREEN);
            changeText = "+" + changeText;
        }else{
            change.setTextColor(Color.RED);
        }

        quote.setText(cursor.getString(SearchResultsActivity.COL_FULLNAME));
        change.setText(changeText);
        price.setText(cursor.getString(SearchResultsActivity.COL_CURRENCY) + " " + NumberFormat.formattedValue(cursor.getString(SearchResultsActivity.COL_LAST_PRICE)));
        datetv.setText(outputFormat.format(quoteDate));
    }
}
