package com.example.teayudamos.model;

import java.util.Date;

public class Acta {
    private String date, description, title, type;

    public Acta() {}

    public Acta(String date, String description, String title, String type) {
        this.date = date;
        this.description = description;
        this.title = title;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}