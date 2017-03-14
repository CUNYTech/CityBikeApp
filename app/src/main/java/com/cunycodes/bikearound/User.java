package com.cunycodes.bikearound;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String membership;
    public String identifier;

    public User(){

    }

    public User(String user, String email, String membership, String identifier){
        this.username = user;
        this.email = email;
        this.membership = membership;
        this.identifier = identifier;
    }

}
