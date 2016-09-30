package com.example.parkhanee.mytravelapp;

/**
 * Created by parkhanee on 2016. 9. 30..
 */
public class Share {
    private String share_id;
    private String folder_id;
    private String user_id;
    private String state;
    private Boolean isMine;

    // constructors
    public Share(){}

    public Share(String share_id,String folder_id,String user_id,String state, Boolean isMine){
        this.share_id = share_id;
        this.folder_id = folder_id;
        this.user_id = user_id;
        this.state = state;
        this.isMine = isMine;
    }

    //getters and setters

    public String getShare_id() {
        return share_id;
    }

    public String getFolder_id() {
        return folder_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getState() {
        return state;
    }

    public Boolean getMine() {
        return isMine;
    }

    public void setShare_id(String share_id) {
        this.share_id = share_id;
    }

    public void setFolder_id(String folder_id) {
        this.folder_id = folder_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setMine(Boolean mine) {
        isMine = mine;
    }

    // toString
    @Override
    public String toString() {
        return "Share [share_id=" + share_id + ",foler_id=" + folder_id + ", state=" + state+", isMine="+isMine
                + "]";
    }
}
