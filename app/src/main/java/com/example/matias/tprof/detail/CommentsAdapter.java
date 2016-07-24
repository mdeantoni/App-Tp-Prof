package com.example.matias.tprof.detail;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.matias.tprof.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.text.ParseException;
/**
 * Created by Mati on 7/17/2016.
 */
public class CommentsAdapter extends CursorAdapter {

    public CommentsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_comment, parent, false);
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Date quoteDate = null;
        SimpleDateFormat inputFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM");

        TextView tvComment = (TextView) view.findViewById(R.id.textview_comment_comments);
        TextView tvDate = (TextView) view.findViewById(R.id.textview_date_comments);
        TextView tvUser = (TextView) view.findViewById(R.id.textview_user_comments);

        String date = cursor.getString(CommentsFragment.COL_DATE);
        
        try {
            quoteDate = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvUser.setText(cursor.getString(CommentsFragment.COL_USERNAME));
        tvComment.setText(cursor.getString(CommentsFragment.COL_COMMENET));
        tvDate.setText(outputFormat.format(quoteDate));
    }
}
