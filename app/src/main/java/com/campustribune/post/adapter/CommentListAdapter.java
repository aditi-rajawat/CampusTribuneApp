package com.campustribune.post.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.campustribune.beans.PostComment;
import com.campustribune.R;
import java.util.ArrayList;

/**
 * Created by snshr on 7/15/2016.
 */
public class CommentListAdapter extends BaseAdapter {
    private ArrayList<PostComment> commentsList;
    private LayoutInflater layoutInflater;

    public CommentListAdapter(Context aContext, ArrayList<PostComment> listData) {
        this.commentsList = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return commentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_comment_list_layout, null);
            holder = new ViewHolder();
            holder.creator = (TextView) convertView.findViewById(R.id.creator);
            holder.commentVal = (TextView) convertView.findViewById(R.id.commentVal);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(position %2 == 1)
        {
            convertView.setBackgroundColor(Color.parseColor("#006bb3"));
        }
        else
        {
             convertView.setBackgroundColor(Color.parseColor("#005c99"));
        }

        holder.creator.setText("Created By "+commentsList.get(position).getUserId());
        holder.commentVal.setText(commentsList.get(position).getCommentContent());
        return convertView;
    }

    static class ViewHolder {
        TextView creator;
        TextView commentVal;

    }
}
