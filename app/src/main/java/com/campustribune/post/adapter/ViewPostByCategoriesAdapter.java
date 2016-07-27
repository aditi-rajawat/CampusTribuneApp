package com.campustribune.post.adapter;

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
import com.campustribune.post.activity.PostListItemData;

import java.util.HashMap;

/**
 * Created by Divya on 7/27/2016.
 */
public class ViewPostByCategoriesAdapter extends ArrayAdapter<Object> {

    HashMap<Integer,PostListItemData> newsDataHashMap;
    private final Context context;

    Integer count =0;

    public ViewPostByCategoriesAdapter(Context context, int count, HashMap<Integer, PostListItemData> newsDataHashMap) {
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


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=((Activity)getContext()).getLayoutInflater();
        View rowView;
        if(newsDataHashMap.get(position).getBitMapImage()!=null) {
            rowView = inflater.inflate(R.layout.view_posts_image, null, true);
            TextView txtHeadline = (TextView) rowView.findViewById(R.id.heading);
            ImageView imgPostImage = (ImageView) rowView.findViewById(R.id.news_image);
            TextView txtContent = (TextView) rowView.findViewById(R.id.description);
            TextView txtPostUserId = (TextView) rowView.findViewById(R.id.posted);

            Log.e("getView", "1");
            if (newsDataHashMap.get(position) != null) {
                txtHeadline.setText(newsDataHashMap.get(position).getHeadline());
                imgPostImage.setImageBitmap(newsDataHashMap.get(position).getBitMapImage());
                txtContent.setText(newsDataHashMap.get(position).getContent());
                txtPostUserId.setText("Created By "+ newsDataHashMap.get(position).getUserId());
                //txtPostUserId.setText("by Sandy");

            }

        }else {
            rowView = inflater.inflate(R.layout.view_posts, null, true);
            TextView txtHeadline1 = (TextView) rowView.findViewById(R.id.heading);
            TextView txtContent1 = (TextView) rowView.findViewById(R.id.description);
            TextView txtPostUserId1 = (TextView) rowView.findViewById(R.id.posted);

            Log.e("getView", "1");
            if (newsDataHashMap.get(position) != null) {
                txtHeadline1.setText(newsDataHashMap.get(position).getHeadline());
                txtContent1.setText(newsDataHashMap.get(position).getContent());
                txtPostUserId1.setText("Created By "+ newsDataHashMap.get(position).getUserId());
                //txtPostUserId1.setText("by Divya");
            }
        }
        return rowView;

    };


}
