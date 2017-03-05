package com.cunycodes.bikearound;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by j on 3/4/2017.
 */

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String membership;

    public User(){

    }

    public User(String user, String email, String membership){
        this.username = user;
        this.email = email;
        this.membership = membership;
    }

}
