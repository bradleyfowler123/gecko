package com.auton.bradley.myfe;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


// fragment that handles the friends tab
public class FriendFragment extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                                // get friend tab layout
        rootView = inflater.inflate(R.layout.fragment_friend, container, false);
                                // load tab bar and tab data into friend layout
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_friend);
        setupViewpagerChild(viewPager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout_friend);
        tabLayout.setupWithViewPager(viewPager);
        setupTabTitles();
                                // return the view
        return rootView;
    }

    public void setupViewpagerChild(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FriendFeedFragment(), "Feed");
        adapter.addFragment(new FriendMapFragment(), "Map");
        viewPager.setAdapter(adapter);
    }

    private void setupTabTitles() {
        try {
            tabLayout.getTabAt(0).setText(getString(R.string.friendFeed_tabName));
            tabLayout.getTabAt(1).setText(getString(R.string.friendMap_tabName));
        }catch (NullPointerException e){
            e.printStackTrace();

        }
    }
}