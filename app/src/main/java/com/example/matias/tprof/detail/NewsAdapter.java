package com.example.matias.tprof.detail;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matias.tprof.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mati on 6/20/2016.
 */
public class NewsAdapter extends CursorAdapter {
    public NewsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_news, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Date publicationDate = null;
        int icon;
        SimpleDateFormat inputFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM");
        String date = cursor.getString(NewsFragment.COL_DATE);
        String source = cursor.getString(NewsFragment.COL_SOURCE).replace("\"", "");

        if(source.equals("LN")){
            icon = R.mipmap.ln_news;
        }else if(source.equals("YHOO")){
            icon = R.mipmap.yhoo_news;
        }else{
            icon = R.mipmap.cro_news;
        }

        try {
            publicationDate = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon_news);
        TextView headlinetv = (TextView) view.findViewById(R.id.headline_news);
        TextView datetv = (TextView) view.findViewById(R.id.date_news);

        iconView.setImageResource(icon);
        headlinetv.setText(cursor.getString(NewsFragment.COL_HEADLINE));
        datetv.setText(outputFormat.format(publicationDate));
    }
}
