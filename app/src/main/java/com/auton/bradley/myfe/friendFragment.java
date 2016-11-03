package com.auton.bradley.myfe;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

// fragment that handles the friends tab
public class friendFragment extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;
    View rootView;
    @Nullable
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
        adapter.addFragment(new ProfileAgendaTab(), "Agenda");
        adapter.addFragment(new ProfilePhotosTab(), "Photos");
        viewPager.setAdapter(adapter);
    }

    private void setupTabTitles() {
        tabLayout.getTabAt(0).setText("Agenda");
        tabLayout.getTabAt(1).setText("Photos");
    }
}

class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    ViewPagerAdapter(FragmentManager manager) { super(manager);}

    @Override
    public Fragment getItem(int position) { return mFragmentList.get(position);}

    @Override
    public int getCount() { return mFragmentList.size(); }

    void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) { return null;}
}