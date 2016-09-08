package com.auton.bradley.myfe;

import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_home_grey,
            R.drawable.ic_search_grey,
            R.drawable.ic_friend_feed_grey,
            R.drawable.ic_settings_grey
    };

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
        adapter.addFragment(new SettingsFragment(), "Settings");
        viewPager.setAdapter(adapter);                                                              // set the adapter to the container
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null; //mFragmentTitleList.get(position);
        }
    }



                            // create options menu in action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
