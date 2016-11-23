package com.auton.bradley.myfe;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfilePhotosFragment extends Fragment {

    public ProfilePhotosFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // function that generates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile_photos, container, false);        // enables easy access to the root search xml
      //  TextView tv = (TextView) rootView.findViewById(R.id.textView);
      //  MainActivity activity = (MainActivity) getActivity();
      //  tv.setText(activity.user.dob);
        return rootView;
    }
}
