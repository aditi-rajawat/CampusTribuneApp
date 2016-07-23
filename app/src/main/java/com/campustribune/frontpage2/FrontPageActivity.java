package com.campustribune.frontpage2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.campustribune.R;
import com.campustribune.beans.Post;
import com.campustribune.login.LoginActivity;
import com.campustribune.userProfile.UserProfileActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FrontPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        List<Post> postData= new ArrayList();
        System.out.println("Started Activity for front page");

        try {
            postData = fill_with_data(LoginActivity.postList);
            System.out.println("Started loading Data");

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(postData, getApplication(),
                new Recycler_View_Adapter.OnItemClickListener(){
                    @Override public void onItemClick(Post post) {
                        Toast.makeText(getBaseContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                        navigateToViewPostActivity();
                }

            });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

       /* RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(100);
        System.out.println("Setting up the animation!");
        recyclerView.setItemAnimator(itemAnimator);*/

    }

    private void navigateToViewPostActivity() {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_action_frontpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Take appropriate action for each action item click
        switch (menuItem.getItemId()) {
            case R.id.menu_action_refresh:
                Toast.makeText(this, "Refresh button was clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.submenu_userprofile:
                Toast.makeText(this,"User-profile menu was clicked",Toast.LENGTH_SHORT).show();
                goToUserProfilePage();
                return true;
            case R.id.submenu_search:
                Toast.makeText(this,"Search button was clicked", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void goToUserProfilePage(){
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    public List<Post> fill_with_data(ArrayList<Post> postList) throws ExecutionException, InterruptedException {
        System.out.println("SIZEEEEE"+postList.size());
        List<Post> data = new ArrayList<>();
        Iterator<Post> listIterator = postList.iterator();
        Post post= new Post();
        while(listIterator.hasNext()){
            System.out.println(" ADDING DATA///");
            post = listIterator.next();
            data.add(post);
        }
        return data;
    }

}
