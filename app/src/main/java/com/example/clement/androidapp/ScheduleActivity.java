package com.example.clement.androidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Intent intent = getIntent();
        JSONObject schedule;
        try {
            schedule = new JSONObject(intent.getStringExtra(MainActivity.INTENT_SCHEDULE));
            Log.d("Received", schedule.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
