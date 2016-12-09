package com.auton.bradley.myfe;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bradley on 13/10/2016.
 * File containing all global Custom Classes
 */

                            // class definition
class FacebookFriendData {
                            // parameter declarations
    ArrayList<String> names;
    ArrayList<String> ids;
    ArrayList<String> picUrls;

                            // function used to initialise a new class
    FacebookFriendData() {
        this.names = new ArrayList<>();
        this.ids = new ArrayList<>();
        this.picUrls = new ArrayList<>();
    }
}
