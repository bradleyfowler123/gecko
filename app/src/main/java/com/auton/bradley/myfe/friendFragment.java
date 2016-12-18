package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;


// fragment that handles the friends tab
public class FriendFragment extends Fragment {
    View rootView;
    LoginButton fbLinkButton;
    Bundle FacebookData;
    private ArrayList<AgendaClass> listItemsData = new ArrayList<>();
    private ArrayList<String> activityDescriptions = new ArrayList<>();
    private ArrayList<String> friendNames = new ArrayList<>();
    private ArrayList<String> timeAgo = new ArrayList<>();
    private ArrayList<String> listItems = new ArrayList<>(); // unique identifier for each list item
    private ArrayList<RequestCreator> picUrls = new ArrayList<>();
    private ListView ff_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                                // get friend tab layout
        rootView = inflater.inflate(R.layout.fragment_friend, container, false);
        TextView signUp = (TextView) rootView.findViewById(R.id.friend_signUp_text);
        final View LoggedInView = rootView.findViewById(R.id.friend_feed_list);
        fbLinkButton = (LoginButton) rootView.findViewById(R.id.connect_with_fb_button);
                                // get user info
        final MainActivity activity = (MainActivity) getActivity();
        final FirebaseUser user = activity.auth.getCurrentUser();
        Boolean fbCon = activity.facebookConnected;
                            // if user signed in
        if(user != null) {
                            // if fb connected - show fb screen
            if (fbCon) {
                signUp.setVisibility(View.GONE);
                fbLinkButton.setVisibility(View.GONE);
                LoggedInView.setVisibility(View.VISIBLE);
                // load tab bar and tab data into friend layout
                populateList();
            }
            else {         // else show connect fb screen
                signUp.setVisibility(View.VISIBLE);
                signUp.setText("Link Facebook to see your friends!");
                fbLinkButton.setVisibility(View.VISIBLE);
                LoggedInView.setVisibility(View.GONE);

                // handle facebook login button press
                fbLinkButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
                // on click, login to facebook
                fbLinkButton.registerCallback(activity.callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        final AccessToken accessToken = loginResult.getAccessToken();                       // get the access token
                        Log.i("fbAccessToken", accessToken.getToken());
                        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());  // generate firebase credential
                                                // make request for facebook user information
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override           // when data is returned
                            public void onCompleted(JSONObject object, GraphResponse response2) {
                                FacebookFriendData friends = getFacebookFriends(accessToken);                        // make separate request for friend list of friends using myfe
                                FacebookData = getFacebookData(object, friends);                            // format the data into a nice bundle
                                if(FacebookData.getString("email").equals(user.getEmail())) {
                                    user.linkWithCredential(credential);
                                    Toast.makeText(getContext(),"FB Linked",Toast.LENGTH_SHORT).show();
                                                    // restart main activity to display new user state
                                    Intent intent = new Intent();
                                    intent.putExtra("tab", 3);
                                    intent.putExtra("fbConnected", true);
                                    intent.putExtra("fbData", FacebookData);
                                    activity.setIntent(intent);
                                    activity.recreate();
                                }
                                else {
                                    LoginManager.getInstance().logOut();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage("Error - Your Myfe and Facebook email addresses must match. Please change one of them in their respective settings or alternatively create a new Myfe account by using the Login with Facebook Button on the Myfe Login Page.")
                                            .setNegativeButton("Okay", null)
                                            .create()
                                            .show();
                                }
                            }
                        });
                        // send the request for data
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }
                    @Override
                    public void onCancel() {
                    }
                    @Override
                    public void onError(FacebookException error) {
                    }
                });
            }
        }
        else {          // otherwise show sign up screen
            signUp.setVisibility(View.VISIBLE);
            fbLinkButton.setVisibility(View.GONE);
            LoggedInView.setVisibility(View.GONE);
        }
                                // return the view
        return rootView;
    }
                            // function to get users friends info
    public FacebookFriendData getFacebookFriends(AccessToken accessToken) {
        final FacebookFriendData friendData = new FacebookFriendData();
        new GraphRequest(
                accessToken,
                "/"+ accessToken.getUserId() +"/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        String friendsNo = "";
                        try {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject aFriend = (JSONObject) jsonArray.get(i);
                                friendData.names.add(i,aFriend.getString("name"));
                                friendData.ids.add(i,aFriend.getString("id"));
                                friendData.picUrls.add(i,"https://graph.facebook.com/" + aFriend.getString("id") + "/picture?width=200&height=150");
                            }
                            friendsNo = object.getJSONObject("summary").getString("total_count");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("You have friends", friendsNo);
                    }
                }
        ).executeAsync();
        return friendData;
    }
                                // function to format all of the data
    public Bundle getFacebookData(JSONObject object, FacebookFriendData friends) {
        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            bundle.putString("id", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email")){ bundle.putString("email", object.getString("email"));}
            else { bundle.putString("email", id + "@facebook.com");}                                // for users who signed upto facebook with mobile
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
            if (friends!=null) {
                bundle.putStringArrayList("friendNames", friends.names);
                bundle.putStringArrayList("friendIds", friends.ids);
                bundle.putStringArrayList("friendUrls", friends.picUrls);}

            return bundle;
        }
        catch(JSONException e) {
            Log.d("idk","Error parsing JSON");
            return null;
        }
    }
                                // get friend feed data and populate list
    void populateList() {
                                // get users friends
        ff_list = (ListView) rootView.findViewById(R.id.friend_feed_list);                          // locate the list object in the home tab
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();           // database data
        MainActivity mActivity = (MainActivity) getActivity();
        Bundle fbData = mActivity.facebookData;                                                     // facebook data
        final ArrayList<String> friendFBNames = fbData.getStringArrayList("friendNames");
                                // if user has friends
        if (!(friendFBNames==null || friendFBNames.isEmpty())) {
                                    // get friend data
            final ArrayList<String> friendFBUrls = fbData.getStringArrayList("friendUrls");
            final ArrayList<String> friendUIDs = fbData.getStringArrayList("friendUids");
                                    // for each friend
            for (int j = 0; j < friendUIDs.size(); j++) {
                                            // get the friends' agenda data
                final DatabaseReference friend = database.child("users").child(friendUIDs.get(j)).child("Agenda");
                friend.addValueEventListener(new ValueEventListener() {
                    @Override               // upon data return
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
                                listItemsData.remove(listItems.indexOf(key));
                                activityDescriptions.remove(listItems.indexOf(key));
                                timeAgo.remove(listItems.indexOf(key));  // today - date,time
                                friendNames.remove(listItems.indexOf(key));
                                picUrls.remove(listItems.indexOf(key));
                                listItems.remove(key);
                            }               // add agenda item to list
                            listItems.add(key);
                            listItemsData.add(agendaItem);
                            activityDescriptions.add(agendaItem.activity + ", Cambridge");
                            timeAgo.add(formatTime(agendaItem.date,agendaItem.time));
                            friendNames.add(friendFBNames.get(i));
                            picUrls.add(Picasso.with(getContext()).load(friendFBUrls.get(i)).centerCrop().resize(100,100));
                            // update the list view
                            friendAdapter adapter = new friendAdapter(getActivity(), friendNames, activityDescriptions, timeAgo, picUrls);
                            ff_list.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
                                // upon list item click
            ff_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    // show detailed view with what friend is doing at bottom
                    Intent intent = new Intent(getActivity(),DetailedItemActivity.class);
                    intent.putExtra("from", "friendFeed");
                    AgendaClass listItem = listItemsData.get(i);
                    intent.putExtra("ref",listItem.ref);
                    intent.putExtra("friendDate",listItem.date);
                    intent.putExtra("friendTime",listItem.time);
                    intent.putExtra("friendName", friendFBNames.get(i));
                    intent.putExtra("friendUrl", friendFBUrls.get(i));
                    startActivity(intent);
            //        Toast.makeText(getContext(), listItemsData.get(i).activity, Toast.LENGTH_SHORT).show(); // show detailed activity view
                }
            });
        }
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
                if (daysApart<1) output = (String) android.text.format.DateFormat.format("HH:mm", time);   // the same day - show timee
                else output = (String) android.text.format.DateFormat.format("E", date);}                   // within a week - show the day
            else output = (String) android.text.format.DateFormat.format("dd, MMM", date);                  // outside a week - show the date
        } catch (ParseException e) {
            e.printStackTrace();
            output = "error";
        }
        return output;
    }
}



// adapter used for friend's activities list view in friend fees tab
class friendAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private ArrayList<RequestCreator> profilePics;
    private ArrayList<String> friendNames;
    private ArrayList<String> activityDescriptions;
    private ArrayList<String> timeAgo;
    private Context c;
    // define a function that can be used to declare this custom adapter class
    friendAdapter(Context context, ArrayList<String> friendNames, ArrayList<String> activityDescriptions, ArrayList<String> timeAgo, ArrayList<RequestCreator> profilePics) {     // arguments set the context, texts and images for this adapter class
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
