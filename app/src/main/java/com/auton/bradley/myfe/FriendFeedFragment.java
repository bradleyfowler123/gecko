package com.auton.bradley.myfe;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final ArrayList<String> friendFBNames = fbData.getStringArrayList("friendNames");
        final ArrayList<String> friendFBUrls = fbData.getStringArrayList("friendUrls");
        final ArrayList<String> friendUIDs = fbData.getStringArrayList("friendUids");
        Log.d("nkklws",friendFBNames.toString());
        Log.d("nkklws",friendUIDs.toString());
                    // get friends data
                          // for each friend
        for (int j = 0; j < friendUIDs.size(); j++) {
                            // get the friends' agenda data
            final DatabaseReference friend = database.child("users").child(friendUIDs.get(j)).child("Agenda");
            friend.addValueEventListener(new ValueEventListener() {
                @Override       // upon data return
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<HashMap<String,friendClass>> t = new GenericTypeIndicator<HashMap<String,friendClass>>() {};
                    HashMap<String,friendClass> agendaData = dataSnapshot.getValue(t);              // get agenda data
                    Iterator<friendClass> iterator = agendaData.values().iterator();                // parse out a list of friendClass'
                    Iterator<String> keys = agendaData.keySet().iterator();
                    String friendUid = dataSnapshot.getRef().getParent().getKey();                  // get this friend's UID
                    int i = friendUIDs.indexOf(friendUid);                                          // locate the index of where they are in FacebookData
                                    // for each agenda item
                    while (iterator.hasNext()) {
                        String key = keys.next();
                        friendClass agendaItem = iterator.next();
                        if (listItems.contains(key)) {
                            activityDescriptions.remove(agendaItem.activity + " at " + agendaItem.company);
                            timeAgo.remove(agendaItem.date);
                            friendNames.remove(friendFBNames.get(i));
                            picUrls.remove(Picasso.with(getContext()).load(friendFBUrls.get(i)));
                        }
                        listItems.add(key);
                        activityDescriptions.add(agendaItem.activity + " at " + agendaItem.company);
                        timeAgo.add(agendaItem.date);
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
                Toast.makeText(getContext(),"What do you want me to do?",Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
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
        profilePics.get(position).transform(new CircleTransform()).into(holder.img);
        holder.friends.setText(friendNames.get(position));
        holder.activity.setText(activityDescriptions.get(position));
        holder.time.setText(timeAgo.get(position));
                                // return the updated view
        return convertView;
    }

}
