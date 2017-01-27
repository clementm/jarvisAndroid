package com.example.clement.androidapp;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clement on 13/01/17.
 */

public class RaspiInterface {

    public interface RaspiCallback {
        void onObjectResponse(JSONObject response);
    }

    private Activity parent;
    private String raspiURL;
    private RequestQueue queue;

    public RaspiInterface(Activity parent, String raspiURL) {
        this.parent = parent;
        this.raspiURL = raspiURL;

        queue = Volley.newRequestQueue(this.parent);
    }

    public void retrieveSchedule(JSONObject body, final RaspiCallback callback) {
        final String url = this.raspiURL + "api/schedule";

        final String requestBody = body.toString();

        StringRequest stringRequest = new StringRequest (Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        try {
                            JSONObject respObject = new JSONObject(response);
                            callback.onObjectResponse(respObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            throw new Error("Unable to retrieve schedule from raspberry pi");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                        throw new Error("Unable to retrieve schedule from raspberry pi server");
                    }
                }

        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        queue.add(stringRequest);
    }

    public void pushUserData(List<Event> events, String todoist_token, final RaspiCallback callback) {
        final String url = this.raspiURL + "api/todos";
        final JSONObject block = new JSONObject();

        JSONArray jsonBody = new JSONArray();
        for(int i=0; i<events.size(); i++) {
            jsonBody.put(events.get(i).getJSONObject());
        }
        try {
            block.put("events", jsonBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (todoist_token.isEmpty()) {
            retrieveSchedule(block, callback);
        } else {
            StringRequest stringRequest = new StringRequest (Request.Method.GET, "https://todoist.com/API/v7/sync?token="+todoist_token+"&sync_token=*&resource_types=[\"items\"]",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject respObj = new JSONObject(response);

                                JSONArray arr = (JSONArray) respObj.get("items");

                                block.put("todos", arr);
                                retrieveSchedule(block, callback);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error", new String(error.networkResponse.data));
                            throw new Error("Impossible de récupérer les todo-lists");
                        }
                    }

            ) {};

            queue.add(stringRequest);
        }


    }

    public void pushEventList(List<Event> events, final RaspiCallback callback) {
        String url = this.raspiURL + "api/events";

        // build JSON array from event list
        JSONArray jsonBody = new JSONArray();
        for(int i=0; i<events.size(); i++) {
            jsonBody.put(events.get(i).getJSONObject());
        }

        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest (Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        try {
                            JSONObject respObject = new JSONObject(response);
                            callback.onObjectResponse(respObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            throw new Error("Unable to push event list to raspberry pi server");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                        throw new Error("Unable to push event list to raspberry pi server");
                    }
                }

        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        queue.add(stringRequest);
    }
}
