package com.auton.bradley.myfe;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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

        // work out what started activity
        final Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        if (from.equals("home")) {
            // get and set data
            Bundle data = intent.getBundleExtra("data");
            tv_title.setText(data.getString("title"));
            Picasso.with(getBaseContext()).load(data.getString("image")).into(iv_activityImage);

            mapView = (MapView) findViewById(R.id.mapView2);
            mapView.onCreate(savedInstanceState);
            // Gets to GoogleMap from the MapView and does initialization stuff
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(0, 0))
                            .title("Marker"));
                    googleMap.setMyLocationEnabled(true);
                    UiSettings uiSettings = googleMap.getUiSettings();
                    uiSettings.setMyLocationButtonEnabled(true);
                    uiSettings.setAllGesturesEnabled(true);
                    uiSettings.setZoomControlsEnabled(true);
                    uiSettings.setMapToolbarEnabled(true);
                }
            });

        }else if (from.equals("friendFeed") || from.equals("friendPage")) {
                        // startup
            findViewById(R.id.adi_friendData).setVisibility(View.VISIBLE);
            final ImageView iv_friendImage = (ImageView) findViewById(R.id.adi_fd_image);
            ImageView iv_addToCal = (ImageView) findViewById(R.id.adi_add_to_calander);
            final TextView tv_friendName = (TextView) findViewById(R.id.adi_fd_text);
                        // get data
            String ref = intent.getStringExtra("ref");
            String friendName = intent.getStringExtra("friendName");
            String friendImage = intent.getStringExtra("friendUrl");
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference agenda = database.child("activitydata").child("cambridge").child(ref);
            agenda.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    activityData = dataSnapshot.getValue(AgendaClass.class);              // get agenda data
                            // set data
                    tv_title.setText(activityData.activity);
                    Picasso.with(getBaseContext()).load(activityData.image).into(iv_activityImage);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Datebase Error2", databaseError.toString());
                }
            });
            // set data
            tv_friendName.setText(friendName);
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
