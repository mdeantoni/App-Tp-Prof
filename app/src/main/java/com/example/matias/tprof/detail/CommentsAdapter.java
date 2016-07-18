package com.example.matias.tprof.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.matias.tprof.R;

import java.util.ArrayList;

/**
 * Created by Mati on 7/17/2016.
 */
public class CommentsAdapter extends ArrayAdapter<Comment> {

    public CommentsAdapter(Context context, ArrayList<Comment> comments){
        super(context,0,comments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Comment comment = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_comment, parent, false);
        }
        // Lookup view for data population
        TextView tvComment = (TextView) convertView.findViewById(R.id.textview_comment_comments);
        TextView tvDate = (TextView) convertView.findViewById(R.id.textview_date_comments);
        TextView tvUser = (TextView) convertView.findViewById(R.id.textview_user_comments);
        // Populate the data into the template view using the data object
        tvUser.setText(comment.Username);
        tvDate.setText(comment.Date);
        tvComment.setText(comment.Comment);
        // Return the completed view to render on screen
        return convertView;
    }
}
