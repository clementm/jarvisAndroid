package com.example.clement.androidapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends DrawerActivity {

    private CalendarInterface calInterface;
    public static final int READ_CALENDAR_PERMISSION = 0;


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_CALENDAR_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startEventsPush();
                } else {
                    Log.d("Permission", "Denied !");
                }
                return;
            }
            /*case READ_NFC_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
                } else {
                    Log.d("Permission", "Denied !");
                }
                return;
            }*/
        }
    }

    @TargetApi(24)
    protected void startEventsPush() {

        Calendar eStartDate = Calendar.getInstance();
        Calendar eEndDate = Calendar.getInstance();

        // eStartDate should be yesterday at 0am
        eStartDate.add(Calendar.DATE, -1);
        eStartDate.set(Calendar.HOUR_OF_DAY, 0);
        eStartDate.set(Calendar.MINUTE, 0);
        eStartDate.set(Calendar.SECOND, 0);
        eStartDate.set(Calendar.MILLISECOND, 0);

        // eStartDate should be tomorrow at 11:59pm
        eEndDate.add(Calendar.DATE, +1);
        eEndDate.set(Calendar.HOUR_OF_DAY, 23);
        eEndDate.set(Calendar.MINUTE, 59);
        eEndDate.set(Calendar.SECOND, 59);
        eEndDate.set(Calendar.MILLISECOND, 999);

        List<Event> eventList = calInterface.fetchCalendarEvents(eStartDate, eEndDate);

        final Activity context = this;

        if (eventList != null) {
            ListView mListView = (ListView) findViewById(R.id.listView);

            EventAdapter adapter = new EventAdapter(MainActivity.this, eventList);
            mListView.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main);

        getSupportActionBar().setTitle("Evènements récents et à venir");

        calInterface = new CalendarInterface(this, READ_CALENDAR_PERMISSION);
        if (calInterface.checkGrantedPermission()) {
            startEventsPush();
        }
    }


}
