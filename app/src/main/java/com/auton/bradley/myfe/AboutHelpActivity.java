package com.auton.bradley.myfe;

import android.content.Intent;
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

        final String aboutUs = "Here is something about us aren't we cool \n no no";
        final ArrayList<String> faqQuestions = new ArrayList<>(Arrays.asList("Question 1?","Question 2?"));
        final ArrayList<String> faqAnswers = new ArrayList<>(Arrays.asList("Answer 1","Answer 2"));

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
                        // Begin the transaction
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
                        ft.replace(R.id.container111, new FooFragment());
// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
                        ft.commit();
                        break;
                }
            }

        });
    }


    public static class FooFragment extends Fragment {
        // The onCreateView method is called when Fragment should create its View object hierarchy,
        // either dynamically or via XML layout inflation.
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            // Defines the xml file for the fragment
            return inflater.inflate(R.layout.abouthelp_faq, parent, false);
        }

        // This event is triggered soon after onCreateView().
        // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            // Setup any handles to view objects here
            // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        }
    }

}