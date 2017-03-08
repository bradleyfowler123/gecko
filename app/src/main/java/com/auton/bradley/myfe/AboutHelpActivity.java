package com.auton.bradley.myfe;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class AboutHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_help);

        final ListView listView = (ListView) findViewById(R.id.aboutHelp__list);

        // Defined Array values to show in ListView
        String[] values = new String[] {
                "About us",
                "FAQ",
                "Contact us",
                "Attributions"
        };

        final String aboutUs = "Our mission is to sustainably enhance the quality of life of individuals by providing" +
                " meaningful opportunities to stimulate and develop people outside of work." +
                "\n\nCreated by Brad Fowler from Cambridge University.";
        final ArrayList<String> faqQuestions = new ArrayList<>(Arrays.asList("Why can't I see some of my friends activity when I know they have scheduled things?","Question 2?"));
        final ArrayList<String> faqAnswers = new ArrayList<>(Arrays.asList("Firstly check that you are friends with them on Facebook. If so then kindly ask them to log out of GoPort and back in again on their phone to fix the problem.","Answer 2"));
        final String attrib = "Thank you:\n";

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                switch (position) {
                            // about us
                    case 0:
                        Intent intent = new Intent(AboutHelpActivity.this, AboutHelpActivityItem.class);
                        intent.putExtra("from", "about");
                        intent.putExtra("data", aboutUs);
                        startActivity(intent);
                        break;
                            // faq
                    case 1:
                        Intent intent2 = new Intent(AboutHelpActivity.this, AboutHelpActivityItem.class);
                        intent2.putExtra("from", "faq");
                        intent2.putExtra("questions", faqQuestions);
                        intent2.putExtra("answers", faqAnswers);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","bradley.fowler123@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "GoPort Query");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear GoPort, \n \n");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        break;
                    case 3:
                        Intent intent4 = new Intent(AboutHelpActivity.this, AboutHelpActivityItem.class);
                        intent4.putExtra("from", "attrib");
                        intent4.putExtra("data", attrib);
                        startActivity(intent4);
                        break;
                }
            }

        });
    }



}