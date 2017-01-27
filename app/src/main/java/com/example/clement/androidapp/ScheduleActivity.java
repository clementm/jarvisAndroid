package com.example.clement.androidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_schedule);

        Intent intent = getIntent();
        JSONObject schedule;
        try {
            schedule = new JSONObject(intent.getStringExtra(SyncActivity.INTENT_SCHEDULE));
            Log.d("Received", schedule.toString());

            JSONArray arr = (JSONArray) schedule.get("schedule");
            List<ScheduleStep> steps = new ArrayList<ScheduleStep>();
            for (int i=0; i<arr.length(); i++) {
                steps.add(new ScheduleStep((JSONObject) arr.get(i)));
            }

            ListView mListView = (ListView) findViewById(R.id.schedule);
            ScheduleStepAdapter adapter = new ScheduleStepAdapter(ScheduleActivity.this, steps);
            mListView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setTitle("Votre programme");


    }
}
