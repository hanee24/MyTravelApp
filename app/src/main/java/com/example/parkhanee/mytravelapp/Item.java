package com.example.parkhanee.mytravelapp;

/**
 * Created by parkhanee on 2016. 8. 12..
 */
public class Item {
    private int cat;
    private String title;
    private String picture;
    private String desc;
    private int dist;
    private String mapy, mapx;
    private int contentId;


    public Item(int cat,String title, String picture, String desc,int dist,String mapy,String mapx,int contentId){
        super();
        this.cat = cat;
        this.title = title;
        this.picture = picture;
        this.desc = desc;
        this.dist = dist;
        this.mapy = mapy;
        this.mapx = mapx;
        this.contentId = contentId;
    }

    //getters and setters

    public int getCat(){
        return cat;
    }

    public String getTitle(){
        return title;
    }

    public String getPicture(){
        return picture;
    }

    public String getDesc(){
        return desc;
    }

    public int getDist(){
        return dist;
    }

    public String getMapy(){
        return mapy;
    }

    public String getMapx(){
        return mapx;
    }

    public int getContentId(){
        return contentId;
    }

    public void setCat(int cat){
        this.cat = cat;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setPicture(String picture){
        this.picture = picture;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public void setDist(int dist){
        this.dist = dist;
    }




}
