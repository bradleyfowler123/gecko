package com.auton.bradley.myfe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

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
                    String[] dialogMessages = getResources().getStringArray(R.array.elvCustomDialogMessages);
                    if(listGroup.elements.size()==childPos+1) {                                     // handle custom requests
                        int inputType;                                                              // dialog input data type
                        final String dialogMessage, before, after;                                  // dialog title message, elv preview variables for custom entry
                        if(groupPos==0) {           // cost
                            inputType = 2;
                            dialogMessage = dialogMessages[0];
                            before = getString(R.string.search_previewCost); after = "";
                        }
                        else if (groupPos==2) {     // when
                            inputType = 2;
                            dialogMessage = dialogMessages[1];
                            before = ""; after = "";
                        }
                        else if (groupPos==3) {     // location
                            inputType = 1;
                            dialogMessage = dialogMessages[2];
                            before = ""; after = "";
                        }
                        else {                      // distance
                            inputType = 2;
                            dialogMessage = dialogMessages[3];
                            before = getString(R.string.search_previewDistanceBefore); after = getString(R.string.search_previewDistanceAfter);
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
                                .setPositiveButton(getString(R.string.search_dialog_enter),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                listGroup.Selected = listGroup.elements.get(childPos);  // update the selected child item
                                                listGroup.CustomValue = userInput.getText();        // get the users entered text and save it
                                                listGroup.CustomValuePreview = before + listGroup.CustomValue.toString() + after; // store the preview text to be shown on list group
                                                elv.collapseGroup(groupPos);                        // collapse the list view which causes the view to be regenerated and so new selected item will be shown
                                            }
                                        })
                                .setNegativeButton(getString(R.string.search_dialog_cancel),
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
                Intent intent = new Intent(getContext(),SearchActivity.class);
                for (int i = 0; i < item.size(); i++) {
                    intent.putExtra(Integer.toString(i),item.get(i).stringify());
                }
                startActivity(intent);
            }
        });

        return rootView;                                                                            // return the search view (and everything below) to the main activity so it can be shown
    }

                                    // function that generates the elv data
    private ArrayList<Item> getData() {                                                             // generates an array containing 3 Item class objects
        String[] cost = getResources().getStringArray(R.array.elvCostItems);
        Item i1=new Item(cost[0], new ArrayList<>(Collections.singletonList(cost[5])),false);          // Define an Item object call i1
        i1.elements.add(cost[1]);                                                                    // child elements below
        i1.elements.add(cost[2]);
        i1.elements.add(cost[3]);
        i1.elements.add(cost[4]);
        i1.elements.add(cost[5]);
        i1.elements.add(cost[6]);

        String[] category = getResources().getStringArray(R.array.elvCategoryItems);
        Item i2=new Item(category[0], new ArrayList<>(Arrays.asList(category[1],category[2])),true);      // Item.Option is set by the arg1 and default selection is set by arg 2
        i2.elements.add(category[1]);
        i2.elements.add(category[2]);
        i2.elements.add(category[3]);
        i2.elements.add(category[4]);
        i2.elements.add(category[5]);

        String[] when = getResources().getStringArray(R.array.elvWhenItems);
        Item i3=new Item(when[0],new ArrayList<>(Collections.singletonList(when[1])),false);         // ie Item(ListGroupTitle, defaultSelection)
        i3.elements.add(when[1]);
        i3.elements.add(when[2]);
        i3.elements.add(when[3]);

        String[] location = getResources().getStringArray(R.array.elvLocationItems);
        Item i4=new Item(location[0],new ArrayList<>(Collections.singletonList(location[1])),false);
        i4.elements.add(location[1]);
        i4.elements.add(location[2]);

        String[] distance = getResources().getStringArray(R.array.elvDistanceItems);
        Item i5=new Item(distance[0],new ArrayList<>(Collections.singletonList(distance[2])),false);
        i5.elements.add(distance[1]);
        i5.elements.add(distance[2]);
        i5.elements.add(distance[3]);
        i5.elements.add(distance[4]);

        String[] other = getResources().getStringArray(R.array.elvOtherItems);
        ArrayList<String> temp = new ArrayList<>();
        Item i6=new Item(other[0], temp,true);
        i6.elements.add(other[1]);
        i6.elements.add(other[2]);

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