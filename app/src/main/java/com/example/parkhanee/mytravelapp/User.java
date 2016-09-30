package com.example.parkhanee.mytravelapp;

/**
 * Created by parkhanee on 2016. 9. 22..
 */
public class User {
    private String user_name;
    private Boolean isFB;
    private String user_id;
    private String lat;
    private String lng;

    // constructors
    public User(String user_id,String user_name,Boolean isFB){
        super();
        this.user_id = user_id;
        this.user_name = user_name;
        this.isFB = isFB;
    }

    public User(String user_id,String user_name,Boolean isFB,String lat, String lng){
        super();
        this.user_id = user_id;
        this.user_name = user_name;
        this.isFB = isFB;
        this.lat = lat;
        this.lng = lng;
    }

    public User(){}


    //getters and setters
    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public Boolean getFB() {
        return isFB;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setFB(Boolean FB) {
        isFB = FB;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


    @Override
    public String toString() {
        return "User [user_id=" + user_id + ", user_name=" + user_name + ", fb=" + isFB
                + "]";
    }
}

