package com.auton.bradley.myfe;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileFriendsFragment extends Fragment {

    public ProfileFriendsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // function that generates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile_friends, container, false);        // enables easy access to the root search xml
        ListView pa_list = (ListView) rootView.findViewById(R.id.profile_friend_list);             // locate the list object in the home tab

        ArrayList<String> activities = new ArrayList<>();
        ArrayList<String> companies = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        activities.add("Bradley Fowler");
        companies.add("some activity");
        dates.add("");

        profileFriendsAdapter adapter = new profileFriendsAdapter(getActivity(),activities,dates,companies);
        pa_list.setAdapter(adapter);

        return rootView;
    }
}


// adapter used for to show your activities as a list view in profile agenda sub tab
class profileFriendsAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
    // declare variables of this class
    private ArrayList<String> activities;
    private ArrayList<String> dates;
    private ArrayList<String> companies;
    private Context c;

    // define a function that can be used to declare this custom adapter class
    profileFriendsAdapter(Context context, ArrayList<String> activities, ArrayList<String> dates, ArrayList<String> companies) {     // arguments set the context, texts and images for this adapter class
        super(context, R.layout.profile_friends_list_item, activities);
        this.c = context;
        this.activities = activities;
        this.dates = dates;
        this.companies = companies;
    }

    // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView activity;
        TextView date;
        TextView companies;
    }

    // function that generates the list view
    @Override
    public
    @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup nullParent = null;
            convertView = inflater.inflate(R.layout.profile_friends_list_item, nullParent);
        }
        // find the views within the list
        final ViewHolder holder = new ViewHolder();
        holder.activity = (TextView) convertView.findViewById(R.id.tv_profile_friends_name);
        holder.date = (TextView) convertView.findViewById(R.id.tv_profile_friends_nextActivity);
        holder.companies = (TextView) convertView.findViewById(R.id.tv_other);
        // populate the title and image with data for a list item
        holder.activity.setText(activities.get(position));
        holder.date.setText(dates.get(position));
        holder.companies.setText(companies.get(position));
        // return the updated view
        return convertView;
    }

}
