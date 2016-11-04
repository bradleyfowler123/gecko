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

/*
Java file to contain all class' related to the friend feed sub tab
 */



                                // fragment that handles the friend feed sub tab
public class FriendFeedFragment extends Fragment {

    public FriendFeedFragment() {}

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
        ListView ff_list = (ListView) rootView.findViewById(R.id.friend_feed_list);                              // locate the list object in the home tab
        int[] profilePics={R.drawable.altontowers,R.drawable.climbing,R.drawable.gym,R.drawable.altontowers};    // get the image data to be shown for the recommendations
        final String[] friendNames = getResources().getStringArray(R.array.friendNames);                // get the names of the recommendations to display
        final String[] activityDescriptions = getResources().getStringArray(R.array.activityDescriptions);                // get the names of the recommendations to display
        final String[] timeAgo = getResources().getStringArray(R.array.timeAgo);                // get the names of the recommendations to display
        // populate the list
        friendFeedAdapter adapter = new friendFeedAdapter(getActivity(),friendNames, activityDescriptions, timeAgo ,profilePics);
        ff_list.setAdapter(adapter);
                                // handle clicks on the list items
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
    private int[] profilePics={};
    private String[] friendNames={};
    private String[] activityDescriptions={};
    private String[] timeAgo={};
    private Context c;
                                // define a function that can be used to declare this custom adapter class
    friendFeedAdapter(Context context, String[] friendNames, String[] activityDescriptions, String[] timeAgo, int[] profilePics) {     // arguments set the context, texts and images for this adapter class
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
        ImageView imgs;
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
        holder.imgs=(ImageView)  convertView.findViewById(R.id.ff_list_item_image);
                                // populate the title and image with data for a list item
        holder.imgs.setImageResource(profilePics[position]);
        holder.friends.setText(friendNames[position]);
        holder.activity.setText(activityDescriptions[position]);
        holder.time.setText(timeAgo[position]);
                                // return the updated view
        return convertView;
    }

}
