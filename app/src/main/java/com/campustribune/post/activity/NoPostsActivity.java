package com.campustribune.post.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.campustribune.BaseActivity;
import com.campustribune.R;

public class NoPostsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_posts);
        // Set the toolbar according to the previous activity
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goToViewPostsByCategoryPage();
            }
        });
    }
    private void goToViewPostsByCategoryPage(){
        Intent intent = new Intent(getApplicationContext(), ViewPostsByCategoryListActivity.class);
        startActivity(intent);
    }
}
