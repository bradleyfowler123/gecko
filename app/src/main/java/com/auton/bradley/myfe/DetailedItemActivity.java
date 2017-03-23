package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/*
    Activity used to display an activity or event in more detail. Also displays a banner if it is
    the users or friends with differing actions
 */

public class DetailedItemActivity extends AppCompatActivity implements OnMapReadyCallback {
    // global variable declarations
    private AgendaClass activityData = new AgendaClass();
    private ArrayList<String> myInterests; private Menu menu;
    private RequestCreator topImage; private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_item);
         // get and display appropriate data and handle button presses
        final Intent intent = getIntent();
        String from = intent.getStringExtra("from");                                                // work out what started this activity - either home feed, friend feed, friend profile, users profile
        myInterests = intent.getStringArrayListExtra("interests");
        switch (from) {
            case "home": {      // home list detailed activity view
                findViewById(R.id.adi_space).setVisibility(View.GONE);
                // get and set data
                String ref = intent.getStringExtra("ref");                              // for now, because it saves a lot of work, when item click on in home feed just get the data again. I think firebase is actually smart so uses saved copies offline
                getNSetData(ref);
                break;
            }
            case "friendFeed":  // friend's detailed activity view
            case "friendPage": {
                // get banner view objects
                findViewById(R.id.adi_friendData).setVisibility(View.VISIBLE);
                final ImageView iv_friendImage = (ImageView) findViewById(R.id.adi_fd_image);
                ImageView iv_addToCal = (ImageView) findViewById(R.id.adi_add_to_calander);
                final TextView tv_friendName = (TextView) findViewById(R.id.adi_fd_name);
                final TextView tv_friendText = (TextView) findViewById(R.id.adi_fd_text);
                // get data
                final String ref = intent.getStringExtra("ref");
                String friendName = intent.getStringExtra("friendName");                            // friend specific data
                String friendImage = intent.getStringExtra("friendUrl");
                String friendDate = intent.getStringExtra("friendDate");
                String friendTime = intent.getStringExtra("friendTime");

                getNSetData(ref);

                // set banner data
                tv_friendName.setText(friendName);
                tv_friendText.setText("is going at " + formatTime(friendTime) + " on " + formatDate(friendDate));
                Picasso.with(getBaseContext()).load(friendImage).placeholder(R.drawable.ic_profilepic).error(R.drawable.ic_profilepic).into(iv_friendImage);
                // handle banner add to cal click
                iv_addToCal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {    // display AddFriendAgendaActivity dialog which displays a confirmation request
                        Intent intent2 = new Intent(getBaseContext(), AddFriendAgendaActivity.class);
                        intent2.putExtra("activity", activityData.activity);
                        intent2.putExtra("location", activityData.location);
                        intent2.putExtra("date", intent.getStringExtra("friendDate"));
                        intent2.putExtra("time", intent.getStringExtra("friendTime"));
                        intent2.putExtra("reference", ref);
                        startActivity(intent2);
                    }
                });
                break;
            }
            case "profile": {   // user's agenda detailed activity view
                // get banner view objects
                findViewById(R.id.adi_scheduledView).setVisibility(View.VISIBLE);
                ImageView edit_schedule = (ImageView) findViewById(R.id.adi_scheduled_edit);
                final TextView scheduled_text = (TextView) findViewById(R.id.adi_scheduled_text);
                // get data
                Log.d("asdfg1", intent.toString());
                final String ref = intent.getStringExtra("ref");
                final String userRef = intent.getStringExtra("userRef");
                String date = intent.getStringExtra("date");
                String time = intent.getStringExtra("time");

                getNSetData(ref);

                // set users banner data
                scheduled_text.setText("You are going at " + formatTime(time) + " on " + formatDate(date));
                // edit schedule is clicked
                edit_schedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        // ask to delete or reschedule
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailedItemActivity.this);
                        builder.setMessage("Delete or Reschedule?")
                                .setPositiveButton("Reschedule", new DialogInterface.OnClickListener() {
                                    @Override        // get new date and time
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(getBaseContext(), EnterDateActivity.class);
                                        intent.putExtra("title", activityData.activity);                                   // data need to add item to calendar
                                        intent.putExtra("location", activityData.location);
                                        intent.putExtra("reference", ref);
                                        intent.putExtra("userRef", userRef);
                                        startActivityForResult(intent, 1);                                              // result is handled below
                                    }
                                })                  // delete item
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                        DatabaseReference userItem = database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Agenda").child(userRef);
                                        userItem.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    finish();                                       // return to profile page
                                                }
                                            }
                                        });
                                    }
                                })
                                .create()
                                .show();
                    }
                });
                break;
            }
        }

    }

    private void getNSetData(final String ref) {
        final ImageView iv_activityImage = (ImageView) findViewById(R.id.adi_image);
        final TextView tv_title = (TextView) findViewById(R.id.adi_title);
        final TextView tv_desc = (TextView) findViewById(R.id.adi_decription);
        final TextView tv_link = (TextView) findViewById(R.id.adi_link);
        final TextView tv_other = (TextView) findViewById(R.id.adi_other);
        final String[] refItems = ref.split("/");
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference agenda = database.child("activitydata/placeData").child(refItems[0]).child(refItems[1]).child(refItems[2]);
        agenda.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    tv_title.setText("Item removed by the provider");
                    tv_desc.setVisibility(View.GONE);
                    tv_link.setVisibility(View.GONE);
                    tv_other.setVisibility(View.GONE);
                    iv_activityImage.setVisibility(View.GONE);
                    findViewById(R.id.map).setVisibility(View.GONE);
                }
                else {
                    activityData = dataSnapshot.getValue(AgendaClass.class);              // get agenda data
                    activityData.event = refItems[1].equals("events");  // set activity/event data
                    activityData.ref = ref;
                    Picasso.with(getBaseContext()).load(activityData.image).placeholder(R.drawable.detailedviewrectangle).error(R.drawable.detailedviewrectangle).into(iv_activityImage);
                    tv_title.setText(activityData.activity);
                    tv_desc.setText(activityData.activityDescription);
                    tv_link.setText("Visit: " + activityData.url);
                    if (activityData.event) {
                        if (activityData.price != 0) {
                            tv_other.setText("Prices from £" + activityData.price + "\n \nTimings:\n" + formatTime(activityData.time) + " on " + formatDate(activityData.date) + "\n \nLocation:\n" + activityData.location);
                        } else {
                            tv_other.setText("Timings:\n" + formatTime(activityData.time) + " on " + formatDate(activityData.date) + "\n \nLocation:\n" + activityData.location);
                        }
                    }
                    else {
                        if (activityData.price != 0) {
                            tv_other.setText("Prices from £" + activityData.price + "\n \nLocation:\n" + activityData.location);
                        }
                        else {
                            tv_other.setText("Location:\n" + activityData.location);
                        }
                    }
                    if (menu != null) {
                        if (myInterests.contains(activityData.ref))
                            menu.getItem(0).setIcon(android.R.drawable.star_on);
                        else menu.getItem(0).setIcon(android.R.drawable.star_off);
                    }
                    if (activityData.familyfriendly != null && activityData.familyfriendly)
                        findViewById(R.id.adi_iconList_family).setVisibility(View.VISIBLE);
                    if (activityData.disabled != null && activityData.disabled)
                        findViewById(R.id.adi_iconList_disabled).setVisibility(View.VISIBLE);
                    if (activityData.indoor != null && activityData.indoor)
                        findViewById(R.id.adi_iconList_indoor).setVisibility(View.VISIBLE);
                    if (activityData.parking != null && activityData.parking)
                        findViewById(R.id.adi_iconList_parking).setVisibility(View.VISIBLE);
                    if (activityData.pet != null && activityData.pet)
                        findViewById(R.id.adi_iconList_pet).setVisibility(View.VISIBLE);
                    if (activityData.toilet != null && activityData.toilet)
                        findViewById(R.id.adi_iconList_toilet).setVisibility(View.VISIBLE);
                    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
                    if (mapFragment!= null) mapFragment.getMapAsync(DetailedItemActivity.this);


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Datebase Error", databaseError.toString());
            }
        });

        tv_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityData.url!=null) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(activityData.url));
                    startActivity(i);
                }
            }
        });
    }

                // functions used for displaying date and time nicely on banners
    private String formatDate(String input) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String output = "error";
        try {
            Date date = format.parse(input);
            output = (String) android.text.format.DateFormat.format("dd, MMM", date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output;
    }
    private String formatTime(String input) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        String output;
        try {
            Date date2 = format.parse(input);
            output = (String) android.text.format.DateFormat.format("HH:mm", date2);
        } catch (ParseException e) {
            e.printStackTrace();
            output = "error";
        }
        return output;
    }


                            // users agenda item reschedule time returned
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // add to calendar button returns data
        if (resultCode == 1) {
            if (requestCode==1){
                                    // get data return
                    String date = data.getStringExtra("date"); String time = data.getStringExtra("time");
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference userItem = database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Agenda").child(data.getStringExtra("userRef"));
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("date",date);
                    map.put("time",time);
                                    // upload new date and time to firebase
                    userItem.updateChildren(map).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getBaseContext(),"Error Update Failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_detailed_item), "Updated", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    TextView textView = (TextView) findViewById(R.id.adi_scheduled_text);
                    textView.setText("You are going at " + formatTime(time) + " on " + formatDate(date));
            }
            else if (requestCode==32) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_detailed_item), "Added to calendar", Snackbar.LENGTH_SHORT);
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
        }
    }


    // create options menu in action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailedactivity, menu);
        this.menu = menu;
        if (myInterests!=null) {
            if (myInterests.contains(activityData.ref)) menu.getItem(0).setIcon(android.R.drawable.star_on);
            else menu.getItem(0).setIcon(android.R.drawable.star_off);
        }
        return true;
    }

    // respond to action bar item press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_addActivity:

                // if user not logged in
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {                            // tell them to log in
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DetailedItemActivity.this);
                    builder.setMessage("Sign in to add items to your agenda")
                            .setPositiveButton("okay", null)
                            .create()
                            .show();
                } else {              // if user logged in
                    final AgendaClass listItem = activityData;
                    if (listItem.event) {           // if event add it straight away
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.activity_detailed_item), "Added to calendar", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        // upload selection to there agenda
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference agendaItem = database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Agenda").push();
                        HashMap<String, String> pushData = new HashMap<>();
                        pushData.put("activity", listItem.activity);
                        pushData.put("location", listItem.location);
                        pushData.put("date", listItem.date);
                        pushData.put("time", listItem.time);
                        pushData.put("ref", listItem.ref);
                        agendaItem.setValue(pushData);
                    }
                    else {              // if activity get a date and time and then add it in main activity
                        Intent intent = new Intent(getBaseContext(), EnterDateActivity.class);
                        intent.putExtra("title", listItem.activity);                           // data need to add item to calendar
                        intent.putExtra("location", listItem.location);
                        intent.putExtra("reference", listItem.ref);
                        Log.d("CCCCCCCC", "d");
                        startActivityForResult(intent, 32);
                         // result is handled by main activity
                    }
                }


                return true;
            case R.id.action_interestedActivity:
                // if user not logged in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user== null) {                            // tell them to log in
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DetailedItemActivity.this);
                    builder.setMessage("Sign in to mark items as interested")
                            .setPositiveButton("okay", null)
                            .create()
                            .show();
                }
                else {              // if user logged in
                    String ref = activityData.ref;
                    if (myInterests.contains(ref)) {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.activity_detailed_item), "Unmarked as interested", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        myInterests.remove(ref);
                        menu.getItem(0).setIcon(android.R.drawable.star_off);
                    }
                    else {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.activity_detailed_item), "Marked as interested", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        myInterests.add(ref);
                        menu.getItem(0).setIcon(android.R.drawable.star_on);
                    }
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference agendaItem = database.child("users").child(user.getUid()).child("Interested");
                    agendaItem.setValue(myInterests).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("complete?", Boolean.toString(task.isSuccessful()));
                        }
                    });
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // function to setup the google map
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        new AsyncTask<Object, Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... params) {
                Geocoder geocoder = new Geocoder(getBaseContext());                                 // geocoder process' locations
                try {
                    List<Address> addresses = geocoder.getFromLocationName(activityData.location, 1);
                    if (addresses.size() != 0) {                                                    // if it finds one
                        address = addresses.get(0);
                        return true;
                    }
                    else {
                        return false;
                    }
                } catch (IOException e) {
                    return false;
                }
            }
            @Override
            protected void onPostExecute(Boolean result) {
                if(result){
                    googleMap.addMarker(new MarkerOptions()                                     // add pin on google map
                            .position(new LatLng(address.getLatitude(), address.getLongitude()))
                            .title(activityData.activity));
                    // move camera to that location
                    CameraPosition camPos = new CameraPosition(new LatLng(address.getLatitude(), address.getLongitude()), 12, 0, 0); // zoom,,rotation
                    CameraUpdate cu = CameraUpdateFactory.newCameraPosition(camPos);
                    googleMap.moveCamera(cu);
                }
                else {
                    View map = findViewById(R.id.map);
                    map.setVisibility(View.GONE);


                }
            }
        }.execute();
        // set blue dot where user is if they have allowed access to their location
        if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
    }
}
