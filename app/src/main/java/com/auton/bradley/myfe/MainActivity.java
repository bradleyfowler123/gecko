package com.auton.bradley.myfe;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // declarations
    Toolbar toolbar;
    private TabLayout tabLayout;
    ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_home_grey,
            R.drawable.ic_search_grey,
            R.drawable.ic_friend_feed_grey,
            R.drawable.ic_planner
    };
    public User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                                            // load main activity layout
        setContentView(R.layout.activity_main);                                                     // load the main activity view
                                            // load action bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);                                             // enable the action bar (above tabbed menus)
        setSupportActionBar(toolbar);                                                               // you can edit action bar style in activity_main.xml
                                    // load tab bar and tab data
        viewPager = (ViewPager) findViewById(R.id.container);                                       // find view underneith tabs
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);                                            // find tab layout
        tabLayout.setupWithViewPager(viewPager);                                                    // setup view
        setupTabIcons();                                                                            // add icons to tabs
                                    // store user data if any
        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");
            String name = intent.getStringExtra("name");
            String dob = intent.getStringExtra("dob");
            user.LogIn(email,password,name,dob);
        }
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());               // generating adapter
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new SearchFragment(), "Search");
        adapter.addFragment(new friendFragment(), "Friend");
        adapter.addFragment(new PlannerFragment(), "Planner");
        viewPager.setAdapter(adapter);                                                              // set the adapter to the container
    }


                            // create options menu in action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(user.loggedIn) {
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
        }
        else {
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
        }
        return true;
    }
                            // respond to action bar item press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_logout:
                user.LogOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_login:
                Intent intent2 = new Intent(this, LoginActivity.class);
                startActivity(intent2);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
