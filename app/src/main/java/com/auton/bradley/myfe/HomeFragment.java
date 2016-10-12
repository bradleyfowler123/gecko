package com.auton.bradley.myfe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);

        String[] weekForecast = getResources().getStringArray(R.array.testArray);

        ListAdapter adapter = new ArrayAdapter<>(getActivity(),R.layout.home_list_item, weekForecast);
        ListView home_list = (ListView) rootView.findViewById(R.id.home_list);
        home_list.setAdapter(adapter);

        return rootView;
    }
}
