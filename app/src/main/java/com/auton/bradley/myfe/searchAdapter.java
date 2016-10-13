package com.auton.bradley.myfe;

/**
 * Created by Bradley on 12/10/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class searchAdapter extends BaseExpandableListAdapter {

    private Context c;
    private ArrayList<Item> item;
    private LayoutInflater inflater;

    public searchAdapter(Context c, ArrayList<Item> item) {
        this.c=c;
        this.item=item;
        inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object getChild(int groupPos, int childPos) {
        return item.get(groupPos).elements.get(childPos);
    }

    @Override
    public long getChildId(int arg0, int arg1){
        return 0;
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView=inflater.inflate(R.layout.search_option_list_item,null);
        }

        String  child=(String) getChild(groupPos, childPos);
        TextView optionTitle = (TextView) convertView.findViewById(R.id.search_option_list_item_text);

        optionTitle.setText(child);


        return convertView;
    }


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public int getGroupCount() {
        return item.size();
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
        return item.get(groupPos);
    }

    @Override
    public int getChildrenCount(int groupPos) {
        return item.get(groupPos).elements.size();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView=inflater.inflate(R.layout.search_option_list_item,null);
        }

        Item i=(Item) getGroup(groupPosition);

        TextView optionTitle = (TextView) convertView.findViewById(R.id.search_option_list_item_text);

        String option = i.Option;
        optionTitle.setText(option);

        convertView.setBackgroundColor(Color.LTGRAY);

        return convertView;
    }
}