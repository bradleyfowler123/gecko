package com.auton.bradley.myfe;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/*
    Activity that runs when about and help is clicked from the menu list. Note it only displays a list which when click on then runs the appropriate other activity
 */

public class AboutHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            // set up the page and get the list view which can then be populated later
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_help);
        final ListView listView = (ListView) findViewById(R.id.aboutHelp__list);

            // Define the list items to show in ListView
        String[] values = new String[] {
                "About us",
                "FAQ",
                "Contact us",
                "Attributions"
        };

            // Define the text that will be displayed when about us is clicked
        final String aboutUs = "Our mission is to sustainably enhance the quality of life of individuals by providing" +
                " meaningful opportunities to stimulate and develop people outside of work." +
                "\n\nCreated by Brad Fowler from Cambridge University.";

            // Define the Questions and Answers that will be displayed when FAQ is clicked
        final ArrayList<String> faqQuestions = new ArrayList<>(Arrays.asList("Why can't I see some of my friends activity when I know they have scheduled things?","Why has my display name not updated after I've changed it?"));
        final ArrayList<String> faqAnswers = new ArrayList<>(Arrays.asList("Firstly check that you are friends with them on Facebook. If so then kindly ask them to log out of Gecko and back in again on their phone to fix the problem.","Try logging out and back in again."));

            // Define the text that will be displayed when Attributions is clicked
        final String attributions = "Icons made by Madebyoliver from www.flaticon.com is licensed by CC 3.0 BY";

        // generate a simple list view Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign this adapter to our ListView
        listView.setAdapter(adapter);

        // handle what happens when an item is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                switch (position) {
                            // about us
                    case 0:         // start AboutHelpActivityItem which contains an empty text view to be populated
                        Intent intent = new Intent(AboutHelpActivity.this, AboutHelpActivityItem.class);
                        intent.putExtra("from", "about");
                        intent.putExtra("data", aboutUs);   // send over text to display
                        startActivity(intent);
                        break;
                            // faq
                    case 1:         // start AboutHelpActivityItem which contains an empty text view to be populated
                        Intent intent2 = new Intent(AboutHelpActivity.this, AboutHelpActivityItem.class);
                        intent2.putExtra("from", "faq");
                        intent2.putExtra("questions", faqQuestions);    // send over data to display
                        intent2.putExtra("answers", faqAnswers);
                        startActivity(intent2);
                        break;
                            // contact us
                    case 2:          // start a send email intent, user can pick mail application of their choice
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","bradley.fowler123@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Gecko Query");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Gecko, \n \n");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        break;
                            // Attributions
                    case 3:         // start AboutHelpActivityItem which contains an empty text view to be populated
                        Intent intent4 = new Intent(AboutHelpActivity.this, AboutHelpActivityItem.class);
                        intent4.putExtra("from", "attributions");
                        intent4.putExtra("data", attributions);       // send over text to display
                        startActivity(intent4);
                        break;
                }
            }

        });
    }



}