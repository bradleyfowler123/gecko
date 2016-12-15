package com.auton.bradley.myfe;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;
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
        final MainActivity activity = (MainActivity) getActivity();
                                // get test data
        final int[] images={R.drawable.altontowers,R.drawable.climbing,R.drawable.gym,R.drawable.altontowers,R.drawable.altontowers,R.drawable.climbing,R.drawable.gym,R.drawable.altontowers};    // get the image data to be shown for the recommendations
        final String[] titles = getResources().getStringArray(R.array.recommendationsArray);                // get the names of the recommendations to display
        final String[] pris = getResources().getStringArray(R.array.pricesArray);
        final String[] locs = getResources().getStringArray(R.array.locationsArray);
                                    // format test data
        ArrayList<HomeListData> listData = new ArrayList<>(8);
        final ArrayList<String> searchTitles = new ArrayList<>(8);
        int[] color = new int[8];
        for (int i = 0; i < 8; i++) {
            HomeListData element = new HomeListData();
            element.setActivityTitle(titles[i]);
            searchTitles.add(titles[i]);
            element.setActivityLocation(locs[i]);
            element.setActivityPrice(pris[i]);
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),images[i]);
            color[i] = Palette.from(bitmap).generate().getDominantColor(0);
            element.setColor(Color.argb(200,Color.red(color[i]),Color.green(color[i]),Color.blue(color[i])));
            double darkness = 1-(0.299*Color.red(color[i]) + 0.587*Color.green(color[i]) + 0.114*Color.blue(color[i]))/255;
            if(darkness<0.4){
                element.setDark(false); // It's a light color
            }else{
                element.setDark(true); // It's a dark color
            }
            element.setActivityPic(Picasso.with(getContext()).load(images[i]));
            listData.add(element);
        }
                                   // populate the list
        adapter = new homeAdapter(getActivity(),listData,searchTitles);
        home_list.setAdapter(adapter);
                                   // handle clicks on the list items
        home_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),DetailedItemActivity.class);
                intent.putExtra("fbData", activity.facebookData);
                intent.putExtra("fbCon", activity.facebookConnected);
                intent.putExtra("title",searchTitles.get(i));
                intent.putExtra("image",images[i]);
                startActivity(intent);
            }
        });

        return rootView;                                                                            // return the home view (and everything below) to the main activity so it can be shown
    };


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_home,menu);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);
        } else {
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);
        }

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //*** setOnQueryTextFocusChangeListener ***
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

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                adapter.filter(searchQuery.trim());
                home_list.invalidate();
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });





    }



}





// adapter used for to show your activities as a list view in profile agenda sub tab
class homeAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private ArrayList<HomeListData> listData;
    private Context c;
    ArrayList<String> arraySearchList;
    ArrayList<HomeListData> backupData;

    // define a function that can be used to declare this custom adapter class
    homeAdapter(Context context, ArrayList<HomeListData> listData, ArrayList<String> activityTitles) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.home_list_item, activityTitles);
        this.c = context;
        this.listData = listData;
        arraySearchList = new ArrayList<>();
        arraySearchList.addAll(activityTitles);
        backupData = new ArrayList<>();
        backupData.addAll(listData);

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
    View getView(final int position, View convertView, @NonNull ViewGroup parent) {
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
        holder.activityTitle.setText(listData.get(position).getActivityTitle());
        holder.activityLocation.setText(listData.get(position).getActivityLocation());
        holder.activityPrice.setText(listData.get(position).getActivityPrice());
        listData.get(position).getActivityPic().into(holder.img);
        View btn = convertView.findViewById(R.id.sr_color);
        GradientDrawable bgShape = (GradientDrawable) btn.getBackground().getCurrent();
        bgShape.setColor(listData.get(position).getColor());
      //  holder.color.setBackgroundColor(colors[position]);
        if (this.listData.get(position).getDark()) {
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

                MainActivity activity = (MainActivity) getContext();
                Intent intent = new Intent(activity, EnterDateActivity.class);
                intent.putExtra("title", holder.activityTitle.getText());
                intent.putExtra("location", holder.activityLocation.getText());
                activity.startActivityForResult(intent, 1);

            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        Log.d("juubhjhbmn", arraySearchList.toString());
        listData.clear();
        if (charText.length() == 0) {
            listData.addAll(backupData);

        } else {
            Log.d("jhbmn", arraySearchList.toString());
            for (int i = 0; i < arraySearchList.size(); i++) {
                if (charText.length() != 0 && arraySearchList.get(i).toLowerCase(Locale.getDefault()).contains(charText)) {
                    listData.add(backupData.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

}
