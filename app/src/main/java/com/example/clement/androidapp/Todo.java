package com.example.clement.androidapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by clement on 26/01/17.
 */

public class Todo implements Comparable<Todo> {
    private String content;
    private int priority;
    private Date due_date;

    public Todo(String content, int priority, Date due_date) {
        this.content = content;
        this.priority = priority;
        this.due_date = due_date;
    }

    public Todo(JSONObject obj) throws JSONException, ParseException {
        this.content = obj.getString("content");
        this.priority = obj.getInt("priority");
        SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        if (obj.has("due_date_utc") && !obj.getString("due_date_utc").contentEquals("null")) {
            this.due_date = format.parse(obj.getString("due_date_utc"));
        }
    }

    public String getContent() {
        return this.content;
    }

    public int getPriority() {
        return this.priority;
    }

    public Date getDueDate() {
        return this.due_date;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("content", this.content);
            obj.put("priority", this.priority);
            obj.put("due_date", this.due_date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public int compareTo(Todo o) {
        return 0;
    }
}
