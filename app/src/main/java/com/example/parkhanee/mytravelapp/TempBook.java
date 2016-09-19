package com.example.parkhanee.mytravelapp;

/**
 * Created by parkhanee on 2016. 9. 19..
 */
public class TempBook {

    private int id;
    private String title;
    private String author;

    public TempBook(){}

    public TempBook(String title, String author) {
        super();
        this.title = title;
        this.author = author;
    }

    //getters & setters


    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "TempBook [id=" + id + ", title=" + title + ", author=" + author
                + "]";
    }


}
