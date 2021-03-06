package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import com.facebook.AccessToken;
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
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/*
    fragment that handles the friend feed tab
 */

public class FriendFragment extends Fragment {
                    // global variable declarations
    View rootView;
    LoginButton fbLinkButton;
    Bundle FacebookData;
    public ListView ff_list;
    private friendAdapter adapter;
    private ArrayList<AgendaClass> sortedList = new ArrayList<>();
    private ArrayList<String> listItems = new ArrayList<>(); // some necessary crap
    private HashMap<String, Boolean> friendFirebaseIDs;
    private ArrayList<String> friendFirebaseIDsOnly;
    private int friendCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

                                // get friend tab layout views
        rootView = inflater.inflate(R.layout.fragment_friend, container, false);
        TextView signUp = (TextView) rootView.findViewById(R.id.friend_signUp_text);
        fbLinkButton = (LoginButton) rootView.findViewById(R.id.connect_with_fb_button);
        ff_list = (ListView) rootView.findViewById(R.id.friend_feed_list);

                                // get user info
        final MainActivity activity = (MainActivity) getActivity();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            // if user signed in
        if(user != null) {
            Boolean fbCon = activity.facebookConnected;
                            // if fb connected - show fb screen
            if (fbCon) {
                                // show correct views
                signUp.setVisibility(View.GONE);
                fbLinkButton.setVisibility(View.GONE);
                ff_list.setVisibility(View.VISIBLE);
                ff_list.setEmptyView(rootView.findViewById(R.id.friend_empty_list_item));
                                // populate list
                friendAdapter adapter = new friendAdapter(getActivity(), sortedList, listItems);
                ff_list.setAdapter(adapter);
                                // on list item click show detailed activity view
                ff_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // show detailed view with what friend is doing at bottom
                        Intent intent = new Intent(getActivity(),DetailedItemActivity.class);
                        intent.putExtra("from", "friendFeed");
                        AgendaClass listItem = sortedList.get(i);
                        intent.putExtra("ref",listItem.ref);
                        intent.putExtra("event", listItem.event);
                        intent.putExtra("friendDate",listItem.date);
                        intent.putExtra("friendTime",listItem.time);
                        intent.putExtra("friendName", listItem.friendName);
                        intent.putExtra("friendUrl", listItem.picUrl);
                        intent.putExtra("interests", activity.interested);
                        startActivity(intent);
                    }
                });
            }
            else {         // else show connect fb screen
                signUp.setVisibility(View.VISIBLE);
                signUp.setText(R.string.friendFeed_linkFb);
                fbLinkButton.setVisibility(View.VISIBLE);
                ff_list.setVisibility(View.GONE);

                // handle facebook login button press
                fbLinkButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
                // on click, login to facebook
                fbLinkButton.registerCallback(activity.callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        final AccessToken accessToken = loginResult.getAccessToken();                       // get the access token
                        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());  // generate firebase credential
                                                // make request for facebook user information
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override           // when data is returned
                            public void onCompleted(JSONObject object, GraphResponse response2) {
                                FacebookFriendData friends = getFacebookFriends(accessToken);                        // make separate request for friend list of friends using myfe
                                FacebookData = getFacebookData(object, friends);                            // format the data into a nice bundle
                                //noinspection ConstantConditions
                                if(FacebookData.getString("email").equals(user.getEmail())) {
                                    user.linkWithCredential(credential);
                                    Toast.makeText(getContext(),"FB Linked",Toast.LENGTH_SHORT).show();
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
            ff_list.setVisibility(View.GONE);
        }
                                // return the view
        return rootView;
    }

                                // function to update the list items shown in the friend feed
    public void storeData(ArrayList<AgendaClass> sortedList2, ArrayList<String> strings) {
        if (strings != null && strings.size() > 0) {
            sortedList = sortedList2;
            listItems = strings; // some necessary crap
            if (ff_list != null) {
                if (adapter == null) {     // create new adapter to generate list
                    adapter = new friendAdapter(getActivity(), sortedList, listItems);
                    ff_list.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }    // update current adapter (list)

            }
        }
    }


    // below all needed for when user first links accounts so very similar to login code below
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
                        try {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject aFriend = (JSONObject) jsonArray.get(i);
                                friendData.names.add(i,aFriend.getString("name"));
                                friendData.ids.add(i,aFriend.getString("id"));
                                friendData.picUrls.add(i,"https://graph.facebook.com/" + aFriend.getString("id") + "/picture?width=200&height=150");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FacebookData.putStringArrayList("friendNames",friendData.names);
                        FacebookData.putStringArrayList("friendIds",friendData.ids);
                        FacebookData.putStringArrayList("friendUrls",friendData.picUrls);
                        storeFbFriendsInFirebase();
                    }
                }
        ).executeAsync();
        return friendData;
    }

    public void storeFbFriendsInFirebase() {
        // store friends in firebase
        // store users uid and fb id in database lookup table - need not run every time
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        @SuppressWarnings("ConstantConditions") DatabaseReference myFacebookIDRef = database.child("facebookIDs").child(FacebookData.getString("id"));
        assert user != null;
        myFacebookIDRef.setValue(user.getUid());

                // if you have no friends run main activity
        final ArrayList<String> facebookFriendIDs = FacebookData.getStringArrayList("friendIds");
        if (facebookFriendIDs == null || facebookFriendIDs.isEmpty()) {
            DatabaseReference friends = database.child("users").child(user.getUid()).child("friendUIDs");
            friends.removeValue();
            // restart main activity to display new user state
            MainActivity activity = (MainActivity) getActivity();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("tab", 1);
            intent.putExtra("fbConnected", true);
            intent.putExtra("fbData", FacebookData);
            activity.finish();
            startActivity(intent);
        }

                // else store friends UIDs in the users friend list on firebase
        else {
            // initialise variables
            friendCount = facebookFriendIDs.size();
            friendFirebaseIDs = new HashMap<>();                          // will hold the list of friend UIDs
            friendFirebaseIDsOnly =  new ArrayList<>(Collections.nCopies(friendCount, " "));


            // for every facebook friend, get their uid
            for (int i = 0; i < facebookFriendIDs.size(); i++) {
                DatabaseReference firebaseIDRef = database.child("facebookIDs").child(facebookFriendIDs.get(i));
                firebaseIDRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        friendCount = friendCount - 1;
                        // store their uid in list
                        if (dataSnapshot.getValue() != null) {
                            String theUID = dataSnapshot.getValue().toString();
                            int index = facebookFriendIDs.indexOf(dataSnapshot.getKey());            // the facebook id
                            friendFirebaseIDsOnly.set(index, theUID);
                            friendFirebaseIDs.put(theUID, true);

                        }
                        // once all UIDs stored, store friends UIDs in user entry of database
                        if (friendCount == 0) {
                            DatabaseReference friends = database.child("users").child(user.getUid()).child("friendUIDs");
                            friends.setValue(friendFirebaseIDs);
                            FacebookData.putStringArrayList("friendUids", friendFirebaseIDsOnly);

                            // restart main activity to display new user state
                            MainActivity activity = (MainActivity) getActivity();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("tab", 1);
                            intent.putExtra("fbConnected", true);
                            intent.putExtra("fbData", FacebookData);
                            activity.finish();
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
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
            FirebaseCrash.report(e);
            return null;
        }
    }

}



// adapter used for friend's activities list view in friend fees tab
class friendAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private ArrayList<AgendaClass> items;
    private Context c;
    // define a function that can be used to declare this custom adapter class
    friendAdapter(Context context, ArrayList<AgendaClass> agendaClassArrayList, ArrayList<String> idk) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.friend_feed_list_item,idk);
        this.c=context;
        this.items=agendaClassArrayList;
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
        try {
            Picasso.with(getContext()).load(items.get(position).picUrl).centerCrop().resize(100, 100).placeholder(R.drawable.ic_profilepic).error(R.drawable.ic_profilepic).into(holder.img);
            holder.friends.setText(items.get(position).friendName);
            holder.activity.setText(items.get(position).activity);
            holder.time.setText(items.get(position).timeAgo);
        }
        catch (Exception e) {
            Log.d("Exception Friend Frag", "1254");
        }

        // return the updated view
        return convertView;
    }
}
