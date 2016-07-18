package com.campustribune.frontpage;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.campustribune.R;

import java.util.HashMap;

/**
 * Created by sandyarathidas on 7/17/16.
 */
public class ListViewAdapter extends ArrayAdapter<Object>{

    HashMap<Integer,ListItem> newsDataHashMap;
    private final Context context;

    Integer count =0;

    public ListViewAdapter(Context context, int count, HashMap<Integer, ListItem> newsDataHashMap) {
        super(context, count);
        this.context= context;
        this.newsDataHashMap = newsDataHashMap;
        this.count = count;
    }

    public int getCount(){
        Log.e("count", String.valueOf(count));
        return count;

    }

    public Object getItem(int position) {
        return (Object) newsDataHashMap.get(position);
    }


    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=((Activity)getContext()).getLayoutInflater();
        View rowView;
        if (position==0)
            rowView=inflater.inflate(R.layout.activity_topitem, null, true);
        else
            rowView=inflater.inflate(R.layout.activity_frontpage_list, null, true);

        TextView txtHeadline = (TextView) rowView.findViewById(R.id.heading);
        ImageView imgPostImage = (ImageView) rowView.findViewById(R.id.news_image);
        TextView txtContent = (TextView) rowView.findViewById(R.id.description);
        TextView txtPostUserId    = (TextView) rowView.findViewById(R.id.posted);

        Log.e("getView", "1");
        if(newsDataHashMap.get(position)!= null) {
            txtHeadline.setText(newsDataHashMap.get(position).getHeadline());
            imgPostImage.setImageBitmap(newsDataHashMap.get(position).getBitMapImage());
            txtContent.setText(newsDataHashMap.get(position).getContent());
            //txtPostUserId.setText(newsDataHashMap.get(position).getUserId());
            txtPostUserId.setText("by Sandy");

        }
        return rowView;

    };


}
