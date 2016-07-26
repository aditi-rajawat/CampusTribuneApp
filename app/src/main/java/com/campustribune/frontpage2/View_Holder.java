package com.campustribune.frontpage2;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.campustribune.R;
import com.campustribune.beans.Post;
import com.squareup.picasso.Picasso;

/**
 * Created by sandyarathidas on 7/18/16.
 */
public class View_Holder extends RecyclerView.ViewHolder {

    CardView cv;
    TextView title;
    TextView description;
    ImageView image;
    ImageView alert;
    TextView owner;

    View_Holder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cardView);
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        image = (ImageView) itemView.findViewById(R.id.imageView);
        alert=(ImageView) itemView.findViewById(R.id.alertFlag);
        owner=(TextView) itemView.findViewById(R.id.ownername);

    }

    public void bind(final Post post, final Recycler_View_Adapter.OnItemClickListener listener) {
        title.setText(post.getHeadline());
        description.setText(post.getContent());
        owner.setText("Created By "+post.getUserId());

        if(post.isAlert())
            alert.setVisibility(View.VISIBLE);

        if(post.getImgURL()!=null && post.getImgURL().length()>0)
            Picasso.with(itemView.getContext()).load(post.getImgURL()).into(image);
        else
            image.setVisibility(View.GONE);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(post);
            }
        });
    }

}
