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

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ProfileAgendaFragment extends Fragment {

    ArrayList<String> activities = new ArrayList<>();
    ArrayList<String> companies = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String> listItems = new ArrayList<>();

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
        final ListView pa_list = (ListView) rootView.findViewById(R.id.profile_agenda_list);             // locate the list object in the home tab
        Log.d("drtfg", getActivity().getLocalClassName());
        String Uid;
        if (getActivity().getLocalClassName().equals("FriendActivity")) {
            final FriendActivity activity = (FriendActivity) getActivity();
            Uid = activity.UIDs.get(activity.index);
        }
        else {
            final MainActivity activity = (MainActivity) getActivity();
            Uid = activity.auth.getCurrentUser().getUid();
        }


        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference agenda = database.child("users").child(Uid).child("Agenda");
        agenda.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 GenericTypeIndicator<HashMap<String, AgendaClass>> t = new GenericTypeIndicator<HashMap<String, AgendaClass>>() {};
                 HashMap<String, AgendaClass> agendaData = dataSnapshot.getValue(t);              // get agenda data
                 Iterator<AgendaClass> iterator = agendaData.values().iterator();                // parse out a list of friendClass'
                 Iterator<String> keys = agendaData.keySet().iterator();
                 while (iterator.hasNext()) {
                     String key = keys.next();
                     AgendaClass agendaItem = iterator.next();
                     // if already exists in list
                     if (listItems.contains(key)) {          // remove it
                         listItems.remove(key);
                         activities.remove(agendaItem.activity);
                         dates.remove(agendaItem.date);
                         companies.remove(agendaItem.company);
                     }               // add agenda item to list
                     listItems.add(key);
                     activities.add(agendaItem.activity);
                     dates.add(agendaItem.date);
                     companies.add(agendaItem.company);
                     // update the list view
                     profileAgendaAdapter adapter = new profileAgendaAdapter(getActivity(), activities, dates, companies);
                     pa_list.setAdapter(adapter);
                 }
             }
             @Override
             public void onCancelled(DatabaseError databaseError) {
             }
        });

        return rootView;
    }
}


                        // adapter used for to show your activities as a list view in profile agenda sub tab
class profileAgendaAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private ArrayList<String> activities;
    private ArrayList<String> dates;
    private ArrayList<String> companies;
    private Context c;
    // define a function that can be used to declare this custom adapter class
    profileAgendaAdapter(Context context, ArrayList<String> activities,ArrayList<String> dates, ArrayList<String> companies) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.profile_agenda_list_item,activities);
        this.c=context;
        this.activities=activities;
        this.dates=dates;
        this.companies=companies;
    }
    // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView activity;
        TextView date;
        TextView companies;
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
        holder.activity=(TextView) convertView.findViewById(R.id.tv_profile_agenda_activities);
        holder.date=(TextView) convertView.findViewById(R.id.tv_profile_agenda_date);
        holder.companies=(TextView) convertView.findViewById(R.id.tv_profile_agenda_company);
        // populate the title and image with data for a list item
        holder.activity.setText(activities.get(position));
        holder.date.setText(dates.get(position));
        holder.companies.setText(companies.get(position));
        // return the updated view
        return convertView;
    }

}