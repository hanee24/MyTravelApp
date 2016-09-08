package com.example.parkhanee.mytravelapp;

/**
 * Created by parkhanee on 2016. 9. 8..
 */
public class Folder {
    private String name;
    private String desc;

    public Folder (String name, String desc){
        super();
        this.name = name;
        this.desc = desc;
    }


    //getters and setters

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
