package com.auton.bradley.myfe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class AboutHelpActivityItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_help_item);
        TextView textView = (TextView) findViewById(R.id.aboutHelp_item_text);
        String from = getIntent().getStringExtra("from");
        if (from.equals("about") || from.equals("attrib")) {
            String data = getIntent().getStringExtra("data");
            textView.setText(data);
        }
        else {  // FAQ
            ArrayList<String> questions = getIntent().getStringArrayListExtra("questions");
            ArrayList<String> answers = getIntent().getStringArrayListExtra("answers");
            String text = "";
            for (int i=0; i<questions.size();i++) {
                text = text + questions.get(i) + "\n\n" + answers.get(i) + "\n\n\n";
            }
            textView.setText(text);
        }


    }
}
