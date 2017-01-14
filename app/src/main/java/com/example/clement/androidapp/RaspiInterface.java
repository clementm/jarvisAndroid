package com.example.clement.androidapp;

import android.app.Activity;
import android.util.Log;
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
