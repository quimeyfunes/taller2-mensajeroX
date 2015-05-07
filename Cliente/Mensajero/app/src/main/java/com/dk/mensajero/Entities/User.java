package com.dk.mensajero.Entities;

import android.content.Context;

import com.dk.mensajero.DB.DbHelper;


public class User {

    private long id = 0;
    private String userId = "";
    private String phone = "";
    private String profilePicture = "";
    private String name = "";

    public User(){
    }

    public User(long id, String userId, String phone, String profilePicture, String name){
        this.id = id;
        this.userId = userId;
        this.phone = phone;
        this.profilePicture = profilePicture;
        this.name = name;
    }

    //Properties

    public long getId(){
        return this.id;
    }

    public String getUserId(){
        return this.userId;
    }

    public String getPhone(){
        return this.phone;
    }

    public String getProfilePicture(){
        return this.profilePicture;
    }

    public String getName(){
        return this.name;
    }

    public void setId(long id){
        this.id = id;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setProfilePicture(String profilePicture){
        this.profilePicture = profilePicture;
    }

    public void setName(String name){
        this.name = name;
    }


    //Methods

    public void save(Context context) {
        DbHelper helper = new DbHelper(context);
        helper.insertUser(this);
    }
}