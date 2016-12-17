package com.auton.bradley.myfe;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

// fragment that handles the planner tab
public class ProfileFragment extends Fragment {
    ViewPager viewPager;
    ViewPager viewPagerNFb;
    TabLayout tabLayout;
    View rootView;
                                    // function that generates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);        // enables easy access to the root search xml
        TextView tvName = (TextView) rootView.findViewById(R.id.tv_profile_name);
        ViewSwitcher loggedInView = (ViewSwitcher) rootView.findViewById(R.id.profile_loggedIn);
        TextView signUpView = (TextView) rootView.findViewById(R.id.profile_signUp_text);
                                    // get user info
        MainActivity activity = (MainActivity) getActivity();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Bundle fbData = activity.facebookData;
        Boolean fbCon = activity.facebookConnected;
                                // if user logged in
        if(user != null) {
                                // do not show sign up view
            loggedInView.setVisibility(View.VISIBLE);
            signUpView.setVisibility(View.GONE);
            if(fbCon) {
                // do nothing means show facebook logged in view
                                // get user data
                tvName.setText(user.getDisplayName());
                ImageView imageView = (ImageView) rootView.findViewById(R.id.img_profile_pic);
                RequestCreator picURL = Picasso.with(getContext()).load(fbData.getString("profile_pic"));
                picURL.transform(new CircleTransform()).into(imageView);
                                // load tab bar and tab data into friend layout
                viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_profile);
                setupViewpagerChild(viewPager);
                tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout_profile);
                tabLayout.setupWithViewPager(viewPager);
                setupTabTitles();
            }
            else {
                loggedInView.showNext();            // show view for user with no facebook connected
                viewPagerNFb = (ViewPager) rootView.findViewById(R.id.viewpager_profile_noFB);
                ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
                adapter.addFragment(new ProfileAgendaFragment(), "Agenda");
                viewPagerNFb.setAdapter(adapter);
            }
        }
        else {
            loggedInView.setVisibility(View.GONE);
            signUpView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    public void setupViewpagerChild(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ProfileAgendaFragment(), "Agenda");
        adapter.addFragment(new ProfilePhotosFragment(), "Photos");
        adapter.addFragment(new ProfileFriendsListFragment(), "Friends");
        viewPager.setAdapter(adapter);
    }

    private void setupTabTitles() {
        tabLayout.getTabAt(0).setText(getString(R.string.profileAgenda_tabName));
        tabLayout.getTabAt(1).setText(getString(R.string.profilePhotos_tabName));
        tabLayout.getTabAt(2).setText(getString(R.string.profileFriends_tabName));
    }
}