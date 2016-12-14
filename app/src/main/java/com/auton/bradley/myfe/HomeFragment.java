package com.auton.bradley.myfe;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

/*
Java file to contain all class' related to the home tab
 */



                            // fragment that handles the home tab
public class HomeFragment extends Fragment {

    public HomeFragment() {        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

                                   // function that generates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                                   // variable declarations
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);                           // enables easy access to the root search xml
        ListView home_list = (ListView) rootView.findViewById(R.id.home_list);                              // locate the list object in the home tab
        int[] images={R.drawable.altontowers,R.drawable.climbing,R.drawable.gym,R.drawable.altontowers,R.drawable.altontowers,R.drawable.climbing,R.drawable.gym,R.drawable.altontowers};    // get the image data to be shown for the recommendations
        final String[] titles = getResources().getStringArray(R.array.recommendationsArray);                // get the names of the recommendations to display
        final String[] pris = getResources().getStringArray(R.array.pricesArray);
        final String[] locs = getResources().getStringArray(R.array.locationsArray);

        ArrayList<String> testTitles = new ArrayList<>();
        ArrayList<String> testLocations = new ArrayList<>();
        ArrayList<String> testPrices = new ArrayList<>();
        RequestCreator[] urls = new RequestCreator[8];
        for (int i = 0; i < 8; i++) {
            testTitles.add(titles[i]);
            testLocations.add(locs[i]);
            testPrices.add(pris[i]);
            urls[i] = Picasso.with(getContext()).load(images[i]);

        }
                                   // populate the list
        homeAdapter adapter = new homeAdapter(getActivity(),testTitles, testLocations, testPrices, urls);
        home_list.setAdapter(adapter);
                                   // handle clicks on the list items
        home_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(),R.string.home_toast_list_click,Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;                                                                            // return the home view (and everything below) to the main activity so it can be shown
    }
}



// adapter used for to show your activities as a list view in profile agenda sub tab
class homeAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private ArrayList<String> activityTitles;
    private ArrayList<String> activityLocations;
    private ArrayList<String> activityPrices;
    private RequestCreator[] activityPics;
    private Context c;

    // define a function that can be used to declare this custom adapter class
    homeAdapter(Context context, ArrayList<String> titles, ArrayList<String> locations, ArrayList<String> prices, RequestCreator[] pics) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.search_results_list_item, titles);
        this.c = context;
        this.activityTitles = titles;
        this.activityLocations = locations;
        this.activityPrices = prices;
        this.activityPics = pics;
    }

    // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView activityTitle;
        TextView activityLocation;
        TextView activityPrice;
        ImageView img;
        ImageView imageView;
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
        holder.activityPrice = (TextView) convertView.findViewById(R.id.sr_list_item_price);
        holder.img = (ImageView) convertView.findViewById(R.id.sr_list_item_image);
        holder.imageView = (ImageView) convertView.findViewById(R.id.sr_add_to_calander);
        // populate the title and image with data for a list item
        holder.activityTitle.setText(activityTitles.get(position));
        holder.activityLocation.setText(activityLocations.get(position));
        holder.activityPrice.setText(activityPrices.get(position));
        activityPics[position].into(holder.img);
        Picasso.with(c).load(R.drawable.ic_calander).into(holder.imageView);
        // return the updated view
        return convertView;
    }
}
