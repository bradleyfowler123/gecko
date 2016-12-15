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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class EnterDateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_date);

        GridLayout grid = (GridLayout) findViewById(R.id.activity_enter_date);

        Log.d("nfdkls", Integer.toString(grid.getChildCount()));
        LinearLayout line1 = (LinearLayout) grid.getChildAt(1);
        LinearLayout line2 = (LinearLayout) grid.getChildAt(3);
        final TextView[] weekDays = new TextView[8];
        for (int i = 0; i < 4; i++) {
            weekDays[i] = (TextView) line1.getChildAt(i*2 +1);
            weekDays[i+4] = (TextView) line2.getChildAt(i*2 +1);
        }

        String[] wkNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        int start = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        for (int i = 0; i < 7; i++) {
            int j = (start+i) % 7;
            weekDays[i].setText(wkNames[j]);
            weekDays[i].setOnClickListener(new onClickListenerPosition(i) {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getBaseContext(),Integer.toString(this.position),Toast.LENGTH_SHORT).show();

                    TimePickerDialog tpd = new TimePickerDialog(EnterDateActivity.this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        }
                    },Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),true);
                    tpd.show();

                }
            });
        }
        weekDays[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(EnterDateActivity.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, final int i2) {

                        TimePickerDialog tpd = new TimePickerDialog(EnterDateActivity.this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i3, int i4) {
                                
                            }
                        },Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),true);
                        tpd.show();

                    }
                }, Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });



    }

}

