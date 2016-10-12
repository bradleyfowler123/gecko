package com.auton.bradley.myfe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

        //final String[] weekForecast = getResources().getStringArray(R.array.testArray);
        int[] images={R.drawable.altontowers,R.drawable.climbing,R.drawable.gym,R.drawable.altontowers};
        final String[] titles = getResources().getStringArray(R.array.testArray);

       // ListAdapter adapter = new ArrayAdapter<>(getActivity(),R.layout.home_list_item, weekForecast);
        ListView home_list = (ListView) rootView.findViewById(R.id.home_list);

        homeAdapter adapter = new homeAdapter(getActivity(),titles,images);
        home_list.setAdapter(adapter);

        home_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(),"Navigating to the website",Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;
    }
}
