package com.auton.bradley.myfe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutHelpActivityItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_help_item);
        TextView textView = (TextView) findViewById(R.id.aboutHelp_item_text);
        String data = getIntent().getStringExtra("data");
        textView.setText(data);

    }
}
