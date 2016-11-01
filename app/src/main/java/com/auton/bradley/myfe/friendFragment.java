package com.auton.bradley.myfe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

                                // fragment that handles the friends tab
public class friendFragment extends Fragment {
private FragmentTabHost mTabHost;
    public friendFragment() {        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      //  View rootView = (View) inflater.inflate(R.layout.fragment_friend, container, false);
        // Inflate the layout for this fragment

        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.layout.fragment_friend);

        mTabHost.addTab(mTabHost.newTabSpec("Tab1").setIndicator("Feed"),
                ProfileAgendaTab.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("Tab2").setIndicator("Map"),
                ProfilePhotosTab.class, null);

        return mTabHost;


         }
}
