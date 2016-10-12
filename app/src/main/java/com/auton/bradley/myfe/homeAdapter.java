package com.auton.bradley.myfe;

/**
 * Created by Bradley on 12/10/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.auton.bradley.myfe.R.attr.title;


public class homeAdapter extends ArrayAdapter<String> {

    //declarations
    int[] imgs={};
    String[] titles={};
    Context c;
    LayoutInflater inflater;

    public homeAdapter(Context context, String[] titles, int[] imgs) {
        super(context, R.layout.home_list_item,titles);

        this.c=context;
        this.titles=titles;
        this.imgs=imgs;

    }

    public class ViewHolder {
        TextView title;
        ImageView img;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView==null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.home_list_item,null);
        }

        final ViewHolder holder=new ViewHolder();

        holder.title=(TextView) convertView.findViewById(R.id.home_list_item_text);
        holder.img=(ImageView)  convertView.findViewById(R.id.home_list_item_image);

        holder.img.setImageResource(imgs[position]);
        holder.title.setText(titles[position]);

        return convertView;
    }

}