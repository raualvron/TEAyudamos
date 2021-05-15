package com.example.teayudamos.model;


import com.google.firebase.firestore.GeoPoint;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Location {
    private String action, datetime, road, userId;
    private ArrayList<GeoPoint> points;

    public Location() {}

    public Location(String action, String datetime, String userId, String road, ArrayList<GeoPoint> points) {
        this.action = action;
        this.datetime = datetime;
        this.points = points;
        this.road = road;
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public  ArrayList<GeoPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<GeoPoint> points) {
        this.points = points;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }
}
