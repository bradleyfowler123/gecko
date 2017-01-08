package com.auton.bradley.myfe;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 */
public class SettingsActivity extends PreferenceActivity {
    private static boolean[] start = new boolean[3];
    private static boolean fbCon; private static Bundle FacebookData; private static ArrayList<String> friendFirebaseIDs; private static int friendCount;
    private static CallbackManager callbackManager;
    private static int currentTab;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

                            // when a setting is changed
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(final Preference preference, Object value) {
            String stringValue = value.toString();                                                  // the value set
                            // firstly separate by type in order to set summaries
                                        //  all list preferences
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;                        // For list preferences, look up the correct display value in the preference's 'entries' list.
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(                                                              // Set the summary to reflect the new value.
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            }                           // ringtone preference
            else if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty(stringValue)) {                                                // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));
                    if (ringtone == null) {  // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {                // Set the summary to reflect the new ringtone name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {                    // For all other preferences
                preference.setSummary(stringValue);                                                 // set the summary to the value's simple string representation
                Log.d("kidsklx", preference.getKey());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (preference.getKey().equals("email_text")){
                    if (start[0] || stringValue.isEmpty()) {
                        preference.setSummary(user.getEmail());
                        preference.setDefaultValue(user.getEmail());
                        start[0] = false;
                    }
                    else {
                        // update email address
                        final CharSequence backup = preference.getSummary();
                        preference.setSummary(stringValue);
                        preference.setDefaultValue(stringValue);
                        user.updateEmail(stringValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                if (task.isSuccessful()) {
                                    Toast.makeText(preference.getContext(),"Email address updated",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    preference.setSummary(backup);
                                    preference.setDefaultValue(backup);
                                    Toast.makeText(preference.getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                else if (preference.getKey().equals("password_text")){
                    preference.setDefaultValue("*********");
                    preference.setSummary("*********");
                    if (start[1] || stringValue.isEmpty()) {
                        start[1] = false;
                    }
                    else {
                        //update password
                        user.updatePassword(stringValue)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(preference.getContext(),"Password Updated",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(preference.getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }

                }
                else if (preference.getKey().equals("name_text")){
                    if (start[2] || stringValue.isEmpty()) {
                        preference.setSummary(user.getDisplayName());
                        preference.setDefaultValue(user.getDisplayName());
                        start[2] = false;
                    }
                    else {
                        final CharSequence backup = preference.getSummary();
                        preference.setSummary(stringValue);
                        preference.setDefaultValue(stringValue);
                        //update name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(stringValue)
                                //        .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(preference.getContext(),"User profile updated.",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            preference.setSummary(backup);
                                            preference.setDefaultValue(backup);
                                            Toast.makeText(preference.getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }

                }
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override           // setup settings page
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);

        if (getIntent().hasExtra("fbCon")) {
            fbCon = getIntent().getBooleanExtra("fbCon",false);
            currentTab = getIntent().getIntExtra("tab", 0);
        }
        Log.d("reikd", Boolean.toString(fbCon));
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                   // when back is click finish current activity
                finish();
            }
        });
    }

    @Override           // load list
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

                        // This method stops fragment injection in malicious applications. Make sure to deny any unknown fragments here.
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    // This fragment shows general preferences
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            start[0] = true; start[1] = true; start[2] = true;
            bindPreferenceSummaryToValue(findPreference("email_text"));
            bindPreferenceSummaryToValue(findPreference("password_text"));
            bindPreferenceSummaryToValue(findPreference("name_text"));
            final SwitchPreference fbConnected = (SwitchPreference) findPreference("fbCon_switch");
            Log.d("ewdks,m", Boolean.toString(fbCon));
            fbConnected.setChecked(fbCon);
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            fbConnected.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    Log.d("!!!!!!!!11", "dddddddd");
                    if (fbCon) {            // unlink facebook
                        if (user.getProviders().contains("password")) {
                            FirebaseAuth.getInstance().getCurrentUser().unlink("facebook.com")
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                LoginManager.getInstance().logOut();
                                                Toast.makeText(getContext(), "Facebook succesfully unlinked.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getContext(), MainActivity.class);         // start main activity and pass relevant data
                                                intent.putExtra("tab", currentTab);
                                                intent.putExtra("fbConnected", false);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        else {              // do nothing
                            fbConnected.setChecked(fbCon);
                            Toast.makeText(getContext(), "Not possible as facebook is your only sign in method", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {                 // link facebook
                        linkFacebook(user);
                    }
                    return true;
                }
            });
        }

        void linkFacebook(final FirebaseUser user) {
            LoginManager loginManager = LoginManager.getInstance();
            loginManager.logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "email", "user_friends"));
            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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
                            Log.d("gujkjk", friends.names.toString());
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
                            Log.d("yfujbkm", object.toString());
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
                            storeFbFriendsInFirebase();
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

        public void storeFbFriendsInFirebase() {
            // store friends in firebase
            // store users uid and fb id in database lookup table - need not run every time
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference myFacebookIDRef = database.child("facebookIDs").child(FacebookData.getString("id"));
            myFacebookIDRef.setValue(user.getUid());
            // if you have no friends run main activity
            final ArrayList<String> facebookFriendIDs = FacebookData.getStringArrayList("friendIds");
      //      Log.d("yughj", friendFirebaseIDs.toString());
            if (facebookFriendIDs == null || facebookFriendIDs.isEmpty()) {
                DatabaseReference friends = database.child("users").child(user.getUid()).child("friendUIDs");
                friends.removeValue();
                Intent intent = new Intent(getContext(), MainActivity.class);     // if there is facebook data
                intent.putExtra("tab", currentTab);                                     // start main activity and pass relevant data
                intent.putExtra("fbConnected", true);
                intent.putExtra("fbData", FacebookData);
                startActivity(intent);
            }
            else {          // else store friends UIDs in the users friend list on firebase
                // initialise variables
                friendFirebaseIDs = new ArrayList<>();                          // will hold the list of friend UIDs
                for (int i = 0; i < facebookFriendIDs.size(); i++) {
                    friendFirebaseIDs.add(i, "");
                }
                friendCount = facebookFriendIDs.size();
                // for every facebook friend, get their uid
                for (int i = 0; i < facebookFriendIDs.size(); i++) {
                    DatabaseReference firebaseIDRef = database.child("facebookIDs").child(facebookFriendIDs.get(i));
                    firebaseIDRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // store their uid in list
                            int index = facebookFriendIDs.indexOf(dataSnapshot.getKey());
                            friendFirebaseIDs.set(index, dataSnapshot.getValue().toString());
                            friendCount = friendCount - 1;
                            // once all UIDs stored, store friends UIDs in user entry of database
                            if (friendCount == 0) {
                                DatabaseReference friends = database.child("users").child(user.getUid()).child("friendUIDs");
                                friends.setValue(friendFirebaseIDs);
                                FacebookData.putStringArrayList("friendUids", friendFirebaseIDs);
                                //        Log.d("uuhjv", FacebookData.getStringArrayList("friendUids").toString());
                                // start main activity
                                Intent intent = new Intent(getContext(), MainActivity.class);     // if there is facebook data
                                intent.putExtra("tab", currentTab);                                     // start main activity and pass relevant data
                                intent.putExtra("fbConnected", true);
                                intent.putExtra("fbData", FacebookData);
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



        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    //This fragment shows notification preferences only
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    // This fragment shows data and sync preferences
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
