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
        final View rootView = inflater.inflate(R.layout.fragment_planner, container, false);         // enables easy access to the root search xml

        TextView tvName = (TextView) rootView.findViewById(R.id.textView);
        TextView tvDob = (TextView) rootView.findViewById(R.id.textView2);

        MainActivity activity = (MainActivity) getActivity();
        if(activity.user.loggedIn) {
            tvName.setText("Hey " + activity.user.name +"! You were born on " + activity.user.dob);
            tvDob.setText(activity.user.email);
        }
        else {
            tvName.setText("You should Sign Up");
            tvDob.setText("");
        }


        return rootView;
    }
}