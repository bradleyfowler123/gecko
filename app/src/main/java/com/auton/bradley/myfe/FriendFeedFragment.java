package com.auton.bradley.myfe;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/*
Java file to contain all class' related to the friend feed sub tab
 */



                                // fragment that handles the friend feed sub tab
public class FriendFeedFragment extends Fragment {

    public FriendFeedFragment() {}

    private ArrayList<String> activityDescriptions = new ArrayList<>();
    private ArrayList<String> friendNames = new ArrayList<>();
    private ArrayList<String> timeAgo = new ArrayList<>();
    private ArrayList<String> listItems = new ArrayList<>(); // unique identifier for each list item
    private ArrayList<RequestCreator> picUrls = new ArrayList<>();
    private ListView ff_list;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
                                // function that generates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                                // variable declarations
        View rootView = inflater.inflate(R.layout.fragment_friend_feed, container, false);        // enables easy access to the root search xml
        ff_list = (ListView) rootView.findViewById(R.id.friend_feed_list);                              // locate the list object in the home tab
                                    // get user data
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();           // database data
        MainActivity mActivity = (MainActivity) getActivity();
        Bundle fbData = mActivity.facebookData;                                                     // facebook data
        final ArrayList<String> friendFBNames = fbData.getStringArrayList("friendNames");
                                // if user has friends
        if (!(friendFBNames==null || friendFBNames.isEmpty())) {
                                // get friend data
            final ArrayList<String> friendFBUrls = fbData.getStringArrayList("friendUrls");
            final ArrayList<String> friendUIDs = fbData.getStringArrayList("friendUids");
            //    Log.d("nkklws",friendFBNames.toString());
            //    Log.d("nkklws",friendUIDs.toString());
                                // for each friend
            for (int j = 0; j < friendUIDs.size(); j++) {
                                    // get the friends' agenda data
                final DatabaseReference friend = database.child("users").child(friendUIDs.get(j)).child("Agenda");
                friend.addValueEventListener(new ValueEventListener() {
                    @Override       // upon data return
                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // isolate each agenda item along with which friend it is
                        GenericTypeIndicator<HashMap<String, AgendaClass>> t = new GenericTypeIndicator<HashMap<String, AgendaClass>>() {};
                        HashMap<String, AgendaClass> agendaData = dataSnapshot.getValue(t);              // get agenda data
                        Iterator<AgendaClass> iterator = agendaData.values().iterator();                // parse out a list of friendClass'
                        Iterator<String> keys = agendaData.keySet().iterator();
                        String friendUid = dataSnapshot.getRef().getParent().getKey();                  // get this friend's UID
                        int i = friendUIDs.indexOf(friendUid);                                          // locate the index of where they are in FacebookData
                                        // for each agenda item
                        while (iterator.hasNext()) {
                            String key = keys.next();
                            AgendaClass agendaItem = iterator.next();
                                                // if already exists in list
                            if (listItems.contains(key)) {          // remove it
                                listItems.remove(key);
                                activityDescriptions.remove(agendaItem.activity + " at " + agendaItem.location);
                                timeAgo.remove(agendaItem.date);
                                friendNames.remove(friendFBNames.get(i));
                                picUrls.remove(Picasso.with(getContext()).load(friendFBUrls.get(i)));
                            }               // add agenda item to list
                            listItems.add(key);
                            activityDescriptions.add(agendaItem.activity + " at " + agendaItem.location);
                            timeAgo.add(formatData(agendaItem.date));
                            friendNames.add(friendFBNames.get(i));
                            picUrls.add(Picasso.with(getContext()).load(friendFBUrls.get(i)));
                            // update the list view
                            friendFeedAdapter adapter = new friendFeedAdapter(getActivity(), friendNames, activityDescriptions, timeAgo, picUrls);
                            ff_list.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            ff_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getContext(), "What do you want me to do?", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return rootView;
    }

    private String formatData(String input) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        String output = "error";
        try {
            Date date = format.parse(input);
            //   String week = (String) android.text.format.DateFormat.format("ww", date);
            //   int weekYear = DateFormat.getDateInstance().getCalendar().getWeekYear();
            output = (String) android.text.format.DateFormat.format("dd, MMM", date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output;
    }
}
                                // adapter used for friend's activities list view in friend fees tab
class friendFeedAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
                                // declare variables of this class
    private ArrayList<RequestCreator> profilePics;
    private ArrayList<String> friendNames;
    private ArrayList<String> activityDescriptions;
    private ArrayList<String> timeAgo;
    private Context c;
                                // define a function that can be used to declare this custom adapter class
    friendFeedAdapter(Context context, ArrayList<String> friendNames, ArrayList<String> activityDescriptions, ArrayList<String> timeAgo, ArrayList<RequestCreator> profilePics) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.friend_feed_list_item,friendNames);
        this.c=context;
        this.friendNames=friendNames;
        this.activityDescriptions=activityDescriptions;
        this.timeAgo=timeAgo;
        this.profilePics=profilePics;
    }
                                // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView friends;                                                                             // used to store the relevant views
        TextView activity;
        TextView time;
        ImageView img;
    }
                                // function that generates the list view
    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent){
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup nullParent = null;
            convertView=inflater.inflate(R.layout.friend_feed_list_item,nullParent);
        }
                                // find the views within the list
        final ViewHolder holder=new ViewHolder();
        holder.friends=(TextView) convertView.findViewById(R.id.ff_list_item_friends);
        holder.activity=(TextView) convertView.findViewById(R.id.ff_list_item_activity);
        holder.time=(TextView) convertView.findViewById(R.id.ff_list_item_timeAgo);
        holder.img=(ImageView)  convertView.findViewById(R.id.ff_list_item_image);
                                // populate the title and image with data for a list item
        profilePics.get(position).into(holder.img);
        holder.friends.setText(friendNames.get(position));
        holder.activity.setText(activityDescriptions.get(position));
        holder.time.setText(timeAgo.get(position));
                                // return the updated view
        return convertView;
    }

}
