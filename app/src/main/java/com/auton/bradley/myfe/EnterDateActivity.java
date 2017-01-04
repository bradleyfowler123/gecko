package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class EnterDateActivity extends AppCompatActivity {

    private int year;
    private int month;
    private int day;
    private String title;
    private String location;
    private String ref;                                                                             // to activity name in ca,bridge list
    private String activityUserRef;                                                                 // key of activity entry in users agenda, only used when updating item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_date);
                            // get data to be returned at finish()
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        location = intent.getStringExtra("location");
        ref = intent.getStringExtra("reference");
        activityUserRef = intent.getStringExtra("userRef");
                            // get all of the view objects
        GridLayout grid = (GridLayout) findViewById(R.id.activity_enter_date);
        LinearLayout line1 = (LinearLayout) grid.getChildAt(1);
        LinearLayout line2 = (LinearLayout) grid.getChildAt(3);
        final TextView[] weekDays = new TextView[8];
        for (int i = 0; i < 4; i++) {
            weekDays[i] = (TextView) line1.getChildAt(i*2 +1);
            weekDays[i+4] = (TextView) line2.getChildAt(i*2 +1);
        }
                            // set text on view weekday text views
        String[] wkNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        int start = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        for (int i = 0; i < 7; i++) {
            int j = (start+i) % 7;
            weekDays[i].setText(wkNames[j]);                                                        // arrange so starts from today
            weekDays[i].setOnClickListener(new onClickListenerPosition(i) {
                @Override           // when on is clicked
                public void onClick(View view) {        // get date and then get time
                    month = Calendar.getInstance().get(Calendar.MONTH) + 1;                         // deal with month starting at zero
                    day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+this.position;
                    year = Calendar.getInstance().get(Calendar.YEAR);
                    Intent intent = new Intent(EnterDateActivity.this,EnterTimeActivity.class);
                    startActivityForResult(intent,21);                                               // get time
                }
            });
        }                   // when ... is clicked
        weekDays[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                // show date picker dialog
                DatePickerDialog dpd = new DatePickerDialog(EnterDateActivity.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override       // get date and then get time
                    public void onDateSet(DatePicker datePicker, int i, int i1, final int i2) {
                        Intent intent = new Intent(EnterDateActivity.this,EnterTimeActivity.class);
                        year = i; month = i1+1; day = i2;
                        startActivityForResult(intent,21);                                           // get time
                    }
                }, Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
    }
    @Override               // when time is returned
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 21) {                                                                     // get time request code
            if (resultCode == 1) {                                                                  // Make sure the request was successful
                                // send date and time back
                Intent intent = new Intent();
                intent.putExtra("date", Integer.toString(month) + "/" + Integer.toString(day) + "/" + Integer.toString(year));
                intent.putExtra("time", data.getStringExtra("time"));
                intent.putExtra("title", title);
                intent.putExtra("location", location);
                intent.putExtra("reference", ref);
                if (activityUserRef != null) intent.putExtra("userRef", activityUserRef);
                setResult(1,intent);
                finish();
            }
        }
    }

}

