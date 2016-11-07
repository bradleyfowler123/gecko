package com.auton.bradley.myfe;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

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
        ListView pa_list = (ListView) rootView.findViewById(R.id.profile_agenda_list);             // locate the list object in the home tab

        MainActivity activity = (MainActivity) getActivity();
        String agenda = activity.user.agenda;
        String[] agendaItems = agenda.split(":");
        String itemElements[][] = new String[2][3];
        ArrayList<String> activities = new ArrayList<>();
        ArrayList<String> companies = new ArrayList<>();
        for(int i=0; i<agendaItems.length; i++){
            itemElements[i] = agendaItems[i].split(";");
            activities.add(itemElements[i][0]);
            companies.add(itemElements[i][1]);
        }

        Log.println(Log.WARN,"qwerty1",activities.get(0));
        Log.println(Log.WARN,"qwerty1",activities.get(1));
        Log.println(Log.WARN,"qwerty3",companies.get(0));
        Log.println(Log.WARN,"qwerty4",companies.get(1));

        profileAgendaAdapter adapter = new profileAgendaAdapter(getActivity(),agendaItems);
        pa_list.setAdapter(adapter);
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,agendaItems);
        pa_list.setAdapter(itemsAdapter);

        return rootView;
    }
}


// adapter used for friend's activities list view in friend fees tab
class profileAgendaAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private String[] friendNames;
    private Context c;
    // define a function that can be used to declare this custom adapter class
    profileAgendaAdapter(Context context, String[] friendNames) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.profile_agenda_list_item,friendNames);
        this.c=context;
        this.friendNames=friendNames;
    }
    // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView friends;
    }
    // function that generates the list view
    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent){
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup nullParent = null;
            convertView=inflater.inflate(R.layout.profile_agenda_list_item,nullParent);
        }
        // find the views within the list
        final ViewHolder holder=new ViewHolder();
        holder.friends=(TextView) convertView.findViewById(R.id.textView2);
        // populate the title and image with data for a list item
        holder.friends.setText(friendNames[position]);
        // return the updated view
        return convertView;
    }

}