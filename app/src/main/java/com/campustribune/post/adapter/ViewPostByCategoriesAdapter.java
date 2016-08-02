package com.campustribune.post.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.campustribune.R;
import com.campustribune.beans.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Divya on 7/27/2016.
 */
public class ViewPostByCategoriesAdapter extends ArrayAdapter<Post> {

    public interface ViewPostInterface{
        void viewPostDetail(Post post);
    }

    private ViewPostInterface myPostInterface;
    private Context context;

    public ViewPostByCategoriesAdapter(Context context, ArrayList<Post> posts, Activity activity){
        super(context, 0, posts);
        this.context=context;
        try{
            this.myPostInterface = (ViewPostInterface)activity;
        }catch (ClassCastException ex){
            System.out.println("ViewAllEventsActivity must implement ViewEachEventInterface!");
        }
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {

        final Post post = getItem(position);

        TextView txtHeadline;
        ImageView imgPostImage;
        TextView txtContent;
        TextView txtPostUserId;
        ImageView alertImage;
        FrameLayout imageFrame;



        if (rowView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.view_posts_image, parent, false);
        }

        txtHeadline = (TextView) rowView.findViewById(R.id.heading);
        imgPostImage = (ImageView) rowView.findViewById(R.id.post_category_image);
        txtContent = (TextView) rowView.findViewById(R.id.description);
        txtPostUserId = (TextView) rowView.findViewById(R.id.posted);
        alertImage = (ImageView) rowView.findViewById(R.id.alertFlag);
        imageFrame =(FrameLayout)rowView.findViewById(R.id.imageFrameLayout);

        txtHeadline.setText(post.getHeadline());
        txtContent.setText(post.getContent());
        txtPostUserId.setText("Created By " + post.getUserId());
        if(post.isAlert()) {
            System.out.println("THIS IS AN ALERT");
            alertImage.setVisibility(View.VISIBLE);
        }else {
            alertImage.setVisibility(View.GONE);
        }
        if(post.getImgURL()!=null && post.getImgURL().length()>0) {
            imageFrame.setVisibility(View.VISIBLE);
            Picasso.with(context).load(post.getImgURL()).into(imgPostImage);
        }else {
            imageFrame.setVisibility(View.GONE);
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPostByCategoriesAdapter.this.myPostInterface.viewPostDetail(post);
            }
        });

        return rowView;
    }
}
