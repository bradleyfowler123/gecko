package com.auton.bradley.myfe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_item);
        String activityImageUrl;
                    // get vies objects
        final ImageView iv_activityImage = (ImageView) findViewById(R.id.adi_image);
        final TextView tv_title = (TextView) findViewById(R.id.adi_title);
                        // work out what started activity
        Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        if (from.equals("home")) {
                        // get and set data
            Bundle data = intent.getBundleExtra("data");
            tv_title.setText(data.getString("title"));
            Picasso.with(getBaseContext()).load(data.getString("image")).into(iv_activityImage);

        }else if (from.equals("friendFeed")) {
                        // startup
            findViewById(R.id.adi_friendData).setVisibility(View.VISIBLE);
            final ImageView iv_friendImage = (ImageView) findViewById(R.id.adi_fd_image);
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
                    AgendaClass activityData = dataSnapshot.getValue(AgendaClass.class);              // get agenda data
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

        }

    }
}
