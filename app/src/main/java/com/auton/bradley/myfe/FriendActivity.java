package com.auton.bradley.myfe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that displays a friends profile
 */


public class FriendActivity extends AppCompatActivity {
                                        // global variable declarations
    private TabLayout tabLayout;
    ViewPager viewPager;
    public ArrayList<String> UIDs;
    public ArrayList<String> names;
    public ArrayList<String> Urls;
    public int index;
                                        // main function
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
                            // get facebook friend's data
        Intent intent = getIntent();
        UIDs = intent.getStringArrayListExtra("uid");
        names = intent.getStringArrayListExtra("name");
        Urls = intent.getStringArrayListExtra("url");
        index = intent.getIntExtra("index",0);
                            // get views
        TextView naneTv = (TextView) findViewById(R.id.af_tv_profile_name);
        naneTv.setText(names.get(index));
        ImageView imageView = (ImageView) findViewById(R.id.af_img_profile_pic);
        RequestCreator picURL = Picasso.with(getBaseContext()).load(Urls.get(index));
        picURL.transform(new CircleTransform()).into(imageView);
                        // load tab bar and tab data
        viewPager = (ViewPager) findViewById(R.id.af_viewpager_profile);                                       // find view underneith tabs
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.af_tabLayout_profile);                                            // find tab layout
        tabLayout.setupWithViewPager(viewPager);                                                    // setup view
        setupTabTitles();                                                                            // add icons to tabs
    }
                        // add the tabs
    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());               // generating adapter
        adapter.addFragment(new ProfileAgendaFragment(), "Agenda");
        adapter.addFragment(new ProfilePhotosFragment(), "Photos");
        adapter.addFragment(new ProfileFriendsListFragment(), "Friends");
        viewPager.setAdapter(adapter);
    }

    private void setupTabTitles() {
        tabLayout.getTabAt(0).setText(getString(R.string.profileAgenda_tabName));
        tabLayout.getTabAt(1).setText(getString(R.string.profilePhotos_tabName));
        tabLayout.getTabAt(2).setText("Your Friends");
    }
}

/**
 * Custom adapter used for defining adding fragment tabs to a viewpager
 */

class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    ViewPagerAdapter(FragmentManager manager) {
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

    void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null; //mFragmentTitleList.get(position);
    }
}


