package com.auton.bradley.myfe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;


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

    private ArrayList<AgendaClass> listItemsData = new ArrayList<>();
    public ArrayList<AgendaClass> sortedList = new ArrayList<>();
    private ArrayList<String> listItems = new ArrayList<>(); // some necessary crap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance(); user = auth.getCurrentUser();
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
                            // check permissions granted
        ArrayList<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);}
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);}
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);}
        if (permissions.size() != 0) {
            String[] Sarray = permissions.toArray(new String[permissions.size()]);
            ActivityCompat.requestPermissions(this, Sarray, 3);
        }
        // load main activity layout
        setContentView(R.layout.activity_main);                                                     // load the main activity view
        // load action bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);                                             // enable the action bar (above tabbed menus)
        setSupportActionBar(toolbar);                                                               // you can edit action bar style in activity_main.xml
        // load tab bar and tab data
        viewPager = (ViewPager) findViewById(R.id.container);                                       // find view underneith tabs
        FriendFragment friendFragment = setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);                                            // find tab layout
        tabLayout.setupWithViewPager(viewPager);                                                    // setup view
        setupTabIcons();                                                                            // add icons to tabs
                            // if there is user data or search preferences
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
                                // get user data
            currentTab = intent.getIntExtra("tab", 0);
            facebookConnected = intent.getBooleanExtra("fbConnected", false);
            if (facebookConnected) {
                facebookData = intent.getBundleExtra("fbData");
            }                    // get search pref
            Bundle searchPref = intent.getBundleExtra("searchPref");
        } else {            // else - i.e. on first app run
            auth.signOut();                     // sign out any firebase user that may be signed in as we have no data on them
            facebookConnected = false;
        }
        if (facebookConnected != null && facebookConnected) {
            getNSetFriendFeedData(friendFragment);
        }

        viewPager.setCurrentItem(currentTab);
    }

    // facebook - passes data back to facebook api
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);                            // Pass the activity result back to the Facebook SDK
                    // add to calendar button returns data
        switch (requestCode) {
            case 1: {
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

    private FriendFragment setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());               // generating adapter
        adapter.addFragment(new HomeFragment(), "Home");
        FriendFragment myFragment = new FriendFragment();
        adapter.addFragment(myFragment, "Friend");
        adapter.addFragment(new ProfileFragment(), "Profile");
        viewPager.setAdapter(adapter);// set the adapter to the container
        return myFragment;
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
                intent3.putExtra("fbData", facebookData);
                intent3.putExtra("fbCon", facebookConnected);
                startActivity(intent3);
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



    void getNSetFriendFeedData(final FriendFragment friendFragment) {
    // get friend feed data and populate list
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
                // get the friends' agenda data
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
                        for (int k = 0; k < listItemsData.size(); k++) {                            // not the size gets calculated upon every iteration
                            if (listItemsData.get(k).picUrl.equals(friendUrl)) {                    // ideally use fb uid but as this is not available using urls as they are unique
                                listItemsData.remove(k);
                                listItems.remove(k);
                                k = k-1;                                                            // as all items left unchecked have moved pointers by -1, reflect this in where we are up to counting
                            }
                        }               // all all list items for this friend
                        // for each agenda item
                        while (iterator.hasNext()) {
                            AgendaClass agendaItem = iterator.next();
                            TimeDispNRank timeNRank = formatTime(agendaItem.date,agendaItem.time);
                            if (!timeNRank.timeDisp.equals("0")) {
                                agendaItem.rank = timeNRank.rank;
                                agendaItem.activityDescription = agendaItem.activity + ", Cambridge";
                                agendaItem.timeAgo = timeNRank.timeDisp;
                                agendaItem.friendName = friendFBNames.get(i);
                                agendaItem.picUrl = friendFBUrls.get(i);
                                listItems.add(agendaItem.activity);
                                listItemsData.add(agendaItem);
                                sortedList = listItemsData;
                                Collections.sort(sortedList, new AgendaComparator());
                                friendFragment.storeData(sortedList, listItems);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            // upon list item click

        }
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

}