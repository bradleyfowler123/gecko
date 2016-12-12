package com.auton.bradley.myfe;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        ArrayList<String> testTitles = new ArrayList<>();
        ArrayList<String> testLocations = new ArrayList<>();
        RequestCreator[] urls = new RequestCreator[6];
        String result = "";
        for (int i = 0; i < 6; i++) {
            result = result + intent.getStringExtra(Integer.toString(i));

            testTitles.add("Alton Towers");
            testLocations.add("Alton, Cheshire");
            urls[i] = (Picasso.with(getBaseContext()).load(R.drawable.altontowers));
        }
        Log.d("Result!!!!", result);
        ListView results_list = (ListView) findViewById(R.id.activity_search_list);                              // locate the list object in the home tab
        searchResultsAdapter adapter = new searchResultsAdapter(getBaseContext(),testTitles,testLocations,urls);
        results_list.setAdapter(adapter);

        results_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // if user signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null) {       // upload selection to there agenda
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference agendaItem = database.child("users").child(user.getUid()).child("Agenda").push();
                    agendaItem.child("activity").setValue("Activity One");
                    agendaItem.child("company").setValue("Myfe Inc");
                    agendaItem.child("date").setValue("25/12/16");
                    agendaItem.child("time").setValue("0900");
                    Toast.makeText(getBaseContext(),"Added to Agenda",Toast.LENGTH_SHORT).show();
                }
             //   Intent intent = new Intent(getBaseContext(), MainActivity.class);     // if there is facebook data
             //   startActivity(intent);
            }
        });



    }
}


// adapter used for to show your activities as a list view in profile agenda sub tab
class searchResultsAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private ArrayList<String> activityTitles;
    private ArrayList<String> activityLocations;
    private RequestCreator[] activityPics;
    private Context c;

    // define a function that can be used to declare this custom adapter class
    searchResultsAdapter(Context context, ArrayList<String> titles, ArrayList<String> locations, RequestCreator[] pics) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.search_results_list_item, titles);
        this.c = context;
        this.activityTitles = titles;
        this.activityLocations = locations;
        this.activityPics = pics;
    }

    // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView activityTitle;
        TextView activityLocation;
        ImageView img;
    }

    // function that generates the list view
    @Override
    public
    @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup nullParent = null;
            convertView = inflater.inflate(R.layout.search_results_list_item, nullParent);
        }
        // find the views within the list
        final ViewHolder holder = new ViewHolder();
        holder.activityTitle = (TextView) convertView.findViewById(R.id.sr_list_item_title);
        holder.activityLocation = (TextView) convertView.findViewById(R.id.sr_list_item_location);
        holder.img = (ImageView) convertView.findViewById(R.id.sr_list_item_image);
        // populate the title and image with data for a list item
        holder.activityTitle.setText(activityTitles.get(position));
        holder.activityLocation.setText(activityLocations.get(position));
        activityPics[position].into(holder.img);
        // return the updated view
        return convertView;
    }

}