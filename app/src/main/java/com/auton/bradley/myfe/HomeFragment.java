package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/*
Java file to contain all class' related to the home tab
 */



                            // fragment that handles the home tab
public class HomeFragment extends Fragment {

    private ListView home_list;
    private homeAdapter adapter;

    public HomeFragment() {        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

                                   // function that generates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
                                   // variable declarations
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);                           // enables easy access to the root search xml
        home_list = (ListView) rootView.findViewById(R.id.home_list);                              // locate the list object in the home tab
                                // get real data
        final ArrayList<HomeListData> listItems = new ArrayList<>();
        final ArrayList<String> listTitles = new ArrayList<>();
        final ArrayList<String> listKeys = new ArrayList<>();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference activityDataRef = database.child("activitydata").child("cambridge");
        activityDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, AgendaClass>> t = new GenericTypeIndicator<HashMap<String, AgendaClass>>() {};
                HashMap<String, AgendaClass> agendaData = dataSnapshot.getValue(t);              // get agenda data
                if (agendaData != null) {                                                           // if cambridge has activities
                    Iterator<AgendaClass> iterator = agendaData.values().iterator();                // parse out a list of friendClass'
                    Iterator<String> keys = agendaData.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = keys.next();
                        AgendaClass agendaItem = iterator.next();
                        agendaItem.ref = key;
                        // if already exists in list
                        if (listKeys.contains(key)) {          // remove it
                            listKeys.remove(key);
                            HomeListData listItem = new HomeListData(agendaItem);
                            listItem.setColor(R.color.com_facebook_blue);
                            listItem.setDark(true);
                            listItems.remove(listItem);
                            listTitles.remove(agendaItem.activity);
                        }               // add agenda item to list
                        listKeys.add(key);
                        HomeListData listItem = new HomeListData(agendaItem);
                        listItem.setColor(R.color.com_facebook_blue);
                        listItem.setDark(true);
                        listItems.add(listItem);
                        listTitles.add(agendaItem.activity);
                        Log.d("skjkmlsd", listItem.getData().familyfriendly.toString());
                    }
                    // populate the list
                    adapter = new homeAdapter(getActivity(),listItems,listTitles);                             // note searchTitles the strings that are search able, in this case just the titles
                    home_list.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("!!!!!!!!!!!!", databaseError.toString());
            }
        });
      /*                           // color
            final Bitmap image = Picasso.with(this).load("http://").get();
            color[i] = Palette.from(bitmap).generate().getDominantColor(0);
            element.setColor(Color.argb(200,Color.red(color[i]),Color.green(color[i]),Color.blue(color[i])));
            double darkness = 1-(0.299*Color.red(color[i]) + 0.587*Color.green(color[i]) + 0.114*Color.blue(color[i]))/255;
            if(darkness<0.4){
                element.setDark(false); // It's a light color
            }else{
                element.setDark(true); // It's a dark color
            }
    */
                                   // handle clicks on the list items
        home_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),DetailedItemActivity.class);
                intent.putExtra("data",listItems.get(i).getDataBundle1());
                startActivity(intent);
            }
        });

        return rootView;                                                                            // return the home view (and everything below) to the main activity so it can be shown
    }
                                // setup the home menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();                                                                               // remove current menu
        inflater.inflate(R.menu.menu_home,menu);                                                    // add home one
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {                                  // display either login or logout
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);
        } else {
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);
        }
                                // set up search button
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override               // on text entered
            public boolean onQueryTextChange(String searchQuery) {
                adapter.filter(searchQuery.trim());                                                 // update the list items
                home_list.invalidate();                                                             // ensure list refresh
                return true;
            }
        });                         // on other actions
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {                                // Do something when collapsed
                return true;  // Return true to collapse action view
            }
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {                                  // Do something when expanded
                return true;  // Return true to expand action view
            }
        });
    }
}

                        // adapter used for to populate activities in home list
class homeAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
                                // declare variables of this class
    private ArrayList<HomeListData> listData;
    private Context c;
    private ArrayList<String> arraySearchList;
    private ArrayList<HomeListData> backupData;
                                // define a function that can be used to declare this custom adapter class
    homeAdapter(Context context, ArrayList<HomeListData> listData, ArrayList<String> activityTitles) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.home_list_item, activityTitles);
        this.c = context;
        this.listData = listData;                                                                   // all of the data to be shown
        arraySearchList = new ArrayList<>();                                                        // contains all available titles, including the ones not shown
        arraySearchList.addAll(activityTitles);
        backupData = new ArrayList<>();                                                             // same but for all of the data
        backupData.addAll(listData);
    }                          // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView activityTitle;
        TextView activityLocation;
        TextView activityPrice;
        ImageView img;
        ImageView addToCal;
    }                          // function that generates the list view, runs for every list item
    @Override
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                                // if the view is empty, get it
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup nullParent = null;
            convertView = inflater.inflate(R.layout.home_list_item, nullParent);
        }                       // find the views within the list
        final ViewHolder holder = new ViewHolder();
        holder.activityTitle = (TextView) convertView.findViewById(R.id.sr_list_item_title);
        holder.activityLocation = (TextView) convertView.findViewById(R.id.sr_list_item_location);
        holder.activityPrice = (TextView) convertView.findViewById(R.id.sr_list_item_price);
        holder.img = (ImageView) convertView.findViewById(R.id.sr_list_item_image);
        holder.addToCal = (ImageView) convertView.findViewById(R.id.sr_add_to_calander);
                                // populate the texts and images with data for a list item
        holder.activityTitle.setText(listData.get(position).getData().activity);
        holder.activityLocation.setText(listData.get(position).getData().location);
        holder.activityPrice.setText(listData.get(position).getData().price);
        RequestCreator activityImg = Picasso.with(getContext()).load(listData.get(position).getData().image);
        activityImg.centerCrop().resize(340,200).into(holder.img);
        View btn = convertView.findViewById(R.id.sr_color);                                         // get the background rectangle
        GradientDrawable bgShape = (GradientDrawable) btn.getBackground().getCurrent();             // get its background
        bgShape.setColor(listData.get(position).getColor());                                        // set the color of it
        if (this.listData.get(position).getDark()) {                                                // handle dark or light background
            Picasso.with(c).load(R.drawable.ic_calendar_white).into(holder.addToCal);
            holder.activityTitle.setTextColor(Color.WHITE);
            holder.activityLocation.setTextColor(Color.WHITE);
            holder.activityPrice.setTextColor(Color.WHITE);
        }
        else {
            Picasso.with(c).load(R.drawable.ic_calander).into(holder.addToCal);
            holder.activityTitle.setTextColor(Color.BLACK);
            holder.activityLocation.setTextColor(Color.BLACK);
            holder.activityPrice.setTextColor(Color.BLACK);
        }
                            // add an onclick listener for the add to calendar button
        holder.addToCal.setOnClickListener(new onClickListenerPosition(position) {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser()== null) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Sign in to add items to your agenda")
                            .setPositiveButton("okay", null)
                            .create()
                            .show();
                }
                else {
                    MainActivity activity = (MainActivity) getContext();
                    Intent intent = new Intent(activity, EnterDateActivity.class);
                    intent.putExtra("title", holder.activityTitle.getText());                           // data need to add item to calendar
                    intent.putExtra("location", holder.activityLocation.getText());
                    Log.d("infdjk", listData.get(this.position).getData().ref);
                    intent.putExtra("reference", listData.get(this.position).getData().ref);
                    activity.startActivityForResult(intent, 1);                                         // result is handled by main activity
                }
            }
        });
        return convertView;                                                                         // return the updated view
    }
    @Override
    public int getCount() {
        return listData.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }                               // function to remove list items not containing searched titles
    void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());                                       // get the searched string
        listData.clear();                                                                           // remove all items from list
        if (charText.length() == 0) {
            listData.addAll(backupData);                                                            // upon first click of search button, add all of the items to the list

        } else {                                                                                    // when text entered
            for (int i = 0; i < arraySearchList.size(); i++) {                                      // cycle through titles until searched text is contained in a title
                if (charText.length() != 0 && arraySearchList.get(i).toLowerCase(Locale.getDefault()).contains(charText)) {
                    listData.add(backupData.get(i));                                                // add that list item to the list
                }
            }
        }
        notifyDataSetChanged();                                                                     // update the list view
    }
}
