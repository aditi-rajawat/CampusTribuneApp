package com.campustribune.frontpage;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.campustribune.R;
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
    Button category;

    View_Holder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cardView);
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        image = (ImageView) itemView.findViewById(R.id.imageView);
        alert=(ImageView) itemView.findViewById(R.id.alertFlag);
        owner=(TextView) itemView.findViewById(R.id.ownername);
        category=(Button) itemView.findViewById(R.id.label_category);

    }

    public void bind(final Data data, final Recycler_View_Adapter.OnItemClickListener listener) {
        if(data.getItemType().equalsIgnoreCase("Event")){
            System.out.println("THIS IS AN EVENT");
            //cv.setCardBackgroundColor(Color.parseColor("#DECAC5"));
            cv.setCardBackgroundColor(Color.parseColor("#CDDEC5"));
        }
        else {

            cv.setCardBackgroundColor(Color.WHITE);
        }
        title.setText(data.getItemTitle());
        System.out.println("Title" + data.getItemTitle());
        description.setText(data.getItemContent());
        category.setText(data.getItemCategory());
        owner.setText("Created By " + data.getItemOwnerId());

        if(data.getIsItemAlert()) {

            System.out.println("THIS IS AN ALERT");
            alert.setVisibility(View.VISIBLE);
        }else{
            alert.setVisibility(View.GONE);
        }

        if(data.getItemImageURL()!=null && data.getItemImageURL().length()>0) {
            image.setVisibility(View.VISIBLE);
            Picasso.with(itemView.getContext()).load(data.getItemImageURL()).into(image);
        }else {
            image.setVisibility(View.GONE);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(data);
            }
        });
    }

}
