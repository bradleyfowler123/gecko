package com.auton.bradley.myfe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ProfileFriendsListFragment extends Fragment {

    public ProfileFriendsListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // function that generates the view
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile_friends, container, false);        // enables easy access to the root search xml
        ListView pa_list = (ListView) rootView.findViewById(R.id.profile_friend_list);             // locate the list object in the home tab
                                // variable declarations
        final Bundle fbData;
        final ArrayList<String> friendNames;
        final ArrayList<String> friendUids;
        final ArrayList<String> profilePicUrls;
        final ArrayList<String> interested;
                                // handle whether fragment was called by profile tab or friend activity
        if (getActivity().getLocalClassName().equals("FriendActivity")) {                           // if friend activity
            final FriendActivity activity = (FriendActivity) getActivity();                         // get users friend list
            friendNames = activity.names;
            friendUids = activity.UIDs;
            profilePicUrls = activity.Urls;
            interested = activity.Interested;
        }
        else {                                                                                      // if main activity
            final MainActivity activity = (MainActivity) getActivity();                             // get users friend list
            fbData = activity.facebookData;
            friendNames = activity.facebookData.getStringArrayList("friendNames");
            friendUids = activity.facebookData.getStringArrayList("friendUids");
            profilePicUrls = activity.facebookData.getStringArrayList("friendUrls");
            interested = activity.interested;
        }
                                // populate friends list
        friendsListAdapter adapter = new friendsListAdapter(getActivity(),friendNames,profilePicUrls);
        pa_list.setAdapter(adapter);
                                // when friend is clicked up
        pa_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {                               // navigate to their page
                //start new activity, pass friendUID, friendFBFirstName, profilePic
                Intent intent = new Intent(getContext(), FriendActivity.class);     // if there is facebook data
                intent.putStringArrayListExtra("uid", friendUids);
                intent.putStringArrayListExtra("name", friendNames);
                intent.putStringArrayListExtra("url", profilePicUrls);
                intent.putStringArrayListExtra("interests", interested);
                intent.putExtra("index", i);
                startActivity(intent);
            }
        });

        return rootView;
    }
}

// adapter used for to show your activities as a list view in profile agenda sub tab
class friendsListAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private ArrayList<String> names;
    private ArrayList<String> profilePics;
    private Context c;
    // define a function that can be used to declare this custom adapter class
    friendsListAdapter(Context context, ArrayList<String> names, ArrayList<String> profilePicUrls) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.profile_friends_list_item, names);
        this.c = context;
        this.names = names;
        this.profilePics = profilePicUrls;
    }
    // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView name;
        ImageView profilePic;
    }
    // function that generates the list view
    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup nullParent = null;
            convertView = inflater.inflate(R.layout.profile_friends_list_item, nullParent);
        }
        // find the views within the list
        final ViewHolder holder = new ViewHolder();
        holder.name = (TextView) convertView.findViewById(R.id.tv_profile_friends_name);
        holder.profilePic = (ImageView) convertView.findViewById(R.id.profile_friend_list_item_profilePic);
        // populate the title and image with data for a list item
        holder.name.setText(names.get(position));
        Picasso.with(getContext()).load(profilePics.get(position)).centerCrop().resize(100,100).into(holder.profilePic);
        // return the updated view
        return convertView;
    }

}
