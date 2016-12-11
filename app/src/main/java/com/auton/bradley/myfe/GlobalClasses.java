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

class friendClass {
    String date;
    String company;
    String activity;

    public friendClass(){

    }

    public friendClass(String date, String company, String activity) {
        this.date = date;
        this.company = company;
        this.activity = activity;
    }
}

class FriendUIDs {
    int friendNumber;
    String friendUID;

    public FriendUIDs(){

    }

    public FriendUIDs(int friendNumber, String friendUID) {
        this.friendNumber = friendNumber;
        this.friendUID = friendUID;
    }
}