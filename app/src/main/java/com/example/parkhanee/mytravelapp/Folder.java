package com.example.parkhanee.mytravelapp;

/**
 * Created by parkhanee on 2016. 9. 8..
 */
public class Folder {
    private String name;
    private String desc;
    private String date_start, date_end;
    private int id;

    public Folder (int id,String name, String desc,String start, String end){
        super();
        this.id = id;
        this.name = name;
        this.desc = desc;
        date_end = end;
        date_start = start;
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
}
