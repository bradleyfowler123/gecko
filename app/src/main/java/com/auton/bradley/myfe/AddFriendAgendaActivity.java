package com.auton.bradley.myfe;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        final String title = intent.getStringExtra("title");
        final String location = intent.getStringExtra("location");
        final String date = intent.getStringExtra("date");
        Log.d("ndkls",date);
        final String time = intent.getStringExtra("time");
        final String uid = intent.getStringExtra("friendUid");
                        // set view data
        activityTv.setText(title);
        dateTv.setText(formatData(date) + " at " + time);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                DatabaseReference agendaItem = database.child("users").child(user.getUid()).child("Agenda").push();
                HashMap<String, String> pushData = new HashMap<>();
                pushData.put("activity",title);
                pushData.put("location",location);
                pushData.put("date",date);
                pushData.put("time",time);
                agendaItem.setValue(pushData);

                Toast.makeText(getBaseContext(),"Added",Toast.LENGTH_SHORT).show();

                if (checkBox.isChecked()) {
                    
                }
                finish();
            }
        });
    }

    private String formatData(String input) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String output = "error";
        try {
            Date date = format.parse(input);
            //   String week = (String) android.text.format.DateFormat.format("ww", date);
            //   int weekYear = DateFormat.getDateInstance().getCalendar().getWeekYear();
            output = (String) android.text.format.DateFormat.format("dd, MMM", date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output;
    }
}
