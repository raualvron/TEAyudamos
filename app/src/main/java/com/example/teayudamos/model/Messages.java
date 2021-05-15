package com.example.teayudamos.model;
import java.util.Date;

public class Messages {
    private String message, uid, type, datetime;

    public Messages() {}

    public Messages(String message, String uid, String datetime, String type) {
        this.message = message;
        this.uid = uid;
        this.datetime = datetime;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public String getDateTime() {
        return datetime;
    }

    public void setDate(Date date) {
        this.datetime = datetime;
    }
}