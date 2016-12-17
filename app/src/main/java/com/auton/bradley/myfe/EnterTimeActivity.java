package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;

public class EnterTimeActivity extends AppCompatActivity {

    private int hour = 14;
    private int minute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_time);
                            // get all the view objects
        final TextView time = (TextView) findViewById(R.id.aet_time);
        SeekBar seekBar = (SeekBar) findViewById(R.id.aet_seekBar);
        Button enterButton = (Button) findViewById(R.id.aet_button);
                            // when the time is clicked, let user to enter custom time
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = new TimePickerDialog(EnterTimeActivity.this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i3, int i4) {
                        hour=i3; minute=i4;                                                         // set custom time
                        time.setText(Integer.toString(hour) + ":" + String.format("%02d", minute)); // display custom time
                    }
                },hour,minute,true);
                tpd.show();
            }
        });
                            // update time when slider changed
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hour = 7+i; minute = 0;
                time.setText(Integer.toString(hour) + ":" + String.format("%02d", minute));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
                            // when user clicks enter
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                            // send time back
                Intent intent = new Intent();
                intent.putExtra("time", Integer.toString(hour) + ":" + Integer.toString(minute));
                setResult(1,intent);
                finish();                                                                           // end activity
            }
        });
    }
}
