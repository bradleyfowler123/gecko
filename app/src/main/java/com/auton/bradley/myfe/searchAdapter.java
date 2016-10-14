package com.auton.bradley.myfe;

/**
 * Created by Bradley on 12/10/2016.
 * adapter used for elv in search fragment
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;


                                    // Define the custom adapter class for our elv
class searchAdapter extends BaseExpandableListAdapter {
                                    // declare variables of class
    private ArrayList<Item> item;                                                                   // item array used to contain all of the elv data
    private LayoutInflater inflater;
                                    // define a function that can be used to declare this custom adapter class
    searchAdapter(Context c, ArrayList<Item> item) {
        this.item=item;                                                                             // store the elv data in the adapter ready to be used
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

                                    // function that returns a child elements data from the supplied data stored in the adapter
    @Override
    public Object getChild(int groupPos, int childPos) {
        return item.get(groupPos).elements.get(childPos);
    }

                                    // function that generates the view for the child elements within the elv
    @Override
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView==null) {
            final ViewGroup nullParent = null;
            convertView=inflater.inflate(R.layout.search_element_list_item,nullParent);
        }
                                    // populate a child element within a list group
        String child = (String) getChild(groupPos, childPos);                                               // return name of a particular child element
        TextView optionElement = (TextView) convertView.findViewById(R.id.search_element_list_item_text);   // return the textView of a particular child element
        optionElement.setText(child);                                                                       // set the name of a particular child element using the given data
                                    // highlight that child element if it is selected
        Item i=(Item) getGroup(groupPos);                                                           // return the list group object the child element is part of
        String selected = i.Selected;                                                               // return the selected value for that list group
        if(selected.equals(child)) {
            convertView.setBackgroundColor(Color.BLUE);                                             // colour blue if selected
        }
        else {
            convertView.setBackgroundColor(Color.TRANSPARENT);                                      // else no colour
        }
                                    // return the updated view
        return convertView;
    }

                                    // function that generates the view for the list group banners
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            final ViewGroup nullParent = null;
            convertView=inflater.inflate(R.layout.search_option_list_item,nullParent);
        }
                                    // variable declarations
        Item i=(Item) getGroup(groupPosition);                                                                      // return a list group's data
        TextView optionTitle = (TextView) convertView.findViewById(R.id.search_option_list_item_text);              // find the textView's on he banner to be set
        TextView selectedTitle = (TextView) convertView.findViewById(R.id.search_option_list_item_text_element);
                                    // data extraction
        String option = i.Option;                                                                   // list group title
        String selected = i.Selected;                                                               // list group's element selection
                                    // update view
        optionTitle.setText(option);
        selectedTitle.setText(selected);
        convertView.setBackgroundColor(Color.LTGRAY);                                               // set colour of group banner
                                    // return the updated view
        return convertView;
    }

    @Override
    public long getChildId(int arg0, int arg1){
        return 0;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;                                                                                // enable the clicking of child elements
    }

    @Override
    public int getGroupCount() {
        return item.size();                                                                         // return the number of list groups
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPos) {
        return item.get(groupPos);                                                                  // returns a group object given it's position
    }

    @Override
    public int getChildrenCount(int groupPos) {
        return item.get(groupPos).elements.size();                                                  // returns the number of elements in a given group
    }

}