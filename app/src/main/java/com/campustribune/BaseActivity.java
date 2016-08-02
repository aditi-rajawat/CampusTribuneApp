package com.campustribune;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.campustribune.event.activity.CreateEventActivity;
import com.campustribune.event.activity.ViewAllEventsActivity;
import com.campustribune.frontpage.FrontPageActivity;
import com.campustribune.login.LoginActivity;
import com.campustribune.post.activity.CreatePostActivity;
import com.campustribune.post.activity.ViewPostsByCategoryListActivity;
import com.campustribune.userProfile.UserProfileActivity;

/**
 * Created by snshr on 7/22/2016.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID)
    {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setUniversityLogo(getSupportActionBar());
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frontPage = new Intent(BaseActivity.this, FrontPageActivity.class);
                frontPage.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(frontPage);
            }
        });

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_frontpage, menu);
        MenuItem ref = (MenuItem) menu.findItem(R.id.action_refresh);
        ref.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Take appropriate action for each action item click
        switch (menuItem.getItemId()) {
            /*case R.id.menu_action_refresh:
                Toast.makeText(this, "Refresh button was clicked", Toast.LENGTH_SHORT).show();
                return true;*/
            case R.id.submenu_userprofile:
                Toast.makeText(this,"User-profile menu was clicked",Toast.LENGTH_SHORT).show();
                goToUserProfilePage();
                return true;
            /*case R.id.submenu_search:
                Toast.makeText(this,"Search button was clicked", Toast.LENGTH_SHORT).show();
                return true;*/
            case R.id.submenu_createpost:
                goToCreatePostPage();
                return true;
            case R.id.submenu_createevent:      // Added by Aditi on 07/23/2016 START
                goToCreateEventPage();
                return true;
            case R.id.submenu_viewallevents:
                goToViewAllEventsPage();
                return true;                   // Added by Aditi on 07/23/2016 END
            case R.id.submenu_viewpostsbycategory:
                goToViewPostsByCategoryPage();
                return true;
            case R.id.submenu_logout:
                handleLogout();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void handleLogout() {

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        navigateToLoginActivity();

    }

    private void navigateToLoginActivity() {

        Intent loginIntent = new Intent(this, LoginActivity.class);
        this.finish();
        startActivity(loginIntent);
    }

    private void goToUserProfilePage(){
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(intent);
    }

    private void goToCreatePostPage(){
        Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
        startActivity(intent);
    }


    // Added by Aditi on 07/23/2016 START
    private void goToCreateEventPage(){
        Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
        startActivity(intent);
    }

    private void goToViewAllEventsPage(){
        Intent intent = new Intent(getApplicationContext(), ViewAllEventsActivity.class);
        startActivity(intent);
    }
    // Added by Aditi on 07/23/2016 END
    private void goToViewPostsByCategoryPage(){
        Intent intent = new Intent(getApplicationContext(), ViewPostsByCategoryListActivity.class);
        startActivity(intent);
    }


    private void setUniversityLogo(ActionBar actionbar){
        SharedPreferences settingsout = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String university=settingsout.getString("loggedInUserUniversity", "");
        switch (university) {
            case "SJSU":
                actionbar.setIcon(R.drawable.sjsulogo);
                return;
            case "UNCC":
                actionbar.setIcon(R.drawable.uncclogo);
                return;
            default:

        }
    }
}
