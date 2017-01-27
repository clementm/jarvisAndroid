package com.example.clement.androidapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static com.example.clement.androidapp.MainActivity.READ_CALENDAR_PERMISSION;

public class SyncActivity extends DrawerActivity {

    public final static String INTENT_SCHEDULE = "com.example.jarvis.SCHEDULE";

    public static final int READ_NFC_PERMISSION = 1;

    private CalendarInterface calInterface;

    private String todoist_token = "";

    NfcAdapter mAdapter;
    PendingIntent pendingIntent;
    IntentFilter intentFiltersArray[];
    String techListsArray[][];


    private RaspiInterface raspiInterface;

    @TargetApi(24)
    protected void pushEvents() {

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

            final ImageView icon = (ImageView) findViewById(R.id.icon);
            icon.setImageResource(R.drawable.ic_cloud_upload);
            icon.setColorFilter( 0xffaaaaaa, PorterDuff.Mode.MULTIPLY );

            SharedPreferences settings = getSharedPreferences("Jarvis", 0);
            todoist_token = settings.getString("todoist_token", "");

            icon.setColorFilter( 0xff82be42, PorterDuff.Mode.MULTIPLY );
            Toast.makeText(context, "Partage des données...", Toast.LENGTH_SHORT).show();

            raspiInterface.pushUserData(eventList, todoist_token, new RaspiInterface.RaspiCallback() {
                @Override
                public void onObjectResponse(JSONObject response) {

                    icon.setColorFilter( 0xff82be42, PorterDuff.Mode.MULTIPLY );
                    Toast.makeText(context, "Terminé !", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, ScheduleActivity.class);
                    intent.putExtra(INTENT_SCHEDULE, response.toString());
                    startActivity(intent);

                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_NFC_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
                } else {
                    Log.d("Permission", "Denied !");
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_sync);
        ImageView icon = (ImageView) findViewById(R.id.icon);
        icon.setColorFilter( 0xffaaaaaa, PorterDuff.Mode.MULTIPLY );

        SharedPreferences settings = getSharedPreferences("Jarvis", 0);
        todoist_token = settings.getString("todoist_token", "");

        getSupportActionBar().setTitle("Synchronisation");

        calInterface = new CalendarInterface(this, READ_CALENDAR_PERMISSION);


        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[] {ndef, };
        techListsArray = new String[][] { new String[] { NfcF.class.getName() } };
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.NFC);

        Log.d("Permission", String.valueOf(permissionCheck));

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.NFC}, READ_NFC_PERMISSION
            );
        }
    }

    public String readTag(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        Log.d(ndef.getType(), ndef.toString());
        try {
            ndef.connect();
            NdefMessage msg = ndef.getNdefMessage();
            ndef.close();
            if (msg == null) {
                Log.d("TAG", "NULL");
            } else {
                String content = new String(msg.getRecords()[0].getPayload(), Charset.forName("UTF-8"));
                return content.substring(3, content.length()-1);
            }
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ndef.getType();
    }


    public void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, pendingIntent, null, null);

        Log.d("Permission", "Enable foreground dispatch");
    }

    private Handler mHandler = new Handler();

    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        String url = "http://"+readTag(tagFromIntent)+"/";

        ImageView icon = (ImageView) findViewById(R.id.icon);
        icon.setColorFilter( 0xff82be42, PorterDuff.Mode.MULTIPLY );
        TextView statusText = (TextView) findViewById(R.id.statusText);
        statusText.setText("Envoi des données : \n"+url);

        raspiInterface = new RaspiInterface(this, url);
        pushEvents();
    }
}
