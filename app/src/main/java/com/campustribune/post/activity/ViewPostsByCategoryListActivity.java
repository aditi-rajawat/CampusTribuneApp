package com.campustribune.post.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.campustribune.BaseActivity;
import com.campustribune.R;

public class ViewPostsByCategoryListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts_by_category_list);
        ImageView view =(ImageView)findViewById(R.id.admin);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewPostsByCategoryActivity.class);
                intent.putExtra("category","Administration");
                startActivity(intent);
            }
        });
        ImageView view1 =(ImageView)findViewById(R.id.tech);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewPostsByCategoryActivity.class);
                intent.putExtra("category","Technology");
                startActivity(intent);
            }
        });
        ImageView view2 =(ImageView)findViewById(R.id.sport);
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewPostsByCategoryActivity.class);
                intent.putExtra("category","Sports");
                startActivity(intent);
            }
        });
        ImageView view3 =(ImageView)findViewById(R.id.art);
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewPostsByCategoryActivity.class);
                intent.putExtra("category","Arts");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_frontpage, menu);
        MenuItem viewallevents = (MenuItem)menu.findItem(R.id.submenu_viewpostsbycategory);
        viewallevents.setVisible(false);
        MenuItem ref = (MenuItem) menu.findItem(R.id.action_refresh);
        ref.setVisible(false);
        return true;
    }

}
