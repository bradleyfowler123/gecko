package com.auton.bradley.myfe;

/**
 * Created by Bradley on 13/10/2016.
 * Custom Class used for storing user details
 */

                            // class definition
class User {
                            // parameter declarations
    boolean loggedIn;
    String email, password, fbUserId, fbPassword, name, dob, agenda;
                            // function used by main activity to declare an empty user when not logged in
    User() {
        this.loggedIn = false;
    }
                            // function to easily create logged in user
    void LogIn(String email, String password, String name, String dob, String agenda, String fbUserId, String fbPassword) {                                                          // Item(ListGroupTitle, defaultSelection)
        this.loggedIn = true;
        this.email = email;
        this.password = password;
        this.fbUserId = fbUserId;
        this.fbPassword = fbPassword;
        this.name = name;
        this.dob = dob;
        this.agenda = agenda;
    }
                            // function to easily remove logged in user's data
    void LogOut(){
        this.loggedIn = false;
        this.email = "";
        this.password = "";
        this.fbUserId = "";
        this.fbPassword = "";
        this.name = "";
        this.dob = "";
    }
}

