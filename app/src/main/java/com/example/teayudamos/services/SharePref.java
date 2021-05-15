package com.example.teayudamos.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import com.example.teayudamos.Dashboard;
import com.example.teayudamos.Registration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SharePref {

    SharedPreferences settings;
    SharedPreferences.Editor editor;


    public SharePref(Context context) {
        this.settings = context.getSharedPreferences(Constants.SHARE_PREFERENCE_FILE, 0);
        this.editor = settings.edit();
    }

    public void setSharedPrefString(String key, String value){
        this.editor.putString(key, value);
        this.editor.apply();
    }

    public void setSharedPrefInt(String key, int value){
        this.editor.putInt(key, value);
        this.editor.apply();
    }

    public void setSharedPrefBoolean(String key, boolean value){
        this.editor.putBoolean(key, value);
        this.editor.apply();
    }

    public void setSharedPrefSet(String key, HashMap<String, Object> strings) {
        Set<String> set = new HashSet<>();
        for (Map.Entry<String, Object> entry : strings.entrySet()) {
            set.add(entry.getValue().toString());
        }
        this.editor.putStringSet(key, set);
        this.editor.apply();
    }

    public String getSharedPrefString(String key){
        return this.settings.getString(key, "");
    }

    public int getSharedPrefInt(String key){
        return this.settings.getInt(key, 0);
    }

    public boolean getSharedPrefBoolean(String key){
        return this.settings.getBoolean(key, false);
    }

    public Set<String> getSharedPrefSet(String key){
        return this.settings.getStringSet(key, new HashSet<>());
    }

    public void clearAllSharedPref() {
        this.settings.edit().clear().apply();
    }

    public void clearSharedPrefByKey(String key) {
        this.settings.edit().remove(key).apply();
    }
}