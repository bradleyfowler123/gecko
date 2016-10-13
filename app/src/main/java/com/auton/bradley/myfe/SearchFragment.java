package com.auton.bradley.myfe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);


        final ExpandableListView elv=(ExpandableListView) rootView.findViewById(R.id.search_list);

        final ArrayList<Item> item=getData();

        searchAdapter adapter = new searchAdapter(getActivity(),item);
        elv.setAdapter(adapter);


        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPos, int childPos, long id) {
                Toast.makeText(getContext(),item.get(groupPos).elements.get(childPos)+" Selected",Toast.LENGTH_SHORT).show();
                item.get(groupPos).Selected = item.get(groupPos).elements.get(childPos);
                elv.collapseGroup(groupPos);
                return false;
            }
        });

        return rootView;
    }

    private ArrayList<Item> getData() {
        Item i1=new Item("Cost");
        i1.elements.add("Free");
        i1.elements.add("Upto £20");
        i1.elements.add("Upto £50");
        i1.elements.add("Upto £200");
        i1.elements.add("Any");
        i1.elements.add("Custom");
        i1.Selected = "Any";

        Item i2=new Item("Categories");
        i2.elements.add("Sport");
        i2.elements.add("ele 2.2");
        i2.Selected = "Sport";

        Item i3=new Item("item three");
        i3.elements.add("one");
        i3.elements.add("ele 3.2");
        i3.Selected = "one";

        ArrayList<Item> allItems=new ArrayList<Item>();
        allItems.add(i1);
        allItems.add(i2);
        allItems.add(i3);

        return allItems;
    }
}