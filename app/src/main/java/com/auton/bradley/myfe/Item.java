package com.auton.bradley.myfe;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Bradley on 13/10/2016.
 * Custom Class used for search elv
 */

                            // class definition
class Item {
                            // parameter declarations
    String Option;                                                                                  // Contains the title of the list group
    String Selected;                                                                                // Used to recorded the current element selection
    boolean MultiSelect;
    ArrayList<String> mSelected=new ArrayList<>();
    ArrayList<String> elements=new ArrayList<>();                                                   // array containing all of the elements within a list group
                            // function used to declare an Item object with a list group title and default selection
    Item(String option, String selected, boolean multiSelect) {                                                          // Item(ListGroupTitle, defaultSelection)
        this.Option=option;
        if(multiSelect) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(selected);
            this.mSelected = temp;
        }
        else {
            this.Selected=selected;
        }
        this.MultiSelect=multiSelect;
    }
}
