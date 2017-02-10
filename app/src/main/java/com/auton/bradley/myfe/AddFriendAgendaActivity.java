package com.auton.bradley.myfe;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/*
    Popup activity for when you click add to calendar on a friends activity with the detailed activity view
 */

public class AddFriendAgendaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_agenda);
                        // get view elements
        Button button = (Button) findViewById(R.id.fty_button);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.fty_checkBox);
        TextView activityTv = (TextView) findViewById(R.id.fty_activity_text);
        TextView dateTv = (TextView) findViewById(R.id.fty_date_text);
                        // get data
        Intent intent = getIntent();
        final String title = intent.getStringExtra("activity");
        final String location = intent.getStringExtra("location");
        final String date = intent.getStringExtra("date");
        final String time = intent.getStringExtra("time");
        final String ref = intent.getStringExtra("reference");
                        // set view data
        activityTv.setText(title);
        dateTv.setText(formatData(date) + " at " + formatTime(time));
                        // add button clicked
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                            // upload activity to users agenda on firebase
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                DatabaseReference agendaItem = database.child("users").child(user.getUid()).child("Agenda").push();
                HashMap<String, String> pushData = new HashMap<>();
                pushData.put("activity",title);
                pushData.put("location",location);
                pushData.put("date",date);
                pushData.put("time",time);
                pushData.put("ref", ref);
                agendaItem.setValue(pushData);

                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_detailed_item), "Added to calendar", Snackbar.LENGTH_LONG);
                snackbar.show();
                if (checkBox.isChecked()) {
                    // send notification to appropriate friend
                }
                finish();                                                                           // return to previous activity (detailed item view)
            }
        });
    }
                    // functions used to display date and time in a nice way
    private String formatData(String input) {
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
}
