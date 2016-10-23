package com.auton.bradley.myfe;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPos, int childPos, long id) {   // functions specifies what happens upon a child element click
            //    Toast.makeText(getContext(),item.get(groupPos).elements.get(childPos)+" Selected",Toast.LENGTH_SHORT).show();
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
                else {
                    if(listGroup.elements.size()==childPos+1) {
                        String message = "";
                        if(groupPos==0) {


                            int inputType = 2;


                            final ViewGroup nullParent = null;
                            View promptsView = inflater.inflate(R.layout.input_prompt, nullParent);                    // get prompts.xml view
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                            alertDialogBuilder.setView(promptsView);                                             // set prompts.xml to alertdialog builder

                            final EditText userInput = (EditText) promptsView.findViewById(R.id.InputPromptUserInput); // enable easy access to object
                            userInput.setInputType(inputType);

                            // set dialog message
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    // get user input and set it to result
                                                    // edit text
                                                    listGroup.Selected = "Custom";
                                                    listGroup.CustomValue = userInput.getText();                        // get the users entered text return data
                                                    elv.collapseGroup(groupPos);                                                    // collapse the list view which causes the view to be regenerated and so new selected item will be shown
                                                }
                                            })
                                    .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();



                        }
                     //   Toast.makeText(getContext(),item.get(groupPos).elements.get(childPos)+" Selected " + message,Toast.LENGTH_SHORT).show();
                    }
                    else {
                        listGroup.Selected = listGroup.elements.get(childPos);                          // set the new selected item for single selection group
                        elv.collapseGroup(groupPos);                                                    // collapse the list view which causes the view to be regenerated and so new selected item will be shown
                    }
                }
                return false;
            }
        });

        Button button = (Button) rootView.findViewById(R.id.search_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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