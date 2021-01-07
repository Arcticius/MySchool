package com.example.myschool.entity;

import android.widget.TextView;

public class StudentListView {

    private String id;
    private String name;
    private String balLeave;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeave() {
        return balLeave;
    }

    public void setLeave(String balLeave) {
        this.balLeave = balLeave;
    }
}
