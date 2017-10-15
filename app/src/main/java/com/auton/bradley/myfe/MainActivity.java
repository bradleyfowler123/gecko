package com.auton.bradley.myfe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
                                    // global variable declarations
    Toolbar toolbar;
    private TabLayout tabLayout;
    ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_home_grey,
            R.drawable.ic_friend_feed_grey,
            R.drawable.ic_planner
    };
    public Bundle facebookData;
    public Boolean facebookConnected;
    public CallbackManager callbackManager;
    public FirebaseAuth auth;
    public FirebaseUser user;
    int currentTab = 0;
    String location = "United Kingdom%%England%%Cambridgeshire%%Cambridge"; Location currentLoc;

    private FriendFragment friendFragment = new FriendFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private HomeFragment homeFragment = new HomeFragment();
                                            // contains data to display
    private ArrayList<AgendaClass> friendFeedListItemsData = new ArrayList<>();
    public ArrayList<AgendaClass> friendFeedSortedList = new ArrayList<>();
    private ArrayList<String> friendFeedListItems = new ArrayList<>(); // some necessary crap
    private ArrayList<AgendaClass> homeListItems = new ArrayList<>();                                // contains all of the data for all of the activities in cambridge
    private ArrayList<AgendaClass> filteredHomeListItems = new ArrayList<>();
    private ArrayList<String> filteredHomeListTitles = new ArrayList<>();
    private ArrayList<String> homeListRefs = new ArrayList<>();
    private ArrayList<String> homeListTitles = new ArrayList<>();                                         // stores all of the titles, used to filter results with search
    private Map<String, ArrayList<String>> activityFriendGoingNumbers = new HashMap<>();
    private Map<String, ArrayList<String>> activityFriendGoingNumbersHolder = new HashMap<>();
    private Map<String, ArrayList<String>> activityFriendInterestedNumbers = new HashMap<>();
    public ArrayList<String> interested = new ArrayList<>();
    private AgendaClass[] unseenHomeUpdates = new AgendaClass[20];
    private int eventCount = 0; private int activityCount = 0;
    private int eventCount2 = 0; private int activityCount2 = 0;
    private int indexOfLastActivity = 0, indexOfLastEvent = 0;
    private int eventCountDatabase = 0; private int activityCountDatabase = 0;
    private String lastActivityRef;
    private int activities_to_load;
    private int activity_random_start;
                                    // static variables
    public static final String PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        callbackManager = CallbackManager.Factory.create();

        // check permissions granted - if one is not add it to a list to request from user
        ArrayList<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        }               // request permissions
        if (permissions.size() != 0) {
            String[] Sarray = permissions.toArray(new String[permissions.size()]);
            ActivityCompat.requestPermissions(this, Sarray, 13);
        }
                // get current location
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            currentLoc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);}

        // load main activity layout
        setContentView(R.layout.activity_main);                                                     // load the main activity view// add icons to ta
        toolbar = (Toolbar) findViewById(R.id.toolbar);   // load action bars                                          // enable the action bar (above tabbed menus)
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.container);

        // get shared prefs
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        lastActivityRef = prefs.getString("lastActivityRef", null);

        // check if firebase is signed in
        if (user != null) {     // if user signed into firebase
            getNSetUserData();      // get user's data from firebase
            // check to see if we have their other data
            if (getIntent().hasExtra("fbConnected")) {  // if so login started this activity or preferences set
                // get user's social data from intent
                Intent intent = getIntent();
                currentTab = intent.getIntExtra("tab", 0);
                facebookConnected = intent.getBooleanExtra("fbConnected", false);   // if they have connected facebook

                // set home feed data and then friend data
                setupViewPager(viewPager);
                tabLayout = (TabLayout) findViewById(R.id.tabs);                                            // find tab layout
                tabLayout.setupWithViewPager(viewPager);                                                    // setup view
                setupTabIcons();
                getCounts();
                if (facebookConnected) {
                    facebookData = intent.getBundleExtra("fbData");
                    getNSetFriendData();    // get friends info and show it on home feed
                }
            }

            else {                    // if we have lost all the data just log tem out

                LoginManager.getInstance().logOut();
                facebookConnected = false;
                auth.signOut();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("tab", 0);
                startActivity(intent);
            }
        }

        else {                  // user is not signed in
            // ensure they are fully logged out
            LoginManager.getInstance().logOut();
            facebookConnected = false;
            auth.signOut();

                                // if they were already on the app (returning from e.g. settings page), return them to the correct tab
            if (getIntent().hasExtra("tab")) {
                setupViewPager(viewPager);
                tabLayout = (TabLayout) findViewById(R.id.tabs);                                            // find tab layout
                tabLayout.setupWithViewPager(viewPager);                                                    // setup view
                setupTabIcons();
                getCounts();
            }

                                // otherwise they are logged out and just opened app, so show them login screen
            else {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("tab", 0);
                startActivity(intent);
            }
        }
        viewPager.setCurrentItem(currentTab);
    }

    @Override
    protected void onStop(){
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("lastActivityRef", lastActivityRef);
        // Commit the edits!
        editor.apply();
    }

