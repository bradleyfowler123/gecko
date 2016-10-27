package com.auton.bradley.myfe;

import java.util.ArrayList;

/**
 * Created by Bradley on 13/10/2016.
 * Custom Class used for storing user details
 */

                            // class definition
class User {
                            // parameter declarations
    boolean loggedIn = false;
    String email, password, name, dob;
                            // function used to declare an Item object with a list group title and default selection
    User() {                                                          // Item(ListGroupTitle, defaultSelection)
        this.loggedIn = false;                                                           // variable to store list group type
    }

    void LogIn(String email, String password, String name, String dob) {                                                          // Item(ListGroupTitle, defaultSelection)
        this.loggedIn = true;
        this.email = email;
        this.password = password;
        this.name = name;
        this.dob = dob;                                                             // variable to store list group type
    }
    void LogOut(){
        this.loggedIn = false;
        this.email = "";
        this.password = "";
        this.name = "";
        this.dob = "";
    }
}
