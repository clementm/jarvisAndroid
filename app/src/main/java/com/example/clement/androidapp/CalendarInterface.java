package com.example.clement.androidapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by clement on 13/01/17.
 */

public class CalendarInterface {

    private Activity parent;
    private static int READ_CALENDAR_PERMISSION;

    public CalendarInterface(Activity parent, int permission_index) {
        this.parent = parent;
        READ_CALENDAR_PERMISSION = permission_index;
    }

    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.RRULE
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_TITLE_INDEX = 1;
    private static final int PROJECTION_DESCRIPTION_NAME_INDEX = 2;
    private static final int PROJECTION_DTSTART_INDEX = 3;
    private static final int PROJECTION_DTEND_INDEX = 4;
    private static final int PROJECTION_ALL_DAY_INDEX = 3;
    private static final int PROJECTION_EVENT_LOCATION_INDEX = 6;
    private static final int PROJECTION_RRULE_INDEX = 3;


    protected boolean checkGrantedPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this.parent,
                Manifest.permission.READ_CALENDAR);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.parent,
                    new String[]{Manifest.permission.READ_CALENDAR}, READ_CALENDAR_PERMISSION
            );
        }

        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(24)
    public List<Event> fetchCalendarEvents(Calendar startDate, Calendar endDate) {

        Cursor cur = null;
        ContentResolver cr = parent.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = "((" + CalendarContract.Events.DTSTART + " >= " + startDate.getTimeInMillis() + ") " +
                "AND (" + CalendarContract.Events.DTEND + " <= " + endDate.getTimeInMillis() + ") " +
                "AND (" + CalendarContract.Events.DELETED + " != 1))";


        if (!this.checkGrantedPermission()) {
            return null;
        }

        cur = cr.query(uri, EVENT_PROJECTION, selection, null, null);

        List<Event> eventList = new ArrayList<Event>();

        while (cur.moveToNext()) {
            eventList.add(new Event(
                    Color.GRAY,
                    cur.getString(PROJECTION_TITLE_INDEX),
                    cur.getString(PROJECTION_DESCRIPTION_NAME_INDEX),
                    new Date(Long.parseLong(cur.getString(PROJECTION_DTSTART_INDEX))),
                    new Date(Long.parseLong(cur.getString(PROJECTION_DTEND_INDEX))),
                    cur.getInt(PROJECTION_ALL_DAY_INDEX),
                    cur.getString(PROJECTION_EVENT_LOCATION_INDEX)
            ));
        }

        Collections.sort(eventList);

        return eventList;
    }
}
