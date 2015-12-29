package com.example.matias.tprof;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mati on 12/8/2015.
 */
public class BondQuotesAdapter extends CursorAdapter {
    public BondQuotesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView quote = (TextView) view.findViewById(R.id.list_item_quote_textview);
        TextView change = (TextView) view.findViewById(R.id.list_item_changes_textview);
        TextView price = (TextView) view.findViewById(R.id.list_item_lastprice_textview);
        TextView date = (TextView) view.findViewById(R.id.list_item_datetime_textview);

        quote.setText(cursor.getString(BondQuotesFragment.COL_FULLNAME));
        change.setText(cursor.getString(BondQuotesFragment.COL_LAST_CHANGE));
        price.setText(cursor.getString(BondQuotesFragment.COL_CURRENCY) + cursor.getString(BondQuotesFragment.COL_LAST_PRICE));
        date.setText(cursor.getString(BondQuotesFragment.COL_LAST_TRADE_DATE));
    }
}
