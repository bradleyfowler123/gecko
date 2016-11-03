package com.auton.bradley.myfe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

// fragment that handles the friends tab
public class friendFragment extends Fragment {
    ViewPager viewPager_child;
    TabLayout tabLayout_child;
    View rootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_friend, container, false);


        viewPager_child = (ViewPager) rootview.findViewById(R.id.viewpager_child);
        tabLayout_child = (TabLayout) rootview.findViewById(R.id.tabLayout_child);


        setupViewpagerChild(viewPager_child);
        tabLayout_child.post(new Runnable() {
            @Override
            public void run() {
                tabLayout_child.setupWithViewPager(viewPager_child);
            }
        });

        tabLayout_child.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager_child.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return rootview;
    }

    public void setupViewpagerChild(ViewPager viewPager_child) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ProfileAgendaTab(), "Agenda");
        adapter.addFragment(new ProfilePhotosTab(), "Photos");
        viewPager_child.setAdapter(adapter);
    }
}

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

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null; //mFragmentTitleList.get(position);
    }
}