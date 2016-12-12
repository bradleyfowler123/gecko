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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        ArrayList<String> test = new ArrayList<>();
        RequestCreator[] urls = new RequestCreator[4];
        for (int i = 0; i < 3; i++) {
            Log.d("ujkbl;",intent.getStringExtra(Integer.toString(i)));
            test.add(intent.getStringExtra(Integer.toString(i)));
            urls[i] = (Picasso.with(getBaseContext()).load(R.drawable.altontowers));

        }
        ListView results_list = (ListView) findViewById(R.id.activity_search_list);                              // locate the list object in the home tab
        searchResultsAdapter adapter = new searchResultsAdapter(getBaseContext(),test,test,urls);
        results_list.setAdapter(adapter);
    }
}


// adapter used for to show your activities as a list view in profile agenda sub tab
class searchResultsAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private ArrayList<String> names;
    private ArrayList<String> nextActivities;
    private RequestCreator[] profilePics;
    private Context c;

    // define a function that can be used to declare this custom adapter class
    searchResultsAdapter(Context context, ArrayList<String> names, ArrayList<String> nextActivities, RequestCreator[] profilePics) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.search_results_list_item, names);
        this.c = context;
        this.names = names;
        this.nextActivities = nextActivities;
        this.profilePics = profilePics;
    }

    // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView name;
        TextView nextActivity;
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
        holder.name = (TextView) convertView.findViewById(R.id.sr_list_item_friends);
        holder.nextActivity = (TextView) convertView.findViewById(R.id.sr_list_item_activity);
        holder.img = (ImageView) convertView.findViewById(R.id.sr_list_item_image);
        // populate the title and image with data for a list item
        holder.name.setText(names.get(position));
        holder.nextActivity.setText(nextActivities.get(position));
        profilePics[position].into(holder.img);
        // return the updated view
        return convertView;
    }

}