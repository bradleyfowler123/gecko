package com.auton.bradley.myfe;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfileAgendaFragment extends Fragment {

    public ProfileAgendaFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // function that generates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_profile_agenda, container, false);        // enables easy access to the root search xml

        MainActivity activity = (MainActivity) getActivity();
        String agenda = activity.user.agenda;
        String[] agendaItems = agenda.split(":");
        String itemElements[][] = new String[2][3];
        for(int i=0; i<agendaItems.length; i++){
            itemElements[i] = agendaItems[i].split(";");
        }

        Log.println(Log.INFO,"a",itemElements[1][1]);


        return rootView;
    }
}
