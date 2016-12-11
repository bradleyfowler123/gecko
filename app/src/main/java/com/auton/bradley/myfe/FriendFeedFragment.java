package com.auton.bradley.myfe;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.util.ArrayList;

/*
Java file to contain all class' related to the friend feed sub tab
 */



                                // fragment that handles the friend feed sub tab
public class FriendFeedFragment extends Fragment {

    public FriendFeedFragment() {}

    private ArrayList<String> activityDescriptions = new ArrayList<>();
    private ArrayList<String> friendNames = new ArrayList<>();
    private ArrayList<String> timeAgo = new ArrayList<>();
    private ArrayList<RequestCreator> picUrls = new ArrayList<>();
    private String friendFirebaseID;
    private ListView ff_list;
    private int i;
    private String friendFireRef;


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
        ArrayList<String> friendFBIds = fbData.getStringArrayList("friendIds");
        final ArrayList<String> friendFBNames = fbData.getStringArrayList("friendNames");
        final ArrayList<String> friendFBUrls = fbData.getStringArrayList("friendUrls");
                                // for every facebook friend
        i = -1;
        for (int j = 0; j < friendFBIds.size(); j++) {
                                // get their firebase id
            DatabaseReference firebaseIDRef = database.child("facebookIDs").child(friendFBIds.get(j));
            firebaseIDRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    friendFirebaseID = dataSnapshot.getValue().toString();
                                // get friends agenda data
                    final DatabaseReference friend = database.child("users").child(friendFirebaseID).child("Agenda");
                    friend.addChildEventListener(new ChildEventListener() {
                        @Override       // when data is returned, and on every addition
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            String friendRef = dataSnapshot.getRef().getParent().getParent().toString();
                            if (!friendRef.equals(friendFireRef)) {
                                friendFireRef = friendRef;
                                i = i+1;
                            }
                            dataSnapshot.getRef().getParent().getParent().toString();
                                            // add an agenda items to current list
                            friendClass friendFirebaseData = dataSnapshot.getValue(friendClass.class);
                            activityDescriptions.add(friendFirebaseData.activity + " at " + friendFirebaseData.company);
                            timeAgo.add(friendFirebaseData.date);
                            friendNames.add(friendFBNames.get(i));
                            picUrls.add(Picasso.with(getContext()).load(friendFBUrls.get(i)));
                                            // update the list view
                            friendFeedAdapter adapter = new friendFeedAdapter(getActivity(),friendNames, activityDescriptions, timeAgo, picUrls);
                            ff_list.setAdapter(adapter);
                        }           // when data is changed is other ways
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {}
                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { Log.d("Error FFF1", databaseError.toString());}
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
