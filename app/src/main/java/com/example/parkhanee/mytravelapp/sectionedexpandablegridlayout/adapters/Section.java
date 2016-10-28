package com.example.parkhanee.mytravelapp.sectionedexpandablegridlayout.adapters;

/**
 * Created by parkhanee on 2016. 10. 26..
 */
public class Section {
    private final String name;
    private final int id;
    public boolean isExpanded;

    public Section(String name,int id) {
        this.name = name;
        this.id = id;
        isExpanded = id == 1;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
