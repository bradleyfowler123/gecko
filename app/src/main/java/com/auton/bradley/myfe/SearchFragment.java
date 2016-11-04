package com.auton.bradley.myfe;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/*
Java file to contain all class' related to the Search tab
 */



                        // fragment that handle the search tab
public class SearchFragment extends Fragment {

    public SearchFragment() {        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

                                    // Function that generates the view
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                                    // variable declarations
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);         // enables easy access to the root search xml
        final ExpandableListView elv=(ExpandableListView) rootView.findViewById(R.id.search_list);  // locate the elv in this view
        final ArrayList<Item> item=getData();                                                       // generate data which will be used to populate the el
  //  elv.setVerticalFadingEdgeEnabled(true);
                                    // generate the elv
        final searchAdapter adapter = new searchAdapter(getActivity(),item);                              // define the adapter to be used to generate elv
        elv.setAdapter(adapter);                                                                    // assign this adapter to the elv
                                    // handle clicks on the elv
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {                 // setup function to listen for a click on the child elements
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPos, final int childPos, long id) {   // functions specifies what happens upon a child element click
                final Item listGroup = item.get(groupPos);
                if(listGroup.MultiSelect) {                                                         // if group is multi select one
                    if(listGroup.mSelected.contains(listGroup.elements.get(childPos))){             // if item is already selected
                        listGroup.mSelected.remove(listGroup.elements.get(childPos));               // remove from selection
                    }
                    else {
                        listGroup.mSelected.add(listGroup.elements.get(childPos));                  // else add it to the selection
                    }
                    adapter.notifyDataSetChanged();                                                 // inform to update the elv display
                }
                else {                                                                              // if group is single select
                    if(listGroup.elements.size()==childPos+1) {                                     // handle custom requests
                        int inputType;                                                              // dialog input data type
                        final String dialogMessage, before, after;                                  // dialog title message, elv preview variables for custom entry
                        if(groupPos==0) {
                            inputType = 2;
                            dialogMessage = "Enter your maximum price of items";
                            before = "Up to £"; after = "";
                        }
                        else if (groupPos==2) {
                            inputType = 2;
                            dialogMessage = "Enter the day(s) for which you would like to find items";
                            before = ""; after = "";
                        }
                        else if (groupPos==3) {
                            inputType = 1;
                            dialogMessage = "Enter your desired location for the search";
                            before = ""; after = "";
                        }
                        else {
                            inputType = 2;
                            dialogMessage = "Set the maximum distance range of the search";
                            before = "< "; after = " miles";
                        }

                        final ViewGroup nullParent = null;
                        View promptsView = inflater.inflate(R.layout.input_prompt, nullParent);     // get input_prompts.xml view
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder.setView(promptsView);                                    // set input_prompts.xml to alert dialog builder

                        final EditText userInput = (EditText) promptsView.findViewById(R.id.InputPromptUserInput); // enable easy access to object
                        userInput.setInputType(inputType);                                          // set the dialog input data type
                        TextView promptMessage = (TextView) promptsView.findViewById(R.id.InputPromptMessage);
                        promptMessage.setText(dialogMessage);                                       // set the dialog prompt message
                                            // create the dialog
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Enter",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                listGroup.Selected = listGroup.elements.get(childPos);  // update the selected child item
                                                listGroup.CustomValue = userInput.getText();        // get the users entered text and save it
                                                listGroup.CustomValuePreview = before + listGroup.CustomValue.toString() + after; // store the preview text to be shown on list group
                                                elv.collapseGroup(groupPos);                        // collapse the list view which causes the view to be regenerated and so new selected item will be shown
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                dialog.cancel();
                                            }
                                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();                      // create alert dialog
                        alertDialog.show();                                                         // show it
                     }
                    else {                                                                          // if it's not the custom selection
                        listGroup.Selected = listGroup.elements.get(childPos);                      // set the new selected item for single selection group
                        elv.collapseGroup(groupPos);                                                // collapse the list view which causes the view to be regenerated and so new selected item will be shown
                    }
                }
                return false;
            }
        });
                                        // search button at bottom of tab
        Button button = (Button) rootView.findViewById(R.id.search_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Searching!",Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;                                                                            // return the search view (and everything below) to the main activity so it can be shown
    }

                                    // function that generates the elv data
    private ArrayList<Item> getData() {                                                             // generates an array containing 3 Item class objects
        Item i1=new Item("Cost", new ArrayList<>(Collections.singletonList("Any")),false);          // Define an Item object call i1
        i1.elements.add("Free");                                                                    // child elements below
        i1.elements.add("Up to £20");
        i1.elements.add("Up to £50");
        i1.elements.add("Up to £200");
        i1.elements.add("Any");
        i1.elements.add("Custom");

        Item i2=new Item("Categories", new ArrayList<>(Arrays.asList("Sport","Events")),true);      // Item.Option is set by the arg1 and default selection is set by arg 2
        i2.elements.add("Sport");
        i2.elements.add("Events");
        i2.elements.add("Food / Drink");
        i2.elements.add("Entertainment");
        i2.elements.add("Arts & Craft");

        Item i3=new Item("When",new ArrayList<>(Collections.singletonList("Today")),false);         // ie Item(ListGroupTitle, defaultSelection)
        i3.elements.add("Today");
        i3.elements.add("Tomorrow");
        i3.elements.add("Select Day(s)");

        Item i4=new Item("Location",new ArrayList<>(Collections.singletonList("Current")),false);
        i4.elements.add("Current");
        i4.elements.add("Set Location");

        Item i5=new Item("Distance",new ArrayList<>(Collections.singletonList("< 10 miles")),false);
        i5.elements.add("< 2 miles");
        i5.elements.add("< 10 miles");
        i5.elements.add("< 30 miles");
        i5.elements.add("Set Distance");

        ArrayList<String> temp = new ArrayList<>();
        Item i6=new Item("Other", temp,true);
        i6.elements.add("Family Friendly");
        i6.elements.add("Indoor");

        ArrayList<Item> allItems=new ArrayList<>();                                                 // append all Item objects into an ArrayList
        allItems.add(i1);
        allItems.add(i2);
        allItems.add(i3);
        allItems.add(i4);
        allItems.add(i5);
        allItems.add(i6);

        return allItems;
    }
}



                        // adapter used for elv in search fragment
