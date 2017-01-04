package com.auton.bradley.myfe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
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


public class MainActivity extends AppCompatActivity {
    // declarations
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
    public FirebaseAuth auth; public FirebaseUser user;
    int currentTab = 0;
    String location = "cambridge";

    private FriendFragment friendFragment = new FriendFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private HomeFragment homeFragment = new HomeFragment();

    private ArrayList<AgendaClass> friendFeedListItemsData = new ArrayList<>();
    public ArrayList<AgendaClass> friendFeedSortedList = new ArrayList<>();
    private ArrayList<String> friendFeedListItems = new ArrayList<>(); // some necessary crap
    private ArrayList<AgendaClass> homeListItems = new ArrayList<>();                                // contains all of the data for all of the activities in cambridge
    private ArrayList<AgendaClass> filteredHomeListItems = new ArrayList<>();
    private ArrayList<String> filteredHomeListTitles = new ArrayList<>();
    private ArrayList<String> homeListRefs = new ArrayList<>();
    private ArrayList<String> homeListTitles = new ArrayList<>();                                         // stores all of the titles, used to filter results with search
    private Map<String, Integer> activityFriendGoingNumbers = new HashMap<>();
    private Map<String, Integer> activityFriendInterestedNumbers = new HashMap<>();
    private ArrayList<String> interested = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        // check permissions granted
        ArrayList<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        }
        if (permissions.size() != 0) {
            String[] Sarray = permissions.toArray(new String[permissions.size()]);
            ActivityCompat.requestPermissions(this, Sarray, 3);
        }
        // load main activity layout
        setContentView(R.layout.activity_main);                                                     // load the main activity view
        // load action bars
        toolbar = (Toolbar) findViewById(R.id.toolbar);                                             // enable the action bar (above tabbed menus)
        setSupportActionBar(toolbar);                                                               // you can edit action bar style in activity_main.xml
        // if there is user data or search preferences
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            // get user data
            currentTab = intent.getIntExtra("tab", 0);
            facebookConnected = intent.getBooleanExtra("fbConnected", false);
            if (facebookConnected) {
                facebookData = intent.getBundleExtra("fbData");}
        } else {            // else - i.e. on first app run
            auth.signOut();                     // sign out any firebase user that may be signed in as we have no data on them
            facebookConnected = false;
        }
        // load tab bar and tab data
        viewPager = (ViewPager) findViewById(R.id.container);                                       // find view underneith tabs
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);                                            // find tab layout
        tabLayout.setupWithViewPager(viewPager);                                                    // setup view
        setupTabIcons();                                                                            // add icons to tabs

        getNSetHomeFeedData();
        if (user != null) {
            getNSetUserData();
            if (facebookConnected != null && facebookConnected) {
                getNSetFriendData();
            }
        }
        viewPager.setCurrentItem(currentTab);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        friendFragment = (FriendFragment) getSupportFragmentManager().getFragment(savedInstanceState, "friendFragment");
        homeFragment = (HomeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "homeFragment");
        profileFragment = (ProfileFragment) getSupportFragmentManager().getFragment(savedInstanceState, "profileFragment");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (friendFragment != null && friendFragment.isAdded()) getSupportFragmentManager().putFragment(outState, "friendFragment", friendFragment);
        if (homeFragment != null && homeFragment.isAdded()) getSupportFragmentManager().putFragment(outState, "homeFragment", homeFragment);
        if (profileFragment != null && profileFragment.isAdded()) getSupportFragmentManager().putFragment(outState, "profileFragment", profileFragment);
    }
    // facebook - passes data back to facebook api
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);                            // Pass the activity result back to the Facebook SDK
                    // add to calendar button returns data
        if(requestCode==11) {
                // Make sure the request was successful
                if (resultCode == 1) {
                    Snackbar snackbar = Snackbar
                            .make(viewPager, "Added to calendar", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Snackbar snackbar1 = Snackbar.make(view, "Removed from calendar", Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                }
                            });
                    snackbar.show();
                    if (user != null) {       // upload selection to there agenda
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
            }
            else if (requestCode==12){               // home feed preferences set
                Log.d("yfhujbjk", Integer.toString(requestCode));
                if (resultCode == 1) {
                    filteredHomeListItems.clear(); filteredHomeListTitles.clear();
                    filteredHomeListItems.addAll(homeListItems); filteredHomeListTitles.addAll(homeListTitles);
                                // categories filter
                    ArrayList<String> types = data.getStringArrayListExtra("type");
                    if (!types.contains("Events")) {
                        for (int i = 0; i < filteredHomeListItems.size(); i++) {
                            if (filteredHomeListItems.get(i).event) {
                                filteredHomeListItems.remove(i); filteredHomeListTitles.remove(i);
                                i = i-1;
                            }
                        }
                    }
                    if (!types.contains("Activities")) {
                        for (int i = 0; i < filteredHomeListItems.size(); i++) {
                            if (!filteredHomeListItems.get(i).event) {
                                filteredHomeListItems.remove(i); filteredHomeListTitles.remove(i);
                                i = i-1;
                            }
                        }
                    }
                                // cost filter
                    int limit = data.getIntExtra("cost",99999999);
                    for (int i = 0; i < filteredHomeListItems.size(); i++) {
                        if (filteredHomeListItems.get(i).price > limit) {
                            filteredHomeListItems.remove(i); filteredHomeListTitles.remove(i);
                            i = i-1;
                        }
                    }
                                // location filter
                    location = data.getStringExtra("location");
                                // distance filter
                    double dist = data.getDoubleExtra("distance",0);
                    for (int i = 0; i < filteredHomeListItems.size(); i++) {
                        if (filteredHomeListItems.get(i).distAway > dist) {
                            filteredHomeListItems.remove(i); filteredHomeListTitles.remove(i);
                            i = i-1;
                        }
                    }
                    Log.d("idnsksdn", Double.toString(dist));
                                // other- family friendly
                    ArrayList<String> other = data.getStringArrayListExtra("other");
                    if (other.contains("Family Friendly Only")) {
                        for (int i = 0; i < filteredHomeListItems.size(); i++) {
                            if (!filteredHomeListItems.get(i).familyfriendly) {
                                filteredHomeListItems.remove(i); filteredHomeListTitles.remove(i);
                                i = i-1;
                            }
                        }
                    }

                                // update list
                    if (homeFragment!=null) homeFragment.storeData(filteredHomeListItems,filteredHomeListTitles,activityFriendGoingNumbers,activityFriendInterestedNumbers, interested, true);
                }
            }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 3: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,new String[]{permissions[i]},3);
                    }
                }
            }
        }
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());               // generating adapter
        adapter.addFragment(homeFragment, "Home");
        adapter.addFragment(friendFragment, "Friend");
        adapter.addFragment(profileFragment, "Profile");
        viewPager.setAdapter(adapter);// set the adapter to the container
    }


    // create options menu in action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (user != null) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
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
                startActivity(intent4);
                return true;
            case R.id.action_logout:
                //     if (facebookConnected) {
                LoginManager.getInstance().logOut();
                //     }
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


                                    // get friend data
    void getNSetFriendData() {
        // get users friends
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();           // database data
        final ArrayList<String> friendFBNames = facebookData.getStringArrayList("friendNames");
        // if user has friends
        if (!(friendFBNames==null || friendFBNames.isEmpty())) {
            // get friend data
            final ArrayList<String> friendFBUrls = facebookData.getStringArrayList("friendUrls");
            final ArrayList<String> friendUIDs = facebookData.getStringArrayList("friendUids");
            // for each friend
            for (int j = 0; j < friendUIDs.size(); j++) {
                                         // get and set the friends' agenda data
                final DatabaseReference friend = database.child("users").child(friendUIDs.get(j)).child("Agenda");
                friend.addValueEventListener(new ValueEventListener() {
                    @Override               // upon data return
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // isolate each agenda item along with which friend it is
                        GenericTypeIndicator<HashMap<String, AgendaClass>> t = new GenericTypeIndicator<HashMap<String, AgendaClass>>() {};
                        HashMap<String, AgendaClass> agendaData = dataSnapshot.getValue(t);              // get agenda data
                        Iterator<AgendaClass> iterator = agendaData.values().iterator();                // parse out a list of friendClass'
                        String friendUid = dataSnapshot.getRef().getParent().getKey();                  // get this friend's UID
                        // remove all list items of this friend
                        int i = friendUIDs.indexOf(friendUid);                                          // locate the index of where they are in FacebookData
                        String friendUrl = friendFBUrls.get(i);
                        for (int k = 0; k < friendFeedListItemsData.size(); k++) {                            // not the size gets calculated upon every iteration
                            if (friendFeedListItemsData.get(k).picUrl.equals(friendUrl)) {                    // ideally use fb uid but as this is not available using urls as they are unique
                                friendFeedListItemsData.remove(k);
                                friendFeedListItems.remove(k);
                                k = k-1;                                                            // as all items left unchecked have moved pointers by -1, reflect this in where we are up to counting
                            }
                        }               // all all list items for this friend
                        // for each agenda item
                        while (iterator.hasNext()) {
                            AgendaClass agendaItem = iterator.next();
                            TimeDispNRank timeNRank = formatTime(agendaItem.date, agendaItem.time);
                            if (!timeNRank.timeDisp.equals("0")) {
                                agendaItem.rank = timeNRank.rank;
                                agendaItem.activityDescription = agendaItem.activity + ", " + location;
                                agendaItem.timeAgo = timeNRank.timeDisp;
                                agendaItem.friendName = friendFBNames.get(i);
                                agendaItem.picUrl = friendFBUrls.get(i);
                                friendFeedListItems.add(agendaItem.ref);
                                friendFeedListItemsData.add(agendaItem);
                                friendFeedSortedList = friendFeedListItemsData;
                                Collections.sort(friendFeedSortedList, new AgendaComparator());
                                if (friendFragment!= null) friendFragment.storeData(friendFeedSortedList, friendFeedListItems);
                            }
                        }
                        findFriendsGoingToActivities();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                                // get and set friend interest count
                final DatabaseReference friendInt = database.child("users").child(friendUIDs.get(j)).child("Interested");
                                        // note single event listener so doesn't auto update, this is because noway to distinguish in friend removed or added interest
                friendInt.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String ref = dataSnapshot.getValue().toString();
                        if (activityFriendInterestedNumbers.containsKey(ref)) {
                            activityFriendInterestedNumbers.put(ref, activityFriendInterestedNumbers.get(ref).intValue() + 1);
                        } else activityFriendInterestedNumbers.put(ref, 1);
                        if (homeFragment!= null)homeFragment.storeData(homeListItems, homeListTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, interested,false);
                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String ref = dataSnapshot.getValue().toString();
                        activityFriendInterestedNumbers.put(ref, activityFriendInterestedNumbers.get(ref).intValue() - 1);
                        homeFragment.storeData(homeListItems, homeListTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, interested,false);
                    }
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
    private void findFriendsGoingToActivities() {
        activityFriendGoingNumbers.clear();
        for(String value : homeListRefs) {
            activityFriendGoingNumbers.put(value, Collections.frequency(friendFeedListItems, value));
        }
        if (homeFragment!= null)homeFragment.storeData(homeListItems,homeListTitles,activityFriendGoingNumbers,activityFriendInterestedNumbers, interested,false);
    }
                                // set users interests on home screen
    private void getNSetUserData() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference interestedRef = database.child("users").child(user.getUid()).child("Interested");
        interestedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> snapshotIterator = dataSnapshot.getChildren().iterator();
                interested.clear();
                while (snapshotIterator.hasNext()) {
                    interested.add(snapshotIterator.next().getValue().toString());
                }
                if (homeFragment!=null)homeFragment.storeData(homeListItems,homeListTitles,activityFriendGoingNumbers,activityFriendInterestedNumbers, interested,false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference agenda = database.child("users").child(user.getUid()).child("Agenda");
    }
                                // get and set activities for around cambridge
    private void getNSetHomeFeedData() {
        // setup variables
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference activityDataRef = database.child("activitydata").child(location).child("activities");
        // get data
        activityDataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GenericTypeIndicator<AgendaClass> t = new GenericTypeIndicator<AgendaClass>() {};
                AgendaClass agendaItem = dataSnapshot.getValue(t);              // get agenda data
                agendaItem.ref = dataSnapshot.getKey();
                agendaItem.event = false;
                agendaItem.distAway = getDistanceAway(agendaItem.location);
                homeListItems.add(agendaItem);
                homeListTitles.add(agendaItem.activity);
                homeListRefs.add("activities/" + agendaItem.ref);
                if (homeFragment!=null) homeFragment.storeData(homeListItems,homeListTitles,activityFriendGoingNumbers,activityFriendInterestedNumbers, interested,false);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int index = homeListRefs.indexOf("activities/" + dataSnapshot.getKey());
                homeListItems.remove(index);
                homeListTitles.remove(index);
                homeListRefs.remove(index);
                GenericTypeIndicator<AgendaClass> t = new GenericTypeIndicator<AgendaClass>() {};
                AgendaClass agendaItem = dataSnapshot.getValue(t);              // get agenda data
                agendaItem.ref = dataSnapshot.getKey();
                agendaItem.event = false;
                agendaItem.distAway = getDistanceAway(agendaItem.location);
                homeListItems.add(agendaItem);
                homeListTitles.add(agendaItem.activity);
                homeListRefs.add("activities/" + agendaItem.ref);
                homeFragment.storeData(homeListItems,homeListTitles,activityFriendGoingNumbers,activityFriendInterestedNumbers, interested,false);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = homeListRefs.indexOf("activities/" + dataSnapshot.getKey());
                homeListItems.remove(index);
                homeListTitles.remove(index);
                homeListRefs.remove(index);
                homeFragment.storeData(homeListItems,homeListTitles,activityFriendGoingNumbers,activityFriendInterestedNumbers, interested,false);
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
                // events
        DatabaseReference eventsDataRef = database.child("activitydata").child(location).child("events");
        // get data
        eventsDataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GenericTypeIndicator<AgendaClass> t = new GenericTypeIndicator<AgendaClass>() {};
                AgendaClass agendaItem = dataSnapshot.getValue(t);              // get agenda data
                agendaItem.ref = dataSnapshot.getKey();
                agendaItem.event = true;
                agendaItem.distAway = getDistanceAway(agendaItem.location);
                homeListItems.add(agendaItem);
                homeListTitles.add(agendaItem.activity);
                homeListRefs.add("events/" + agendaItem.ref);
                if (homeFragment!=null) homeFragment.storeData(homeListItems,homeListTitles,activityFriendGoingNumbers,activityFriendInterestedNumbers, interested,false);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int index = homeListRefs.indexOf("events/" + dataSnapshot.getKey());
                homeListItems.remove(index);
                homeListTitles.remove(index);
                homeListRefs.remove(index);
                GenericTypeIndicator<AgendaClass> t = new GenericTypeIndicator<AgendaClass>() {};
                AgendaClass agendaItem = dataSnapshot.getValue(t);              // get agenda data
                agendaItem.ref = dataSnapshot.getKey();
                agendaItem.event = true;
                agendaItem.distAway = getDistanceAway(agendaItem.location);
                homeListItems.add(agendaItem);
                homeListTitles.add(agendaItem.activity);
                homeListRefs.add("events/" + agendaItem.ref);
                homeFragment.storeData(homeListItems,homeListTitles,activityFriendGoingNumbers,activityFriendInterestedNumbers, interested,false);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = homeListRefs.indexOf("events/" + dataSnapshot.getKey());
                homeListItems.remove(index);
                homeListTitles.remove(index);
                homeListRefs.remove(index);
                homeFragment.storeData(homeListItems,homeListTitles,activityFriendGoingNumbers,activityFriendInterestedNumbers, interested,false);
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private TimeDispNRank formatTime(String dateString, String timeString) {
        String output; int rank;
        SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            Date date = formatDate.parse(dateString);
            Date time = formatTime.parse(timeString);
            Date dateCurrent = Calendar.getInstance().getTime();
            int day1 = (int) (date.getTime()/(1000*60*60*24L));
            int day2 = (int) (dateCurrent.getTime()/(1000*60*60*24L));
            int  daysApart = day1-day2;
            rank = (int) (time.getTime()/(24*60L)+2500) + daysApart*100000;
            if (daysApart<7) {
                if (daysApart<1){
                    if (daysApart<0) output = "0";                                                   // already been
                    else output = (String) android.text.format.DateFormat.format("HH:mm", time);}   // the same day - show timee
                else output = (String) android.text.format.DateFormat.format("E", date);}                   // within a week - show the day
            else output = (String) android.text.format.DateFormat.format("dd, MMM", date);                  // outside a week - show the date
        } catch (ParseException e) {
            e.printStackTrace();
            output = "error"; rank = 0;
        }
        return new TimeDispNRank(output,rank);
    }

    double getDistanceAway(String loc) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        try {
            List<Address> addresses = geocoder.getFromLocationName(loc, 1);
            if (addresses.size() != 0) {
                LatLng StartP = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location current = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                int Radius = 6371;//radius of earth in Km
                double lat1 = StartP.latitude;
                double lat2 = current.getLatitude();
                double lon1 = StartP.longitude;
                double lon2 = current.getLongitude();
                double dLat = Math.toRadians(lat2 - lat1);
                double dLon = Math.toRadians(lon2 - lon1);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
                double c = 2 * Math.asin(Math.sqrt(a));
                double valueResult = Radius * c;
                DecimalFormat newFormat = new DecimalFormat("#####.#");
                return Double.valueOf(newFormat.format(valueResult));
            } else {
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

}