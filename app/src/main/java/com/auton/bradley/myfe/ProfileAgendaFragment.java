package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class ProfileAgendaFragment extends Fragment {

    ArrayList<AgendaClass> listItems = new ArrayList<>();
    ArrayList<AgendaClass> sortedList = new ArrayList<>();

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
        final String Uid; final String friendName; final String friendUrl;
                                // handle whether fragment was called by profile tab or friend activity
        if (getActivity().getLocalClassName().equals("FriendActivity")) {
            final FriendActivity activity = (FriendActivity) getActivity();
            Uid = activity.UIDs.get(activity.index);
            friendName = activity.names.get(activity.index);
            friendUrl = activity.Urls.get(activity.index);
        }
        else {
            final MainActivity activity = (MainActivity) getActivity();
            Uid = activity.auth.getCurrentUser().getUid();
            friendName = null; friendUrl = null;
        }
                                        // get data to be displayed
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference agenda = database.child("users").child(Uid).child("Agenda");
        agenda.addValueEventListener(new ValueEventListener() {
             @Override                  // all agenda items handled at once
             public void onDataChange(DataSnapshot dataSnapshot) {
                 GenericTypeIndicator<HashMap<String, AgendaClass>> t = new GenericTypeIndicator<HashMap<String, AgendaClass>>() {
                 };
                 HashMap<String, AgendaClass> agendaData = dataSnapshot.getValue(t);              // get agenda data
                 if (agendaData != null) {                                                          // if user or friend has agenda items
                     Iterator<AgendaClass> iterator = agendaData.values().iterator();                // parse out a list of friendClass'
                     Iterator<String> keySet = agendaData.keySet().iterator();

                     listItems.clear();
                     ArrayList<String> titles = new ArrayList<>();
                     while (iterator.hasNext()) {
                         AgendaClass listItem = iterator.next();
                         listItem.rank = calcRank(listItem.date,listItem.time);
                         listItem.key = keySet.next();
                         listItems.add(listItem);
                         titles.add(listItem.activity);
                     }                // populate list
                     sortedList = listItems;
                     Collections.sort(sortedList, new AgendaComparator());
                     if (getActivity()!=null) {
                         profileAgendaAdapter adapter = new profileAgendaAdapter(getActivity(), sortedList, titles);
                         pa_list.setAdapter(adapter);
                     }
                 }
             }
             @Override
             public void onCancelled(DatabaseError databaseError) {
                 Log.d("Datebase Error2", databaseError.toString());
             }
        });
                                // if agenda item clicked
        pa_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                // load custom detailed view
                Intent intent = new Intent(getActivity(),DetailedItemActivity.class);
                AgendaClass listItem = sortedList.get(i);
                if(getActivity().toString().contains("FriendActivity")) {
                    intent.putExtra("from", "friendPage");                                          // custom detailed friend
                    intent.putExtra("friendDate",listItem.date);
                    intent.putExtra("friendTime",listItem.time);
                    intent.putExtra("friendName", friendName);
                    intent.putExtra("friendUrl", friendUrl);
                }
                else {
                    intent.putExtra("from", "profile");
                    intent.putExtra("date",listItem.date);
                    intent.putExtra("time",listItem.time);
                    intent.putExtra("userRef", listItem.key);
                }
                intent.putExtra("ref",listItem.ref);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private int calcRank(String dateString, String timeString) {
        int rank;
        SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            Date date = formatDate.parse(dateString);
            Date time = formatTime.parse(timeString);
            Date dateCurrent = Calendar.getInstance().getTime();
            int day1 = (int) (date.getTime()/(1000*60*60*24L));
            int day2 = (int) (dateCurrent.getTime()/(1000*60*60*24L));
            int  daysApart = day1-day2;
            rank = (int) (time.getTime()/(24*60L)+2500) + daysApart*100000;
           } catch (ParseException e) {
            e.printStackTrace();
            rank = 0;
        }
        return rank;
    }
}


                        // adapter used for to show your activities as a list view in profile agenda sub tab
class profileAgendaAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private ArrayList<AgendaClass> items;
    private Context c;
    // define a function that can be used to declare this custom adapter class
    profileAgendaAdapter(Context context, ArrayList<AgendaClass> list, ArrayList<String> titles) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.profile_agenda_list_item, titles);
        this.c=context;
        this.items = list;
    }
    // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView activity;
        TextView date;
        TextView locations;
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
        holder.locations=(TextView) convertView.findViewById(R.id.tv_profile_agenda_location);
        // populate the title and image with data for a list item
        holder.activity.setText(items.get(position).activity);
        holder.date.setText(formatTime(items.get(position).date,items.get(position).time));
        holder.locations.setText(items.get(position).location);
        // return the updated view
        return convertView;
    }

    private String formatTime(String dateString, String timeString) {
        String output;
        SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            Date date = formatDate.parse(dateString);
            Date time = formatTime.parse(timeString);
            Date dateCurrent = Calendar.getInstance().getTime();
            int day1 = (int) (date.getTime()/(1000*60*60*24L));
            int day2 = (int) (dateCurrent.getTime()/(1000*60*60*24L));
            int  daysApart = day1-day2;
            if (daysApart<7) {
                if (daysApart<1){
                    if (daysApart<0) output = "Done";                                                   // already been
                    else output = (String) android.text.format.DateFormat.format("HH:mm", time);}   // the same day - show timee
                else output = (String) android.text.format.DateFormat.format("E", date);}                   // within a week - show the day
            else output = (String) android.text.format.DateFormat.format("dd, MMM", date);                  // outside a week - show the date
        } catch (ParseException e) {
            e.printStackTrace();
            output = "error";
        }
        return output;
    }
}