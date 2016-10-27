package com.auton.bradley.myfe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

// fragment that handles the planner tab
public class PlannerFragment extends Fragment {

    public PlannerFragment() {        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        TextView tvName = (TextView) getActivity().findViewById(R.id.textView);
        TextView tvDob = (TextView) getActivity().findViewById(R.id.textView2);

        MainActivity activity = (MainActivity) getActivity();
        ArrayList<String> userData = activity.getUserData();
        tvName.setText(userData.get(0));
        tvDob.setText(userData.get(1));

        return inflater.inflate(R.layout.fragment_planner, container, false);
    }
}