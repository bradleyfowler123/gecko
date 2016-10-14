package com.auton.bradley.myfe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                                    // variable declarations
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);         // enables easy access to the root search xml
        final ExpandableListView elv=(ExpandableListView) rootView.findViewById(R.id.search_list);  // locate the elv in this view
        final ArrayList<Item> item=getData();                                                       // generate data which will be used to populate the el
                                    // generate the elv
        searchAdapter adapter = new searchAdapter(getActivity(),item);                              // define the adapter to be used to generate elv
        elv.setAdapter(adapter);                                                                    // assign this adapter to the elv
                                    // handle clicks on the elv
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {                 // setup function to listen for a click on the child elements
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPos, int childPos, long id) {   // functions specifies what happens upon a child element click
            //    Toast.makeText(getContext(),item.get(groupPos).elements.get(childPos)+" Selected",Toast.LENGTH_SHORT).show();
                item.get(groupPos).Selected = item.get(groupPos).elements.get(childPos);            // set the new selected item within the elv data
                elv.collapseGroup(groupPos);                                                        // collapse the list view which causes the view to be regenerated and so new selected item will be shown
                return false;
            }
        });

        return rootView;                                                                            // return the search view (and everything below) to the main activity so it can be shown
    }

                                    // function that generates the elv data
    private ArrayList<Item> getData() {                                                             // generates an array containing 3 Item class objects
        Item i1=new Item("Cost","Any");                                                             // Define an Item object call i1
        i1.elements.add("Free");                                                                    // child elements below
        i1.elements.add("Up to £20");
        i1.elements.add("Up to £50");
        i1.elements.add("Up to £200");
        i1.elements.add("Any");
        i1.elements.add("Custom");

        Item i2=new Item("Categories","Sport");                                                     // Item.Option is set by the arg1 and Item.Selected is set by arg 2
        i2.elements.add("Sport");
        i2.elements.add("ele 2.2");

        Item i3=new Item("item three","one");                                                       // ie Item(ListGroupTitle, defaultSelection)
        i3.elements.add("one");
        i3.elements.add("ele 3.2");

        ArrayList<Item> allItems=new ArrayList<>();                                                 // append all Item objects into an ArrayList
        allItems.add(i1);
        allItems.add(i2);
        allItems.add(i3);

        return allItems;
    }
}