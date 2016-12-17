package com.auton.bradley.myfe;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DetailedItemActivity extends AppCompatActivity {

    private MapView mapView;
    private AgendaClass activityData = new AgendaClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailed_item);
        // get vies objects
        final ImageView iv_activityImage = (ImageView) findViewById(R.id.adi_image);
        final TextView tv_title = (TextView) findViewById(R.id.adi_title);
        mapView = (MapView) findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);

        // work out what started activity
        final Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        if (from.equals("home")) {
            // get and set data
            final Bundle data = intent.getBundleExtra("data");
            String title = data.getString("title");
            String location = data.getString("location");
            tv_title.setText(title);
            Picasso.with(getBaseContext()).load(data.getString("image")).into(iv_activityImage);
            setupMap(title, location);
        }
        else if (from.equals("friendFeed") || from.equals("friendPage")) {
                        // startup
            findViewById(R.id.adi_friendData).setVisibility(View.VISIBLE);
            final ImageView iv_friendImage = (ImageView) findViewById(R.id.adi_fd_image);
            ImageView iv_addToCal = (ImageView) findViewById(R.id.adi_add_to_calander);
            final TextView tv_friendName = (TextView) findViewById(R.id.adi_fd_name);
            final TextView tv_friendText = (TextView) findViewById(R.id.adi_fd_text);
                        // get data
            String ref = intent.getStringExtra("ref");
            String friendName = intent.getStringExtra("friendName");
            String friendImage = intent.getStringExtra("friendUrl");
            String friendDate = intent.getStringExtra("friendDate");
            String friendTime = intent.getStringExtra("friendTime");
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference agenda = database.child("activitydata").child("cambridge").child(ref);
            agenda.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    activityData = dataSnapshot.getValue(AgendaClass.class);              // get agenda data
                            // set data
                    tv_title.setText(activityData.activity);
                    Picasso.with(getBaseContext()).load(activityData.image).into(iv_activityImage);
                    setupMap(activityData.activity,activityData.location);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Datebase Error2", databaseError.toString());
                }
            });
            // set data
            tv_friendName.setText(friendName);
            tv_friendText.setText("is going at " + formatTime(friendTime) + " on " + formatDate(friendDate));
            Picasso.with(getBaseContext()).load(friendImage).into(iv_friendImage);

            iv_addToCal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent2 = new Intent(getBaseContext(),AddFriendAgendaActivity.class);
                    intent2.putExtra("activity", activityData.activity);
                    intent2.putExtra("location", activityData.location);
                    intent2.putExtra("date", intent.getStringExtra("friendDate"));
                    intent2.putExtra("time", intent.getStringExtra("friendTime"));
                    intent2.putExtra("reference", activityData.ref);
                    startActivity(intent2);
                }
            });
        }
        else if (from.equals("profile")) {
            // startup
            findViewById(R.id.adi_scheduledView).setVisibility(View.VISIBLE);
            ImageView edit_schedule = (ImageView) findViewById(R.id.adi_scheduled_edit);
            final TextView scheduled_text = (TextView) findViewById(R.id.adi_scheduled_text);
            final String[] location = new String[1];                            // tempory, needs to be text view
            // get data
            final String ref = intent.getStringExtra("ref");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference agenda = database.child("activitydata").child("cambridge").child(ref);
            agenda.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    activityData = dataSnapshot.getValue(AgendaClass.class);              // get agenda data
                    // set data
                    tv_title.setText(activityData.activity);
                    location[0] = activityData.location;                            // tempory until put into text view
                    Picasso.with(getBaseContext()).load(activityData.image).into(iv_activityImage);
                    setupMap(activityData.activity,activityData.location);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Datebase Error2", databaseError.toString());
                }
            });
            // set data
            scheduled_text.setText("You are going at " + formatTime(time) + " on " + formatDate(date));
                            // edit schedule is clicked
            edit_schedule.setOnClickListener(new View.OnClickListener() {
                @Override               // get new date and time
                public void onClick(View view) {
                    Intent intent = new Intent(getBaseContext(), EnterDateActivity.class);
                    intent.putExtra("title", tv_title.getText());                                   // data need to add item to calendar
                    intent.putExtra("location", location[0]);
                    intent.putExtra("reference", ref);
                    startActivityForResult(intent, 1);                                              // result is handled below

                }
            });
        }

    }

    private String formatDate(String input) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
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
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
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

    void setupMap(final String title, final String location) {
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Geocoder geocoder = new Geocoder(getBaseContext());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(location, 1);
                    if (addresses.size() != 0) {
                        Address address = addresses.get(0);
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(address.getLatitude(), address.getLongitude()))
                                .title(title));
                        CameraPosition camPos = new CameraPosition(new LatLng(address.getLatitude(),address.getLongitude()),12,0,0); // zoom,,rotation
                        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(camPos);
                        googleMap.moveCamera(cu);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                googleMap.setMyLocationEnabled(true);
                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setMyLocationButtonEnabled(true);
                uiSettings.setAllGesturesEnabled(true);
                uiSettings.setZoomControlsEnabled(true);
                uiSettings.setMapToolbarEnabled(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // add to calendar button returns data
        switch (requestCode) {
            case 1: {
                // Make sure the request was successful
                if (resultCode == 1) {
                    Toast.makeText(getBaseContext(),"Updated!",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
