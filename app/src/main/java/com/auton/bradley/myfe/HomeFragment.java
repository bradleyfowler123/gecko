package com.auton.bradley.myfe;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/*
Java file to contain all class' related to the home tab
 */



                            // fragment that handles the home tab
public class HomeFragment extends Fragment {

    public HomeFragment() {        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

                                   // function that generates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                                   // variable declarations
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);                           // enables easy access to the root search xml
        ListView home_list = (ListView) rootView.findViewById(R.id.home_list);                              // locate the list object in the home tab
        int[] images={R.drawable.altontowers,R.drawable.climbing,R.drawable.gym,R.drawable.altontowers};    // get the image data to be shown for the recommendations
        final String[] titles = getResources().getStringArray(R.array.recommendationsArray);                // get the names of the recommendations to display
                                   // populate the list
        homeAdapter adapter = new homeAdapter(getActivity(),titles,images);
        home_list.setAdapter(adapter);
                                   // handle clicks on the list items
        home_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(),"Navigating to the website",Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;                                                                            // return the home view (and everything below) to the main activity so it can be shown
    }
}



                        // adapter used for recommendations list view in home tab
class homeAdapter extends ArrayAdapter<String> {                                                    // Define the custom adapter class for our list view
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
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent){
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



                        // custom class definition used for search elv
class Item {
    // parameter declarations
    String Option;                                                                                  // Contains the title of the list group
    String Selected;                                                                                // Used to recorded the current element selection
    boolean MultiSelect;                                                                            // identifies whether or not a list group is a multi select one
    Object CustomValue;                                                                             // used to store the custom value
    String CustomValuePreview;                                                                      // used to store the custom value formatted for preview
    ArrayList<String> mSelected=new ArrayList<>();
    ArrayList<String> elements=new ArrayList<>();                                                   // array containing all of the elements within a list group
    // function used to declare an Item object with a list group title and default selection
    Item(String option, ArrayList<String> selected, boolean multiSelect) {                                                          // Item(ListGroupTitle, defaultSelection)
        this.Option=option;                                                                         // variable to store list group name
        if(multiSelect) {
            this.mSelected = selected;                                                              // set the default selection
        }
        else {
            this.Selected=selected.get(0);                                                          // also for single group case
        }
        this.MultiSelect=multiSelect;                                                               // variable to store list group type
    }
}
