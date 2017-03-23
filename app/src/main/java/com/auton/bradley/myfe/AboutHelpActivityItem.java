package com.auton.bradley.myfe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

/*
    Activity that is used to display text after an item in the about and help page list is clicked
 */

public class AboutHelpActivityItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            // set up the page and get the text view which can then be populated later
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_help_item);
        TextView textView = (TextView) findViewById(R.id.aboutHelp_item_text);

            // identify what data we are trying to display
        String from = getIntent().getStringExtra("from");

                // if it is just a block of text
        if (from.equals("about") || from.equals("attributions")) {
                    // get the data and display it
            String data = getIntent().getStringExtra("data");
            textView.setText(data);
        }

                // if it is a list of questions and answers - FAQ
        else {
                    // get the data, process it and then display it
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
