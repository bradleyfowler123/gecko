package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/*
Java file to contain all class' related to the home tab
 */



                            // fragment that handles the home tab
public class HomeFragment extends Fragment {

    private ListView home_list;
    private homeAdapter adapter;
    private ArrayList<AgendaClass> listItems;
    private ArrayList<String> listTitles;
    private Map<String, Integer> activityFriendGoingNumbers = new HashMap<>();
    private Map<String, Integer> activityFriendInterestedNumbers = new HashMap<>();
    private ArrayList<String> myInterests = new ArrayList<>();

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
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);                   // enables easy access to the root search xml
        home_list = (ListView) rootView.findViewById(R.id.home_list);                               // locate the list object in the home tab
        if (listTitles!=null) {
            adapter = new homeAdapter(getActivity(), listItems, listTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, myInterests);
            home_list.setAdapter(adapter);
        }
        // handle clicks on the list items
        home_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override                   // show detailed view of activity
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),DetailedItemActivity.class);
                intent.putExtra("data",listItems.get(i).getDataBundle1());
                intent.putExtra("from", "home");
                startActivity(intent);
            }
        });

        return rootView;                                                                            // return the home view (and everything below) to the main activity so it can be shown
    }

    public void storeData(ArrayList<AgendaClass> sortedList2, ArrayList<String> strings, Map<String, Integer> actFriGoNums, Map<String, Integer> actFriIntNums,  ArrayList<String> interests){
        listItems = sortedList2;
        listTitles = strings; // some necessary crap
        activityFriendGoingNumbers = actFriGoNums;
        activityFriendInterestedNumbers = actFriIntNums;
        myInterests = interests;
        if (home_list!=null && getActivity()!=null) {
            adapter = new homeAdapter(getActivity(), listItems, listTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, myInterests);
            home_list.setAdapter(adapter);
        }

    }

   /* HomeListData setFancyColor(HomeListData input) {
        final Bitmap bitmap;
        try {
            bitmap = Picasso.with(getContext()).load(input.getData().image).get();
            int color = Palette.from(bitmap).generate().getDominantColor(0);
            input.setColor(Color.argb(200,Color.red(color),Color.green(color),Color.blue(color)));
            double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
            if(darkness<0.4){
                input.setDark(false); // It's a light color
            }else{
                input.setDark(true); // It's a dark color
            }
        } catch (IOException e) {
            e.printStackTrace();
            input.setColor(R.color.com_facebook_blue);
            input.setDark(true);
        }
        return input;
    }
  */                              // setup the home menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();                                                                               // remove current menu
        inflater.inflate(R.menu.menu_home,menu);                                                    // add home one
                                // display either login or logout
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);
        } else {
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);
        }                       // set up search button
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
    private ArrayList<AgendaClass> listData;
    private ArrayList<String> myInterests = new ArrayList<>();
    private Context c;
    private ArrayList<String> arraySearchList;
    private ArrayList<AgendaClass> backupData;
    private Map<String, Integer> activityFriendGoingNumbers = new HashMap<>();
    private Map<String, Integer> activityFriendInterestedNumbers = new HashMap<>();
                                // define a function that can be used to declare this custom adapter class
    homeAdapter(Context context, ArrayList<AgendaClass> listData, ArrayList<String> activityTitles, Map<String, Integer> actFriGoNo, Map<String, Integer> actFriIntNums, ArrayList<String> interests) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.home_list_item, activityTitles);
        this.c = context;
        this.listData = listData;                                                                   // all of the data to be shown
        arraySearchList = new ArrayList<>();                                                        // contains all available titles, including the ones not shown
        arraySearchList.addAll(activityTitles);
        backupData = new ArrayList<>();                                                             // same but for all of the data
        backupData.addAll(listData);
        activityFriendGoingNumbers = actFriGoNo; activityFriendInterestedNumbers = actFriIntNums;
        myInterests = interests;
    }                          // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView activityTitle;
        TextView activityLocation;
        TextView noFriGoing; TextView noFriInt;
        ImageView img;
        ImageView interestedIV;
        View addToCal; View interested;
    }                          // function that generates the list view, runs for every list item
    @Override
    @NonNull
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
                                // if the view is empty, get it
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup nullParent = null;
            convertView = inflater.inflate(R.layout.home_list_item, nullParent);
        }                       // find the views within the list
        final ViewHolder holder = new ViewHolder();
        holder.activityTitle = (TextView) convertView.findViewById(R.id.sr_list_item_title);
        holder.activityLocation = (TextView) convertView.findViewById(R.id.sr_list_item_location);
        holder.noFriGoing = (TextView) convertView.findViewById(R.id.hli_friends_going);
        holder.noFriInt = (TextView) convertView.findViewById(R.id.hli_friends_interested);
        holder.img = (ImageView) convertView.findViewById(R.id.sr_list_item_image);
        holder.interestedIV= (ImageView) convertView.findViewById(R.id.home_interested_star);
        holder.addToCal = convertView.findViewById(R.id.sr_add_to_calander);
        holder.interested = convertView.findViewById(R.id.home_interested);
                                // populate the texts and images with data for a list item
        holder.activityTitle.setText(listData.get(position).activity);
        holder.activityLocation.setText(listData.get(position).location);
        if (!activityFriendGoingNumbers.isEmpty()) {
            String act = listData.get(position).ref;
            holder.noFriGoing.setText(String.valueOf(activityFriendGoingNumbers.get(act)));
        }
        if (!activityFriendInterestedNumbers.isEmpty()) {
            String act = listData.get(position).ref;
            if (activityFriendInterestedNumbers.get(act)!=null) holder.noFriInt.setText(String.valueOf(activityFriendInterestedNumbers.get(act)));
        }
        if (myInterests.contains(listData.get(position).ref)) holder.interestedIV.setImageResource(android.R.drawable.star_on);
        else holder.interestedIV.setImageResource(android.R.drawable.star_off);
        RequestCreator activityImg = Picasso.with(getContext()).load(listData.get(position).image);
        activityImg.centerCrop().resize(340,200).into(holder.img);
   /*     View btn = convertView.findViewById(R.id.sr_color);                                         // get the background rectangle
        GradientDrawable bgShape = (GradientDrawable) btn.getBackground().getCurrent();             // get its background
        bgShape.setColor(listData.get(position).getColor());                                        // set the color of it
        if (this.listData.get(position).getDark()) {                                                // handle dark or light background
            Picasso.with(c).load(R.drawable.ic_calendar_white).into(holder.addToCal);
            holder.activityTitle.setTextColor(Color.WHITE);
            holder.activityLocation.setTextColor(Color.WHITE);
//            holder.activityPrice.setTextColor(Color.WHITE);
        }
        else {
            Picasso.with(c).load(R.drawable.ic_calander).into(holder.addToCal);
            holder.activityTitle.setTextColor(Color.BLACK);
            holder.activityLocation.setTextColor(Color.BLACK);
 //           holder.activityPrice.setTextColor(Color.BLACK);
        }
 */                           // add an onclick listener for the add to calendar button in each list item
        holder.addToCal.setOnLongClickListener(new onLongClickListenerPosition(position) {
            @Override
            public boolean onLongClick(View view) {
                CharSequence number = holder.noFriGoing.getText(); String activity = listData.get(position).activity;
                String message;
                if (number == "0") message = "None of your friends have added  " + activity + " to their agenda yet";
                else if (number == "1") message = "1 friend is going to " + activity;
                else message = number + " friends are going " + activity;
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                return true;
            }
        });
        holder.addToCal.setOnClickListener(new onClickListenerPosition(position) {
            @Override
            public void onClick(View view) {
                                    // if user not logged in
                if(FirebaseAuth.getInstance().getCurrentUser()== null) {                            // tell them to log in
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Sign in to add items to your agenda")
                            .setPositiveButton("okay", null)
                            .create()
                            .show();
                }
                else {              // if user logged in
                    MainActivity activity = (MainActivity) getContext();                            // schedule a time and add it to their agenda
                    Intent intent = new Intent(activity, EnterDateActivity.class);
                    intent.putExtra("title", holder.activityTitle.getText());                           // data need to add item to calendar
                    intent.putExtra("location", holder.activityLocation.getText());
                    intent.putExtra("reference", listData.get(this.position).ref);
                    activity.startActivityForResult(intent, 1);                                         // result is handled by main activity
                }
            }
        });
        holder.interested.setOnClickListener(new onClickListenerPosition(position) {
            @Override
            public void onClick(View view) {
                // if user not logged in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user== null) {                            // tell them to log in
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Sign in to mark items as interested")
                            .setPositiveButton("okay", null)
                            .create()
                            .show();
                }
                else {              // if user logged in
                    if (myInterests.contains(listData.get(position).ref)) {
                        myInterests.remove(listData.get(position).ref);
                    }
                    else {
                        myInterests.add(listData.get(position).ref);
                    }
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference agendaItem = database.child("users").child(user.getUid()).child("Interested");
                    agendaItem.setValue(myInterests).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("complete?", Boolean.toString(task.isSuccessful()));
                        }
                    });
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
