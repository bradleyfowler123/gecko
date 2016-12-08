package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;


// fragment that handles the friends tab
public class FriendFragment extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;
    View rootView;
    LoginButton fbLinkButton;
    Bundle FacebookData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                                // get friend tab layout
        rootView = inflater.inflate(R.layout.fragment_friend, container, false);
        TextView signUp = (TextView) rootView.findViewById(R.id.friend_signUp_text);
        final View LoggedInView = rootView.findViewById(R.id.friend_LoggedIn);
        fbLinkButton = (LoginButton) rootView.findViewById(R.id.connect_with_fb_button);
                                // get user info
        final MainActivity activity = (MainActivity) getActivity();
        final FirebaseUser user = activity.auth.getCurrentUser();
        Boolean fbCon = activity.facebookConnected;
        Log.d("refdvrfd",fbCon.toString());

        if(user != null) {
            if (fbCon) {
                signUp.setVisibility(View.GONE);
                fbLinkButton.setVisibility(View.GONE);
                LoggedInView.setVisibility(View.VISIBLE);
                // load tab bar and tab data into friend layout
                viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_friend);
                setupViewpagerChild(viewPager);
                tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout_friend);
                tabLayout.setupWithViewPager(viewPager);
                setupTabTitles();
            }
            else {
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
                                JSONArray friends = getFacebookFriends(accessToken);                        // make separate request for friend list of friends using myfe
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
        else {
            signUp.setVisibility(View.VISIBLE);
            fbLinkButton.setVisibility(View.GONE);
            LoggedInView.setVisibility(View.GONE);
        }

                                // return the view
        return rootView;
    }

    public void setupViewpagerChild(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FriendFeedFragment(), "Feed");
        adapter.addFragment(new FriendMapFragment(), "Map");
        viewPager.setAdapter(adapter);
    }

    private void setupTabTitles() {
        try {
            tabLayout.getTabAt(0).setText(getString(R.string.friendFeed_tabName));
            tabLayout.getTabAt(1).setText(getString(R.string.friendMap_tabName));
        }catch (NullPointerException e){
            e.printStackTrace();

        }
    }

    public JSONArray getFacebookFriends(AccessToken accessToken) {
        final JSONArray[] friendData = {null};
        new GraphRequest(
                accessToken,
                "/"+ accessToken.getUserId() +"/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        String friends = "";
                        try {
                            friendData[0] = object.getJSONArray("data");
                            friends = object.getJSONObject("summary").getString("total_count");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("bjdsnxn", friends);
                    }
                }
        ).executeAsync();
        return friendData[0];
    }
    // function to get all of the data
    public Bundle getFacebookData(JSONObject object, JSONArray friends) {
        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            Log.d("jewnjhc ds",object.toString());
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
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
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
            if (friends!=null)
                bundle.putString("friends", friends.toString());
            else
                bundle.putString("friends", "0");

            return bundle;
        }
        catch(JSONException e) {
            Log.d("idk","Error parsing JSON");
            return null;
        }
    }

}