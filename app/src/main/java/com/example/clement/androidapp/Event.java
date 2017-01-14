package com.example.clement.androidapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by clement on 13/01/17.
 */


public class Event implements Comparable<Event> {
    private int color;
    private String pseudo;
    private String text;
    private Date date;

    public Event(int color, String pseudo, String text, Date date1, Date date, int anInt, String string) {
        this.color = color;
        this.pseudo = pseudo;
        this.text = text;
        this.date = date;
    }

    public String getPseudo() {
        return this.pseudo;
    }

    public String getText() {
        return this.text;
    }

    public int getColor() {
        return this.color;
    }

    public Date getDate() {
        return this.date;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("title", this.pseudo);
            obj.put("description", this.text);
            obj.put("startDate", this.date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public int compareTo(Event o) {
        Log.d("EventCompare1", this.date.toString());
        Log.d("EventCompare2", o.getDate().toString());
        Log.d("EventCompare", String.valueOf(date.compareTo(o.getDate())));
        return date.compareTo(o.getDate());
    }
}