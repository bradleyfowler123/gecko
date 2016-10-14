package com.auton.bradley.myfe;

/**
 * Created by Bradley on 12/10/2016.
 * adapter used for recommendations list view in home tab
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

                            // Define the custom adapter class for our list view
class homeAdapter extends ArrayAdapter<String> {
                            // declare variables of this class
    private int[] images={};
    private String[] titles={};
    private Context c;
                            // define a function that can be used to declare this custom adapter class
    homeAdapter(Context context, String[] titles, int[] images) {                                   // arguments set the context, titles and images for this adapter class
        super(context, R.layout.home_list_item,titles);
        this.c=context;
        this.titles=titles;
        this.images=images;
    }
                            // class definition used to store different views within the list view to be populated
    private class ViewHolder {
        TextView title;                                                                             // used to store the recommendations title text view
        ImageView img;
    }
                             // function that generates the list view
    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent){
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup nullParent = null;
            convertView=inflater.inflate(R.layout.home_list_item,nullParent);
        }
                            // find the views within the list
        final ViewHolder holder=new ViewHolder();
        holder.title=(TextView) convertView.findViewById(R.id.home_list_item_text);
        holder.img=(ImageView)  convertView.findViewById(R.id.home_list_item_image);
                            // populate the title and image with data for a list item
        holder.img.setImageResource(images[position]);
        holder.title.setText(titles[position]);
                            // return the updated view
        return convertView;
    }

}