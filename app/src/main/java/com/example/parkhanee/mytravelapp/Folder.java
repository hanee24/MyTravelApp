package com.example.parkhanee.mytravelapp;

/**
 * Created by parkhanee on 2016. 9. 8..
 */
public class Folder {
    private String name;
    private String desc;
    private String date_start, date_end, created;
    private int id;
    private String user_id;


    public Folder (int id,String name,String user_id, String desc,String start, String end, String created){
        super();
        this.id = id;
        this.name = name;
        this.desc = desc;
        date_end = end;
        date_start = start;
        this.user_id = user_id;
        this.created = created;
    }

    public Folder (){

    }


    //getters and setters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getDate_end() {
        return date_end;
    }

    public String getDate_start() {
        return date_start;
    }

    public String getCreated() {
        return created;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
