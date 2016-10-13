package com.auton.bradley.myfe;

import java.util.ArrayList;

/**
 * Created by Bradley on 13/10/2016.
 */

public class Item {

    public String Option;
    public ArrayList<String> elements=new ArrayList<String>();

    public Item(String Option) {
        this.Option=Option;
    }

    @Override
    public String toString() {
        return Option;
    }

}
