package com.example.clement.androidapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by clement on 13/01/17.
 */


public class ScheduleStep {

    private int type;
    private int duration;
    private String effString;
    private String task;

    public ScheduleStep(JSONObject step) throws JSONException {
        this.type = step.getInt("type");
        this.duration = step.getInt("duration");
        this.effString = "";

        JSONArray effects = (JSONArray) step.get("effects");
        JSONObject eff;
        for (int i = 0; i < effects.length(); i++) {
            eff = (JSONObject) effects.get(i);
            this.effString += eff.getString("name") + " : " + eff.getString("value") + "\n";
        }

        if (this.type == 1) {
            this.task = step.getString("task");
        }
    }

    public int getType() { return this.type; }

    public int getDuration() { return this.duration; }

    public String getEffects() { return this.effString; }

    public String getTask() { return this.task; }
}