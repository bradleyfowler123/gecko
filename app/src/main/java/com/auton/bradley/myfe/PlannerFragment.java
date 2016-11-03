package com.auton.bradley.myfe;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// fragment that handles the planner tab
public class PlannerFragment extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;
    View rootView;
    public PlannerFragment() {        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

                                    // function that generates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_planner, container, false);        // enables easy access to the root search xml
        TextView tvName = (TextView) rootView.findViewById(R.id.tv_profile_name);
        ImageView profilePic = (ImageView) rootView.findViewById(R.id.img_profile_pic);

        MainActivity activity = (MainActivity) getActivity();
        if(activity.user.loggedIn) {
            tvName.setText(activity.user.name);
            profilePic.setVisibility(View.VISIBLE);

            // load tab bar and tab data into friend layout
            viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_friend);
            setupViewpagerChild(viewPager);
            tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout_friend);
            tabLayout.setupWithViewPager(viewPager);
            setupTabTitles();

        }
        else {
            tvName.setText("You should Sign Up");
            profilePic.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    public void setupViewpagerChild(ViewPager viewPager) {
        ViewPagerAdapter2 adapter = new ViewPagerAdapter2(getChildFragmentManager());
        adapter.addFragment(new ProfileAgendaTab(), "Agenda");
        adapter.addFragment(new ProfilePhotosTab(), "Photos");
        viewPager.setAdapter(adapter);
    }

    private void setupTabTitles() {
        tabLayout.getTabAt(0).setText("Agenda");
        tabLayout.getTabAt(1).setText("Photos");
    }
}

class ViewPagerAdapter2 extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    ViewPagerAdapter2(FragmentManager manager) { super(manager);}

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