class searchAdapter extends BaseExpandableListAdapter {                                             // Define the custom adapter class for our elv
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
        if(i.MultiSelect) {                                                                         // if the list group is a multi-select one
            if(i.mSelected.contains(child)) {
                convertView.setBackgroundColor(Color.BLUE);                                         // set all selected elements to blue
            }
            else {
                convertView.setBackgroundColor(Color.TRANSPARENT);                                  // and everything else to transparent
            }
        }
        else {                                                                                      // if the list group was a single select one
            if(i.Selected.equals(child)) {
                convertView.setBackgroundColor(Color.BLUE);                                         // set the selected item to blue
            }
            else {
                convertView.setBackgroundColor(Color.TRANSPARENT);                                  // else no colour
            }
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
        String selected;                                                                            // used to contain the preview selection in banner
        if(i.MultiSelect) {
            if(i.mSelected.size() > 2) {                                                            // when more than two items selected in multi-select
                selected = TextUtils.join(", ",i.mSelected.subList(0,2)) + ", ...";                 // truncate preview
            }
            else {
                selected = TextUtils.join(", ",i.mSelected);
            }
        }
        else {                                                                                      // if single selection
            if (i.elements.get(i.elements.size()-1).equals(i.Selected)) {                           // if it was a custom on
                selected = i.CustomValuePreview;                                                    // set the preview selection to the custom value
            }
            else {
                selected = i.Selected;                                                              // set the preview selection to the selected child name
            }
        }                                                                                           // list group's element selection for single selection group
        // update view
        optionTitle.setText(option);                                                                // set the banner name
        selectedTitle.setText(selected);                                                            // set preview text
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