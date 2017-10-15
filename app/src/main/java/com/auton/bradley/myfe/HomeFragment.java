package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
     fragment that handles the home tab
*/

public class HomeFragment extends Fragment {
                                // global variable declarations
    private ListView home_list;
    private homeAdapter adapter;
    private ArrayList<AgendaClass> listItems;
    private ArrayList<String> listTitles;
    private Map<String, ArrayList<String>> activityFriendGoingNumbers = new HashMap<>();
    private Map<String, ArrayList<String>> activityFriendInterestedNumbers = new HashMap<>();
    private ArrayList<String> myInterests = new ArrayList<>();
    private FirebaseAnalytics mFirebaseAnalytics;
    private Boolean flag_loading = true; private int loaded_count = 0;

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

                                   // get view elements and setup environment
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);                   // enables easy access to the root search xml
        home_list = (ListView) rootView.findViewById(R.id.home_list);                               // locate the list object in the home tab
        home_list.setEmptyView(rootView.findViewById(R.id.home_empty_list_item));

                                    // if we have data generate a list
        if (listTitles!=null) {
            adapter = new homeAdapter(getActivity(), listItems, listTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, myInterests);
            home_list.setAdapter(adapter);
        }

                                    // show detailed activity view when list item clicked upon
        home_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override                   // show detailed view of activity
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            // analytics
                Bundle params = new Bundle();
                params.putString("activity_name", listItems.get(i).activity);
                params.putBoolean("event", listItems.get(i).event);
                params.putInt("price", listItems.get(i).price);
                params.putDouble("distance_away", listItems.get(i).distAway);
                params.putInt("position_in_list", i);
                params.putBoolean("signed_in", (FirebaseAuth.getInstance().getCurrentUser()!=null));
                mFirebaseAnalytics.logEvent("home_list_click", params);
                            // show detailed view
                Intent intent = new Intent(getActivity(),DetailedItemActivity.class);
                intent.putExtra("ref",listItems.get(i).ref);
                intent.putExtra("from", "home");
                intent.putExtra("interests", ((MainActivity) getActivity()).interested);
                startActivity(intent);
            }
        });

        /*
                        // when user scrolls and near the bottom load more data
        home_list.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                // offset by 1 list item so start loading on second to last item
                if(firstVisibleItem+visibleItemCount >= totalItemCount - 1)
                {       // provided we are not already loading data
                    MainActivity mainActivity = (MainActivity) getActivity();

                    if(!mainActivity.activity_loading_flag && !mainActivity.event_loading_flag)
                    {       // load some more items
                        mainActivity.activity_loading_flag = false;
                        mainActivity.event_loading_flag = false;
                        mainActivity.getNSetHomeFeedData();
                    }
                }
            }
        });

        */

        return rootView;                                                                            // return the home view (and everything below) to the main activity so it can be shown
    }

                        // function to update the list items shown in the home list
    public void storeData(ArrayList<AgendaClass> sortedList2, ArrayList<String> strings, Map<String, ArrayList<String>> actFriGoNums, Map<String, ArrayList<String>> actFriIntNums,  ArrayList<String> interests, Boolean forceRemake){

        // get data
        listItems = sortedList2;
        listTitles = strings; // some necessary crap
        activityFriendGoingNumbers = actFriGoNums;
        activityFriendInterestedNumbers = actFriIntNums;
        myInterests = interests;

        // update list view with new data
        if (home_list!=null && getActivity()!=null) {
            if (adapter==null || forceRemake){                  // force remake runs whenever user filters with preferences
                adapter = new homeAdapter(getActivity(), listItems, listTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, myInterests);  // populate new list
                home_list.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();}                                                    // repopulate existing list
        }
    }
                             // setup the home menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();                                                                               // remove current menu
        final Boolean[] yes = {true};                                                               // we need to remake the list just before it tries to filter so the the data it is filtering from is up to date
        inflater.inflate(R.menu.menu_home,menu);                                                    // add home one

                                // display either login or logout
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);
            menu.getItem(4).setVisible(true);   // settings
        } else {
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);   // settings
        }

                            // set up search button
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}
            @Override               // on text entered
            public boolean onQueryTextChange(String searchQuery) {
                if (listTitles != null && listTitles.size()>1) {
                    flag_loading = true;
                    if (yes[0]) {                                                                       // if first list tap, remake list
                        adapter = new homeAdapter(getActivity(), listItems, listTitles, activityFriendGoingNumbers, activityFriendInterestedNumbers, myInterests);
                        home_list.setAdapter(adapter);
                        yes[0] = false;
                    }
                    adapter.filter(searchQuery.trim());                                                 // update the list items
                    home_list.invalidate();                                                             // ensure list refresh
                    return true;
                }
                else return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                flag_loading = false;
                return true;
            }
        });
    }
}
                        // adapter used for to populate activities in home list
class homeAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
                                // declare variables of this class
    private ArrayList<AgendaClass> listData;                                                        // the filtered list data to show
    private ArrayList<String> myInterests = new ArrayList<>();
    private Context c;
    private ArrayList<String> arraySearchList;                                                      // backup data and array search list contain all of the unfiltered data
    private ArrayList<AgendaClass> backupData;
    private Map<String, ArrayList<String>> activityFriendGoingNumbers = new HashMap<>();
    private Map<String, ArrayList<String>> activityFriendInterestedNumbers = new HashMap<>();
    private FirebaseAnalytics mFirebaseAnalytics;
                                // define a function that can be used to declare this custom adapter class
    homeAdapter(Context context, ArrayList<AgendaClass> listData, ArrayList<String> activityTitles, Map<String, ArrayList<String>> actFriGoNo, Map<String, ArrayList<String>> actFriIntNums, ArrayList<String> interests) {     // arguments set the context, texts and images for this adapter class
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
        TextView activityTitle, activityLocation, activityPrice;
        TextView noFriGoing; TextView noFriInt; TextView noTotGoing;
        ImageView img, interestedIV;
        View addToCal,interested,totalGoing;
    }                          // function that generates the list view, runs for every list item
    @Override
    @NonNull
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
                                // if the view is empty, get it
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup nullParent = null;
            convertView = inflater.inflate(R.layout.home_list_item, nullParent);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

                        // find the views within the list
        final ViewHolder holder = new ViewHolder();
        holder.activityTitle = (TextView) convertView.findViewById(R.id.sr_list_item_title);
        holder.activityLocation = (TextView) convertView.findViewById(R.id.sr_list_item_location);
        holder.activityPrice = (TextView) convertView.findViewById(R.id.sr_list_item_price);
        holder.noFriGoing = (TextView) convertView.findViewById(R.id.hli_friends_going);
        holder.noFriInt = (TextView) convertView.findViewById(R.id.hli_friends_interested);
        holder.noTotGoing = (TextView) convertView.findViewById(R.id.hli_total_going);
        holder.img = (ImageView) convertView.findViewById(R.id.sr_list_item_image);
        holder.interestedIV= (ImageView) convertView.findViewById(R.id.home_interested_star);
        holder.addToCal = convertView.findViewById(R.id.sr_add_to_calander);
        holder.interested = convertView.findViewById(R.id.home_interested);
        holder.totalGoing = convertView.findViewById(R.id.home_total_going);

                                // populate the texts and images with data for a list item
        AgendaClass listItem = listData.get(position);
        holder.activityTitle.setText(listItem.activity);
        if (listItem.distAway == -1) {
            holder.activityLocation.setText(String.format("%s " + listItem.location.split(",")[0], new String(Character.toChars(0x1F4CD))));
        }
        else {
            holder.activityLocation.setText(String.format("%s %s km away", new String(Character.toChars(0x1F4CD)), Double.toString(listItem.distAway)));
        }
        if (listItem.price == 0) {
            holder.activityPrice.setVisibility(View.GONE);
        }
        else {
            holder.activityPrice.setVisibility(View.VISIBLE);
            holder.activityPrice.setText(String.format("£%s", Integer.toString(listItem.price)));
        }
        RequestCreator activityImg = Picasso.with(getContext()).load(listItem.image).placeholder(R.drawable.homerectangle).error(R.drawable.homerectangle);
        activityImg.centerCrop().resize(1000,700).into(holder.img);
     /*   if (listItem.event) {
            holder.totalGoing.setVisibility(View.VISIBLE);
            holder.noTotGoing.setText(String.format("%s", Integer.toString(listItem.totalgoing)));
        }
        else {
     */       holder.totalGoing.setVisibility(View.GONE);
      //  }
                    // display numbers and stars on bottom banners
        if (myInterests.contains(listItem.ref)) holder.interestedIV.setImageResource(R.mipmap.ic_star_fill);
        else holder.interestedIV.setImageResource(R.mipmap.ic_star_empty);
        if (!activityFriendGoingNumbers.isEmpty()) {
            if (activityFriendGoingNumbers.get(listItem.ref)!=null) {
                holder.noFriGoing.setText(String.valueOf(activityFriendGoingNumbers.get(listItem.ref).size()));
            }
            else {
                holder.noFriGoing.setText(" ");
            }
        }

        if (!activityFriendInterestedNumbers.isEmpty()) {
            if (activityFriendInterestedNumbers.get(listItem.ref)!=null) {
                holder.noFriInt.setText(String.valueOf(activityFriendInterestedNumbers.get(listItem.ref).size()));
            }
            else {
                holder.noFriInt.setText(" ");
            }
        }

                             // add an on long click listener for the add to calendar button in each list item
        holder.addToCal.setOnLongClickListener(new onLongClickListenerPosition(position) {
            @Override
            public boolean onLongClick(View view) {     // inform user of how many friends are going
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    ArrayList<String> friendsGoing = activityFriendGoingNumbers.get(listData.get(this.position).ref);
                    if (friendsGoing != null) {
                        CharSequence[] cs = friendsGoing.toArray(new CharSequence[friendsGoing.size()]);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Friends Going")
                                .setPositiveButton("okay", null)
                                .setItems(cs, null)
                                .create()
                                .show();
                    }
                    else {
                        Toast.makeText(getContext(),"None of your friends are going to " + listData.get(this.position).activity,Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
        });

                            // add item to your calendar
        holder.addToCal.setOnClickListener(new onClickListenerPosition(position) {
            @Override
            public void onClick(final View view) {
                // if user not logged in
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {                            // tell them to log in
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Sign in to add items to your agenda")
                            .setPositiveButton("okay", null)
                            .create()
                            .show();
                } else {              // if user logged in
                    final AgendaClass listItem = listData.get(this.position);
                    if (listItem.event) {           // if event add it straight away
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                        builder.setMessage("Add event to your calendar?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Snackbar snackbar = Snackbar
                                                .make(view, "Added to calendar", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        // upload selection to there agenda
                                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                        DatabaseReference agendaItem = database.child("users").child(user.getUid()).child("Agenda").push();
                                        HashMap<String, String> pushData = new HashMap<>();
                                        pushData.put("activity", listItem.activity);
                                        pushData.put("location", listItem.location);
                                        pushData.put("date", listItem.date);
                                        pushData.put("time", listItem.time);
                                        pushData.put("ref", listItem.ref);
                                        agendaItem.setValue(pushData);
                                        // analytics
                                        Bundle params = new Bundle();
                                        params.putString("from", "home_feed");
                                        params.putString("activity_name", listItem.activity);
                                        params.putBoolean("event", listItem.event);
                                        params.putInt("price", listItem.price);
                                        params.putDouble("distance_away", listItem.distAway);
                                        int interested; int going;
                                        if (activityFriendInterestedNumbers.get(listItem.ref)==null)  interested=0;
                                        else interested = activityFriendInterestedNumbers.get(listItem.ref).size();
                                        params.putInt("friends_interested", interested);
                                        if (activityFriendGoingNumbers.get(listItem.ref)==null) going = 0;
                                        else going = activityFriendGoingNumbers.get(listItem.ref).size();
                                        params.putInt("friends_going", going);
                                        mFirebaseAnalytics.logEvent("item_added_to_agenda", params);
                                    }
                                })                  // delete item
                                .setNegativeButton("No", null)
                                .create()
                                .show();
                    }
                    else {              // if activity get a date and time and then add it in main activity
                        MainActivity activity = (MainActivity) getContext();                            // schedule a time and add it to their agenda
                        Intent intent = new Intent(activity, EnterDateActivity.class);
                        intent.putExtra("title", listItem.activity);                           // data need to add item to calendar
                        intent.putExtra("location", listItem.location);
                        intent.putExtra("reference", listItem.ref);
                        activity.startActivityForResult(intent, 11);                                         // result is handled by main activity
                    }
                }
            }
        });

                        // toggle interested on list item
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
                    final AgendaClass listItem = listData.get(this.position);
                    String ref = listItem.ref;
                    if (myInterests.contains(ref)) {
                        myInterests.remove(ref);
                        Snackbar snackbar = Snackbar
                                .make(view, "Unmarked as interested", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        // analytics
                        Bundle params = new Bundle();
                        params.putString("from", "home_feed");
                        params.putString("activity_name", listItem.activity);
                        params.putBoolean("event", listItem.event);
                        params.putInt("price", listItem.price);
                        params.putDouble("distance_away", listItem.distAway);
                        params.putInt("position_in_list", this.position);
                        int interested; int going;
                        if (activityFriendInterestedNumbers.get(listItem.ref)==null)  interested=0;
                        else interested = activityFriendInterestedNumbers.get(listItem.ref).size();
                        params.putInt("friends_interested", interested);
                        if (activityFriendGoingNumbers.get(listItem.ref)==null) going = 0;
                        else going = activityFriendGoingNumbers.get(listItem.ref).size();
                        params.putInt("friends_going", going);
                        mFirebaseAnalytics.logEvent("item_unmarked_as_interested", params);
                    }
                    else {
                        myInterests.add(ref);
                        Snackbar snackbar = Snackbar
                                .make(view, "Marked as interested", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        // analytics
                        Bundle params = new Bundle();
                        params.putString("from", "home_feed");
                        params.putString("activity_name", listItem.activity);
                        params.putBoolean("event", listItem.event);
                        params.putInt("price", listItem.price);
                        params.putDouble("distance_away", listItem.distAway);
                        params.putInt("position_in_list", this.position);
                        int interested; int going;
                        if (activityFriendInterestedNumbers.get(listItem.ref)==null)  interested=0;
                        else interested = activityFriendInterestedNumbers.get(listItem.ref).size();
                        params.putInt("friends_interested", interested);
                        if (activityFriendGoingNumbers.get(listItem.ref)==null) going = 0;
                        else going = activityFriendGoingNumbers.get(listItem.ref).size();
                        params.putInt("friends_going", going);
                        mFirebaseAnalytics.logEvent("item_marked_as_interested", params);
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

                    // flash how many friends are interested going
        holder.interested.setOnLongClickListener(new onLongClickListenerPosition(position) {
            @Override
            public boolean onLongClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    ArrayList<String> friendsGoing = activityFriendInterestedNumbers.get(listData.get(this.position).ref);
                    if (friendsGoing != null) {
                        CharSequence[] cs = friendsGoing.toArray(new CharSequence[friendsGoing.size()]);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Friends Interested")
                                .setPositiveButton("okay", null)
                                .setItems(cs, null)
                                .create()
                                .show();
                    }
                    else {
                        Toast.makeText(getContext(),"None of your friends are interested in " + listData.get(this.position).activity,Toast.LENGTH_LONG).show();
                    }
                }
                return true;
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
    }

                    // function to remove list items not containing searched titles
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
        notifyDataSetChanged();                                                                     // update the list view - NOTE IT WILL SET BACKUP DATA TO THE LAST ONE WHERE THE ADAPTER WAS CREATED, NOT TO THE LATE NOTIFY DATASETCHANGED
    }
}
