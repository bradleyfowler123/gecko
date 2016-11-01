package com.auton.bradley.myfe;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

                                // fragment that handles the planner tab
public class PlannerFragment extends Fragment {

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
     //   FragmentTabHost tabHost;
        final View rootView = inflater.inflate(R.layout.fragment_planner, container, false);        // enables easy access to the root search xml
        TextView tvName = (TextView) rootView.findViewById(R.id.tv_profile_name);
        ImageView profilePic = (ImageView) rootView.findViewById(R.id.img_profile_pic);

        MainActivity activity = (MainActivity) getActivity();
        if(activity.user.loggedIn) {
            tvName.setText(activity.user.name);
            profilePic.setVisibility(View.VISIBLE);

     /*       View v = inflater.inflate(R.layout.fragment_planner, container, false);

            mTabHost = (FragmentTabHost) v.findViewById(android.R.id.tabhost);
            mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabs);

            mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Tab 1"), ProfileAgendaTab.class, null);

            mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Tab 2"), ProfilePhotosTab.class, null);


            host.setup(getActivity(),getChildFragmentManager());

            host.addTab(host.newTabSpec("first").setIndicator("First").setContent(new Intent(getActivity()  ,ProfileAgendaTab.class )));
            host.addTab(host.newTabSpec("second").setIndicator("Second").setContent(new Intent(getActivity(), ProfilePhotosTab.class )));
            host.setCurrentTab(0);

*/

        }
        else {
            tvName.setText("You should Sign Up");
            profilePic.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }
}