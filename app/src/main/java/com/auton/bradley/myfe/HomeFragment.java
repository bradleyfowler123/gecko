package com.auton.bradley.myfe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
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
                                // get test data
        int[] images={R.drawable.altontowers,R.drawable.climbing,R.drawable.gym,R.drawable.altontowers,R.drawable.altontowers,R.drawable.climbing,R.drawable.gym,R.drawable.altontowers};    // get the image data to be shown for the recommendations
        final String[] titles = getResources().getStringArray(R.array.recommendationsArray);                // get the names of the recommendations to display
        final String[] pris = getResources().getStringArray(R.array.pricesArray);
        final String[] locs = getResources().getStringArray(R.array.locationsArray);
                                    // format test data
        final ArrayList<String> testTitles = new ArrayList<>();
        ArrayList<String> testLocations = new ArrayList<>();
        ArrayList<String> testPrices = new ArrayList<>();
        RequestCreator[] urls = new RequestCreator[8];
        int[] color = new int[8];
        boolean[] dark = new boolean[8];
        for (int i = 0; i < 8; i++) {
            testTitles.add(titles[i]);
            testLocations.add(locs[i]);
            testPrices.add(pris[i]);
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),images[i]);
            color[i] = Palette.from(bitmap).generate().getDominantColor(0);
            color[i] = Color.argb(200,Color.red(color[i]),Color.green(color[i]),Color.blue(color[i]));
            double darkness = 1-(0.299*Color.red(color[i]) + 0.587*Color.green(color[i]) + 0.114*Color.blue(color[i]))/255;
            if(darkness<0.4){
                dark[i] = false; // It's a light color
            }else{
                dark[i] = true; // It's a dark color
            }
            urls[i] = Picasso.with(getContext()).load(images[i]);
        }
                                   // populate the list
        homeAdapter adapter = new homeAdapter(getActivity(),testTitles, testLocations, testPrices, urls,color,dark);
        home_list.setAdapter(adapter);
                                   // handle clicks on the list items
        home_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(),testTitles.get(i),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),DetailedItemActivity.class);
                startActivity(intent);
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
    private int[] colors;
    private boolean[] darks;
    private Context c;

    // define a function that can be used to declare this custom adapter class
    homeAdapter(Context context, ArrayList<String> titles, ArrayList<String> locations, ArrayList<String> prices, RequestCreator[] pics, int[] colors, boolean[] darks) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.home_list_item, titles);
        this.c = context;
        this.activityTitles = titles;
        this.activityLocations = locations;
        this.activityPrices = prices;
        this.activityPics = pics;
        this.colors = colors;
        this.darks = darks;
    }

    // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView activityTitle;
        TextView activityLocation;
        TextView activityPrice;
        ImageView img;
        ImageView imageView;
        TextView color;
    }

    // function that generates the list view
    @Override
    public
    @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup nullParent = null;
            convertView = inflater.inflate(R.layout.home_list_item, nullParent);
        }
        // find the views within the list
        final ViewHolder holder = new ViewHolder();
        holder.activityTitle = (TextView) convertView.findViewById(R.id.sr_list_item_title);
        holder.activityLocation = (TextView) convertView.findViewById(R.id.sr_list_item_location);
        holder.activityPrice = (TextView) convertView.findViewById(R.id.sr_list_item_price);
      //  holder.color = (TextView) convertView.findViewById(R.id.sr_color);
        holder.img = (ImageView) convertView.findViewById(R.id.sr_list_item_image);
        holder.imageView = (ImageView) convertView.findViewById(R.id.sr_add_to_calander);
        // populate the title and image with data for a list item
        holder.activityTitle.setText(activityTitles.get(position));
        holder.activityLocation.setText(activityLocations.get(position));
        holder.activityPrice.setText(activityPrices.get(position));
        activityPics[position].into(holder.img);
        View btn = convertView.findViewById(R.id.sr_color);
        GradientDrawable bgShape = (GradientDrawable) btn.getBackground().getCurrent();
        bgShape.setColor(colors[position]);
      //  holder.color.setBackgroundColor(colors[position]);
        if (this.darks[position]) {
            Picasso.with(c).load(R.drawable.ic_calendar_white).into(holder.imageView);
            holder.activityTitle.setTextColor(Color.WHITE);
            holder.activityLocation.setTextColor(Color.WHITE);
            holder.activityPrice.setTextColor(Color.WHITE);
        }
        else {
            Picasso.with(c).load(R.drawable.ic_calander).into(holder.imageView);
            holder.activityTitle.setTextColor(Color.BLACK);
            holder.activityLocation.setTextColor(Color.BLACK);
            holder.activityPrice.setTextColor(Color.BLACK);
        }
        // return the updated view

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar
                        .make(view, "Added to Calendar", Snackbar.LENGTH_SHORT)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(view, "Removed from calendar", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                            }
                        });

                snackbar.show();
            }
        });
        return convertView;
    }
}
