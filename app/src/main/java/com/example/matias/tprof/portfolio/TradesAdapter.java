package com.example.matias.tprof.portfolio;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.detail.NewsFragment;
import com.example.matias.tprof.numbers.NumberFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mati on 8/1/2016.
 */
public class TradesAdapter extends CursorAdapter {

    private final String BUY_TRADE_TYPE = "Compra";
    private final String SELL_TRADE_TYPE = "Venta";

    public TradesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_trade, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Date operationDate = null;
        SimpleDateFormat inputFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy");
        String date = cursor.getString(TradesFragment.COL_DATE);
        String operation = cursor.getString(TradesFragment.COL_OPERATION).equals(BUY_TRADE_TYPE) ? "+" : "-";

        try {
            operationDate = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView priceTv = (TextView) view.findViewById(R.id.list_item_trade_price_textview);
        TextView quantityTv = (TextView) view.findViewById(R.id.list_item_trade_quantity_textview);
        TextView nameTv = (TextView) view.findViewById(R.id.list_item_trade_symbol_textview);
        TextView dateTv = (TextView) view.findViewById(R.id.list_item_trade_datetime_textview);

        quantityTv.setText(operation + cursor.getString(TradesFragment.COL_QUANTITY));
        nameTv.setText(cursor.getString(TradesFragment.COL_FULLNAME));
        priceTv.setText("$" + NumberFormat.formattedValue(cursor.getString(TradesFragment.COL_PRICE)));
        dateTv.setText(outputFormat.format(operationDate));
    }
}