/*
    functions to handle screen rotations etc. Although not allowed
 */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        homeFragment = (HomeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "homeFragment");
        friendFragment = (FriendFragment) getSupportFragmentManager().getFragment(savedInstanceState, "friendFragment");
        profileFragment = (ProfileFragment) getSupportFragmentManager().getFragment(savedInstanceState, "profileFragment");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (friendFragment != null && friendFragment.isAdded())
            getSupportFragmentManager().putFragment(outState, "friendFragment", friendFragment);
        if (homeFragment != null && homeFragment.isAdded())
            getSupportFragmentManager().putFragment(outState, "homeFragment", homeFragment);
        if (profileFragment != null && profileFragment.isAdded())
            getSupportFragmentManager().putFragment(outState, "profileFragment", profileFragment);
        super.onSaveInstanceState(outState);
    }

 /*
    function that runs when add to calendar button clicked and data returned
    plus when home feed preferences are set
 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);                            // Pass the activity result back to the Facebook SDK

        if (requestCode == 11) {    // add to calendar button returns data
            // Make sure the request was successful
            if (resultCode == 1) {
                Snackbar snackbar = Snackbar
                        .make(viewPager, "Added to calendar", Snackbar.LENGTH_SHORT);
                snackbar.show();
                if (user != null) {       // upload selection to their agenda
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference agendaItem = database.child("users").child(user.getUid()).child("Agenda").push();
                    HashMap<String, String> pushData = new HashMap<>();
                    pushData.put("activity", data.getStringExtra("title"));
                    pushData.put("location", data.getStringExtra("location"));
                    pushData.put("date", data.getStringExtra("date"));
                    pushData.put("time", data.getStringExtra("time"));
                    pushData.put("ref", data.getStringExtra("reference"));
                    agendaItem.setValue(pushData);
                }
            }
        } else if (requestCode == 12) {               // home feed preferences set
            if (resultCode == 1) {                          // filter home feed
                filteredHomeListItems.clear();
                filteredHomeListTitles.clear();
                filteredHomeListItems.addAll(homeListItems);
                filteredHomeListTitles.addAll(homeListTitles);
                // categories filter
                ArrayList<String> types = data.getStringArrayListExtra("type");
                if (!types.contains("Events")) {        // remove any events if events not selected
                    for (int i = 0; i < filteredHomeListItems.size(); i++) {
                        if (filteredHomeListItems.get(i).event) {
                            filteredHomeListItems.remove(i);
                            filteredHomeListTitles.remove(i);
                            i = i - 1;
                        }
                    }
                }
                if (!types.contains("Activities")) {    // remove any activities if activities not selected
                    for (int i = 0; i < filteredHomeListItems.size(); i++) {
                        if (!filteredHomeListItems.get(i).event) {
                            filteredHomeListItems.remove(i);
                            filteredHomeListTitles.remove(i);
                            i = i - 1;
                        }
                    }
                }
                // cost filter
                try {
                    int limit = data.getIntExtra("cost", 99999999);
                    for (int i = 0; i < filteredHomeListItems.size(); i++) {
                        if (filteredHomeListItems.get(i).price > limit) {       // remove items above the price threshold set
                            filteredHomeListItems.remove(i);
                            filteredHomeListTitles.remove(i);
                            i = i - 1;
                        }
                    }
                } catch (Exception e) {
                         FirebaseCrash.report(e);
                }
                // location filter
                //        location = data.getStringExtra("location");
                // distance filter
                double dist = data.getDoubleExtra("distance", 0);
                for (int i = 0; i < filteredHomeListItems.size(); i++) {
                    if (filteredHomeListItems.get(i).distAway > dist) {     // remove items further away then threshold set
                        filteredHomeListItems.remove(i);
                        filteredHomeListTitles.remove(i);
                        i = i - 1;
                    }
                }
                // other- family friendly
                ArrayList<String> other = data.getStringArrayListExtra("other");
                if (other.contains("Family Friendly")) {               // remove items not family friendly if ffo set
                    for (int i = 0; i < filteredHomeListItems.size(); i++) {
                        if (!filteredHomeListItems.get(i).familyfriendly) {
                            filteredHomeListItems.remove(i);
                            filteredHomeListTitles.remove(i);
                            i = i - 1;
                        }
                    }
                }
                if (other.contains("Indoor")) {               // remove items not family friendly if ffo set
                    for (int i = 0; i < filteredHomeListItems.size(); i++) {
                        if (!filteredHomeListItems.get(i).indoor) {
                            filteredHomeListItems.remove(i);
                            filteredHomeListTitles.remove(i);
                            i = i - 1;
                        }
                    }
                }
                if (other.contains("Disabled Access")) {               // remove items not family friendly if ffo set
                    for (int i = 0; i < filteredHomeListItems.size(); i++) {
                        if (!filteredHomeListItems.get(i).disabled) {
                            filteredHomeListItems.remove(i);
                            filteredHomeListTitles.remove(i);
                            i = i - 1;
                        }
                    }
                }
                if (other.contains("Parking")) {               // remove items not family friendly if ffo set
                    for (int i = 0; i < filteredHomeListItems.size(); i++) {
                        if (!filteredHomeListItems.get(i).parking) {
                            filteredHomeListItems.remove(i);
                            filteredHomeListTitles.remove(i);
                            i = i - 1;
                        }
                    }
                }
                if (other.contains("Pet Friendly")) {               // remove items not family friendly if ffo set
                    for (int i = 0; i < filteredHomeListItems.size(); i++) {
                        if (!filteredHomeListItems.get(i).pet) {
                            filteredHomeListItems.remove(i);
                            filteredHomeListTitles.remove(i);
                            i = i - 1;
                        }
                    }
                }
                if (other.contains("Toilets")) {               // remove items not family friendly if ffo set
                    for (int i = 0; i < filteredHomeListItems.size(); i++) {
                        if (!filteredHomeListItems.get(i).toilet) {
                            filteredHomeListItems.remove(i);
                            filteredHomeListTitles.remove(i);
                            i = i - 1;
                        }
                    }
                }

                // update list
                if (homeFragment != null) { // if no change
                    if (filteredHomeListTitles.size() == homeListTitles.size())       // note true on end forces list to update properly
                        homeFragment.storeData(homeListItems, homeListTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, interested, true);
                    else
                        homeFragment.storeData(filteredHomeListItems, filteredHomeListTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, interested, true);
                }
            }
        }

    }


    /*
    function that runs whenever the permissions are granted or denied
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 13: {          // if location permission granted, get current location
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    currentLoc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);}
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());               // generating adapter
        adapter.addFragment(homeFragment);
        adapter.addFragment(friendFragment);
        adapter.addFragment(profileFragment);
        viewPager.setAdapter(adapter);// set the adapter to the container
    }


    // create options menu in action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
                    // show login or logout button
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        if (user1 != null) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);   // settings
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);   // settings
        }
        return true;
    }

    // respond to action bar item press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_search:
                return true;
            case R.id.action_preferences:
                Intent intent3 = new Intent(this, SearchPrefActivity.class);
                startActivityForResult(intent3, 12);
                return true;
            case R.id.action_settings:
                Intent intent4 = new Intent(this, SettingsActivity.class);
                intent4.putExtra("fbCon", facebookConnected);
                intent4.putExtra("tab", currentTab);
                startActivity(intent4);
                return true;
            case R.id.action_about_help:
                Intent intent5 = new Intent(this, AboutHelpActivity.class);
                intent5.putExtra("tab", currentTab);
                startActivity(intent5);
                return true;
            case R.id.action_logout:
                LoginManager.getInstance().logOut();
                auth.signOut();
                facebookConnected = false;
                currentTab = viewPager.getCurrentItem();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("tab", viewPager.getCurrentItem());
                startActivity(intent);
                return true;
            case R.id.action_login:
                currentTab = viewPager.getCurrentItem();
                Intent intent2 = new Intent(this, LoginActivity.class);
                intent2.putExtra("tab", viewPager.getCurrentItem());
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    All functions regarding getting and setting data from firebase and facebook
     */

    // get friend data
    void getNSetFriendData() {
        // get users friends
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();           // database data
        final ArrayList<String> friendFBNames = facebookData.getStringArrayList("friendNames");
        // if user has friends
        if (!(friendFBNames == null || friendFBNames.isEmpty())) {
            // get friend data
            final ArrayList<String> friendFBUrls = facebookData.getStringArrayList("friendUrls");
            final ArrayList<String> friendUIDs = facebookData.getStringArrayList("friendUids");
            assert friendUIDs != null;
            assert friendFBUrls != null;
            // for each friend
            for (int j = 0; j < friendUIDs.size(); j++) {
                // get and set the friends' agenda data
                final DatabaseReference friend = database.child("users").child(friendUIDs.get(j)).child("Agenda");
                friend.addValueEventListener(new ValueEventListener() {
                    @Override               // upon data return
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            new AsyncTask<Object, Boolean, Boolean>() {
                                @Override
                                protected Boolean doInBackground(Object... params) {
                                    activityFriendGoingNumbersHolder = activityFriendGoingNumbers;
                                    // isolate each agenda item along with which friend it is
                                    GenericTypeIndicator<HashMap<String, AgendaClass>> t = new GenericTypeIndicator<HashMap<String, AgendaClass>>() {
                                    };
                                    HashMap<String, AgendaClass> agendaData = dataSnapshot.getValue(t);              // get agenda data
                                    Iterator<AgendaClass> iterator = agendaData.values().iterator();                // parse out a list of friendClass'
                                    String friendUid = dataSnapshot.getRef().getParent().getKey();                  // get this friend's UID
                                    // remove all list items of this friend
                                    int i = friendUIDs.indexOf(friendUid);
                                    String friendUrl = friendFBUrls.get(i);
                                    for (int k = 0; k < friendFeedListItemsData.size(); k++) {                            // not the size gets calculated upon every iteration
                                        if (friendFeedListItemsData.get(k).picUrl.equals(friendUrl)) {                    // ideally use fb uid but as this is not available using urls as they are unique
                                            friendFeedListItemsData.remove(k);
                                            friendFeedListItems.remove(k);
                                            k = k - 1;                                                            // as all items left unchecked have moved pointers by -1, reflect this in where we are up to counting
                                        }
                                    }               // all all list items for this friend
                                    // for each agenda item
                                    while (iterator.hasNext()) {
                                        AgendaClass agendaItem = iterator.next();
                                        TimeDispNRank timeNRank = formatTime(agendaItem.date, agendaItem.time); // returns formatted datetime string and a number to rank it based on its date and time
                                        if (!timeNRank.timeDisp.equals("0")) {      // item not already been and no error
                                            String ref = agendaItem.ref;
                                            agendaItem.rank = timeNRank.rank;
                                            agendaItem.activityDescription = agendaItem.activity + ", " + location;
                                            agendaItem.timeAgo = timeNRank.timeDisp;
                                            agendaItem.friendName = friendFBNames.get(i);
                                            agendaItem.picUrl = friendFBUrls.get(i);
                                            friendFeedListItems.add(ref);
                                            friendFeedListItemsData.add(agendaItem);
                                            friendFeedSortedList = friendFeedListItemsData;
                                            Collections.sort(friendFeedSortedList, new AgendaComparator());     // sort upcoming items


                                            if (activityFriendGoingNumbersHolder.containsKey(ref)) {          // if a friend is already going to this event
                                                if (!activityFriendGoingNumbersHolder.get(ref).contains(friendFBNames.get(i))){   // if this specific friend not already recorded as going to this event
                                                            // append them to the list of friends which are going
                                                    ArrayList<String> temp = activityFriendGoingNumbersHolder.get(ref);
                                                    temp.add(friendFBNames.get(i));
                                                    activityFriendGoingNumbersHolder.put(ref, temp);
                                                }
                                                            // otherwise do noting as they are already going
                                            } else {
                                                        // if no friends are going to this event
                                                ArrayList<String> temp = new ArrayList<>();
                                                                // create a new list and add this friend
                                                temp.add(friendFBNames.get(i));
                                                activityFriendGoingNumbersHolder.put(ref, temp);
                                            }
                                        }

                                    }
                                    return true;
                                }

                                @Override
                                protected void onPostExecute(Boolean result) {
                                    if (result) {
                                        if (friendFragment != null)
                                            friendFragment.storeData(friendFeedSortedList, friendFeedListItems);    // populate friend feed list
                                        // now friend agenda data has been found, show friend going numbers on home feed
                                        // update list
                                        if (homeFragment != null) {
                                            activityFriendGoingNumbers = activityFriendGoingNumbersHolder;
                                            homeFragment.storeData(homeListItems, homeListTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, interested, false);
                                        }
                                    }
                                }
                            }.execute();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                // get and set friend interest count on home feed
                final DatabaseReference friendInt = database.child("users").child(friendUIDs.get(j)).child("Interested");
                friendInt.addChildEventListener(new ChildEventListener() {
                    @Override               // get a single interest
                    public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                        String friendUid = dataSnapshot.getRef().getParent().getParent().getKey();
                        int i = friendUIDs.indexOf(friendUid);
                        String ref = dataSnapshot.getValue().toString();
                        if (activityFriendInterestedNumbers.containsKey(ref)) {
                            if (!activityFriendInterestedNumbers.get(ref).contains(friendFBNames.get(i))){
                                ArrayList<String> temp = activityFriendInterestedNumbers.get(ref);
                                temp.add(friendFBNames.get(i));
                                activityFriendInterestedNumbers.put(ref, temp);
                            }
                        } else {
                            ArrayList<String> temp = new ArrayList<>();
                            temp.add(friendFBNames.get(i));
                            activityFriendInterestedNumbers.put(ref, temp);
                        }

                        if (homeFragment != null)   // update data
                            homeFragment.storeData(homeListItems, homeListTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, interested, false);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) { // remove 1 from overall count and update home list
                        String friendUid = dataSnapshot.getRef().getParent().getKey();                  // get this friend's UID
                        int i = friendUIDs.indexOf(friendUid);
                        String ref = dataSnapshot.getValue().toString();
                        ArrayList<String> temp = activityFriendInterestedNumbers.get(ref);
                        temp.remove(friendFBNames.get(i));
                        activityFriendInterestedNumbers.put(ref, temp);
                        homeFragment.storeData(homeListItems, homeListTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, interested, false);
                    }
                                // not possible
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        }
    }
                    // get user data
    private void getNSetUserData() {
                        // set users interests stars on home screen
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference interestedRef = database.child("users").child(user.getUid()).child("Interested");
        interestedRef.addValueEventListener(new ValueEventListener() {
            @Override                           // get interests
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> snapshotIterator = dataSnapshot.getChildren().iterator();
                interested.clear(); // prevents using async for some reason
                while (snapshotIterator.hasNext()) {
                    interested.add(snapshotIterator.next().getValue().toString());
                }
                if (homeFragment != null)       // update list
                    homeFragment.storeData(homeListItems, homeListTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, interested, false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getCounts(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        // get the number of activities and events in the database
        database.child("activitydata/placeData").child(location).child("activityCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activityCountDatabase = ((Long) dataSnapshot.getValue()).intValue();
                getNSetHomeFeedData();          // get home feed data
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        database.child("activitydata/placeData").child(location).child("eventCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventCountDatabase = ((Long) dataSnapshot.getValue()).intValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void round2() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference activityDataRef = database.child("activitydata/placeData").child(location).child("activities");
        Query orderedActivities2 = activityDataRef.orderByKey().limitToFirst(activity_random_start);

        // get the activity data
        orderedActivities2.addChildEventListener(new ChildEventListener() {
            @Override
            // for each activity agenda item, add it and repopulate home list
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                new AsyncTask<Object, Boolean, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Object... params) {
                        //         homeListItemsHolder = homeListItems; homeListTitlesHolder = homeListTitles;  homeListRefsHolder = homeListRefs;
                        GenericTypeIndicator<AgendaClass> t = new GenericTypeIndicator<AgendaClass>() {
                        };
                        AgendaClass agendaItem = dataSnapshot.getValue(t);              // get agenda data
                        agendaItem.ref = location + "/activities/" + dataSnapshot.getKey();
                        if (homeListRefs.contains(agendaItem.ref)) return false;
                        else {
                            agendaItem.event = false;
                            agendaItem.distAway = getDistanceAway(agendaItem.location);                         // get distance away
                            unseenHomeUpdates[activityCount2%10] = agendaItem;
                            activityCount2 = activityCount2 + 1;
                            return true;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {
                            if (homeFragment != null) {
                                if (!homeListRefs.contains(unseenHomeUpdates[activityCount % 10].ref)) {
                                    homeListItems.add(unseenHomeUpdates[activityCount % 10]);
                                    homeListTitles.add(unseenHomeUpdates[activityCount % 10].activity);
                                    homeListRefs.add(unseenHomeUpdates[activityCount % 10].ref);
                                    activityCount = activityCount + 1;
                                    indexOfLastActivity = homeListTitles.size() - 1;
                                    homeFragment.storeData(homeListItems, homeListTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, interested, false);
                                }
                            }
                        }
                    }
                }.execute();
            }

            @Override       // for each activity agenda item, update it and repopulate home list
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
            }

            @Override       // for each activity agenda item, remove it and repopulate home list
            public void onChildRemoved(final DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("!!!!!!!!!!!!!!!11", databaseError.toString());
            }
        });
    }


                                    // get and set activities and events for around 'cambridge'
    public void getNSetHomeFeedData() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference activityDataRef = database.child("activitydata/placeData").child(location).child("activities");
        DatabaseReference eventsDataRef = database.child("activitydata/placeData").child(location).child("events");

        //lastActivityRef = homeListItems.get(indexOfLastActivity).ref.split("/")[2];
        Random random = new Random();
        activity_random_start =  random.nextInt(activityCountDatabase);
        activities_to_load = activity_random_start;
        Query orderedActivities = activityDataRef.orderByKey().limitToLast(activityCountDatabase-activity_random_start);
        Query orderedEvents = eventsDataRef.orderByChild("date");

        // get the activity data
        orderedActivities.addChildEventListener(new ChildEventListener() {
            @Override
            // for each activity agenda item, add it and repopulate home list
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                new AsyncTask<Object, Boolean, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Object... params) {
                        //         homeListItemsHolder = homeListItems; homeListTitlesHolder = homeListTitles;  homeListRefsHolder = homeListRefs;
                        GenericTypeIndicator<AgendaClass> t = new GenericTypeIndicator<AgendaClass>() {
                        };
                        AgendaClass agendaItem = dataSnapshot.getValue(t);              // get agenda data
                        agendaItem.ref = location + "/activities/" + dataSnapshot.getKey();
                        if (homeListRefs.contains(agendaItem.ref)) return false;
                        else {
                            agendaItem.event = false;
                            agendaItem.distAway = getDistanceAway(agendaItem.location);                         // get distance away
                            unseenHomeUpdates[activityCount2%10] = agendaItem;
                            activityCount2 = activityCount2 + 1;
                            return true;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {
                            if (homeFragment != null) {
                                if (!homeListRefs.contains(unseenHomeUpdates[activityCount % 10].ref)) {
                                    homeListItems.add(unseenHomeUpdates[activityCount % 10]);
                                    homeListTitles.add(unseenHomeUpdates[activityCount % 10].activity);
                                    homeListRefs.add(unseenHomeUpdates[activityCount % 10].ref);
                                    activityCount = activityCount + 1;
                                    indexOfLastActivity = homeListTitles.size() - 1;
                                    homeFragment.storeData(homeListItems, homeListTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, interested, false);

                                    activities_to_load = activities_to_load - 1;
                                    if (activities_to_load == 0) round2();
                                }
                            }
                        }
                    }
                }.execute();
            }

            @Override       // for each activity agenda item, update it and repopulate home list
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
            }

            @Override       // for each activity agenda item, remove it and repopulate home list
            public void onChildRemoved(final DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("!!!!!!!!!!!!!!!!!!1", databaseError.toString());
            }
        });

        // get events data
        orderedEvents.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                new AsyncTask<Object, Boolean, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Object... params) {
                        GenericTypeIndicator<AgendaClass> t = new GenericTypeIndicator<AgendaClass>() {
                        };
                        AgendaClass agendaItem = dataSnapshot.getValue(t);              // get agenda data
                        agendaItem.ref = location + "/events/" + dataSnapshot.getKey();
                        if (homeListRefs.contains(agendaItem.ref)) return false;
                        else {
                            agendaItem.event = true;
                            agendaItem.distAway = getDistanceAway(agendaItem.location);
                            unseenHomeUpdates[eventCount2%10+10] = agendaItem;
                            eventCount2 = eventCount2 + 1;
                            return true;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {
                            if (homeFragment != null) {
                                if (!homeListRefs.contains(unseenHomeUpdates[eventCount % 10 + 10].ref)) {
                                    homeListItems.add(unseenHomeUpdates[eventCount % 10 + 10]);
                                    homeListTitles.add(unseenHomeUpdates[eventCount % 10 + 10].activity);
                                    homeListRefs.add(unseenHomeUpdates[eventCount % 10 + 10].ref);
                                    eventCount = eventCount + 1;
                                    indexOfLastEvent = homeListTitles.size() - 1;
                                    homeFragment.storeData(homeListItems, homeListTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, interested, false);
                                }
                            }
                        }
                    }
                }.execute();
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(final DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


                                // function that returns formatted date or time, if it has been rank=0 and if not a number used to rank the item
    private TimeDispNRank formatTime(String dateString, String timeString) {
        String output;
        int rank;
        SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US);                // input data formats
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            Date date = formatDate.parse(dateString);
            Date time = formatTime.parse(timeString);
            Date dateCurrent = Calendar.getInstance().getTime();
            int day1 = (int) (date.getTime() / (1000 * 60 * 60 * 24L));                             // in days
            int day2 = (int) (dateCurrent.getTime() / (1000 * 60 * 60 * 24L));
            int daysApart = day1 - day2;
            rank = (int) (time.getTime() / (24 * 60L) + 2500) + daysApart * 100000;
            if (daysApart < 7) {
                if (daysApart < 1) {
                    if (daysApart < 0)
                        output = "0";                                                   // already been
                    else output = (String) android.text.format.DateFormat.format("HH:mm", time);
                }   // the same day - show time
                else output = (String) android.text.format.DateFormat.format("E", date);
            }                   // within a week - show the day
            else
                output = (String) android.text.format.DateFormat.format("dd, MMM", date);                  // outside a week - show the date
        } catch (ParseException e) {
            e.printStackTrace();
            output = "error";
            rank = 0;
        }
        return new TimeDispNRank(output, rank);
    }

    // function that calculate the distance an item is away from current location
    double getDistanceAway(String loc) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        if (currentLoc != null) {
            try {
                List<Address> addresses = geocoder.getFromLocationName(loc, 1);                     // get an address for the string location providerd
                if (addresses.size() != 0) {                                                        // if one exists
                                // calculate distance between current and the given location
                    LatLng StartP = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                    int Radius = 6371;//radius of earth in Km
                    double lat1 = StartP.latitude;
                    double lat2 = currentLoc.getLatitude();
                    double lon1 = StartP.longitude;
                    double lon2 = currentLoc.getLongitude();
                    double dLat = Math.toRadians(lat2 - lat1);
                    double dLon = Math.toRadians(lon2 - lon1);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                                    Math.sin(dLon / 2) * Math.sin(dLon / 2);
                    double c = 2 * Math.asin(Math.sqrt(a));
                    double valueResult = Radius * c;
                                // return formatted answer
                    DecimalFormat newFormat = new DecimalFormat("#####.#");
                    return Double.valueOf(newFormat.format(valueResult));
                }
                else return -1;
            }
            catch (IOException e) {
                return -1;
            }
        }
        else return -1;
    }
}


/**
 * Custom adapter used for defining adding fragment tabs to a viewpager
 */

class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null; //mFragmentTitleList.get(position);
    }
}


