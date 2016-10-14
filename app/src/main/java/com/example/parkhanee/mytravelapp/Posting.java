package com.example.parkhanee.mytravelapp;

/**
 * Created by parkhanee on 2016. 10. 7..
 */
public class Posting {

    private String posting_id, folder_id, user_id,  type, posting_title, note,created, modified;
    private String image_path;

    public Posting(){}

    public Posting(String posting_id, String folder_id, String user_id, String type, String title, String note, String created, String modified){
        this.posting_id = posting_id;
        this.folder_id= folder_id;
        this.user_id = user_id;
        this.posting_title = title;
        this.note = note;
        this.created = created;
        this.modified = modified;
        this.type = type;
        this.image_path="";
    }

    public Posting(String posting_id, String folder_id, String user_id, String type, String title, String note, String created, String modified,String image_path){
        this.posting_id = posting_id;
        this.folder_id= folder_id;
        this.user_id = user_id;
        this.posting_title = title;
        this.note = note;
        this.created = created;
        this.modified = modified;
        this.type = type;
        this.image_path = image_path;
    }

    //getters and setters

    public String getPosting_id() {
        return posting_id;
    }

    public String getFolder_id() {
        return folder_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getType() {
        return type;
    }

    public String getPosting_title() {
        return posting_title;
    }

    public String getNote() {
        return note;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setPosting_id(String posting_id) {
        this.posting_id = posting_id;
    }

    public void setFolder_id(String folder_id) {
        this.folder_id = folder_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPosting_title(String posting_title) {
        this.posting_title = posting_title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
