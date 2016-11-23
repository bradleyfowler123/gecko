package com.auton.bradley.myfe;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileFriendsFragment extends Fragment {

    public ProfileFriendsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // function that generates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile_friends, container, false);        // enables easy access to the root search xml
        ListView pa_list = (ListView) rootView.findViewById(R.id.profile_friend_list);             // locate the list object in the home tab

        RequestCreator picURLs[] = {Picasso.with(getContext()).load(R.drawable.altontowers),Picasso.with(getContext()).load("http://www.freeiconspng.com/uploads/profile-icon-1.png"),Picasso.with(getContext()).load("http://www.freeiconspng.com/uploads/profile-icon-1.png"),Picasso.with(getContext()).load("http://www.freeiconspng.com/uploads/profile-icon-1.png")};
        ArrayList<String> activities = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        activities.add("Bradley Fowler");
        dates.add("is going go-karting tomorrow");

        MainActivity activity = (MainActivity) getActivity();
        if (!activity.user.fbUserId.equals("")) {

                           /* make the API call */
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + activity.user.fbUserId + "/friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {

                            Log.i("unique!!",response.toString());

                            /* handle the result */
                        }


                    }
            ).executeAsync();


        }




        profileFriendsAdapter adapter = new profileFriendsAdapter(getActivity(),activities,dates,picURLs);
        pa_list.setAdapter(adapter);

        return rootView;
    }
}


// adapter used for to show your activities as a list view in profile agenda sub tab
class profileFriendsAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private ArrayList<String> names;
    private ArrayList<String> nextActivities;
    private RequestCreator[] profilePics;
    private Context c;

    // define a function that can be used to declare this custom adapter class
    profileFriendsAdapter(Context context, ArrayList<String> names, ArrayList<String> nextActivities, RequestCreator[] profilePics) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.profile_friends_list_item, names);
        this.c = context;
        this.names = names;
        this.nextActivities = nextActivities;
        this.profilePics = profilePics;
    }

    // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView name;
        TextView nextActivity;
        ImageView img;
    }

    // function that generates the list view
    @Override
    public
    @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup nullParent = null;
            convertView = inflater.inflate(R.layout.profile_friends_list_item, nullParent);
        }
        // find the views within the list
        final ViewHolder holder = new ViewHolder();
        holder.name = (TextView) convertView.findViewById(R.id.tv_profile_friends_name);
        holder.nextActivity = (TextView) convertView.findViewById(R.id.tv_profile_friends_nextActivity);
        holder.img = (ImageView) convertView.findViewById(R.id.profile_friend_list_item_profilePic);
        // populate the title and image with data for a list item
        holder.name.setText(names.get(position));
        holder.nextActivity.setText(nextActivities.get(position));
        profilePics[position].transform(new CircleTransform()).into(holder.img);
        // return the updated view
        return convertView;
    }

}
