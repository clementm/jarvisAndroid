package com.example.clement.androidapp;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
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

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoActivity extends DrawerActivity {
    public static final String PREFS_NAME = "Jarvis";
    String todoist_token = "";

    private RequestQueue queue;

    protected void getAccessToken() {
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if(uri.getHost().contentEquals("localhost")) {
                    final String code = uri.getQueryParameter("code");
                    Log.d("Body", code);
                    view.setVisibility(View.INVISIBLE);

                    StringRequest stringRequest = new StringRequest (Request.Method.POST, "https://todoist.com/oauth/access_token",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        setupView(R.layout.activity_todo);
                                        JSONObject respObj = new JSONObject(response);
                                        todoist_token = respObj.get("access_token").toString();

                                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("todoist_token", todoist_token);
                                        editor.commit();

                                        fetchTodos();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Error", new String(error.networkResponse.data));
                                    throw new Error("Impossible de récupérer le token d'accès Todoist");
                                }
                            }

                    ) {
                        @Override
                        public String getBodyContentType() {
                            return "application/x-www-form-urlencoded; charset=UTF-8";
                        }
                        @Override
                        public Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("client_id", "5933403b0995424bb88a6626f0b9da1f");
                            params.put("client_secret", "6a72fbb6359f41e698d1699a0e1e8438");
                            params.put("code", code);
                            return params;
                        }
                    };

                    queue.add(stringRequest);
                }
            }
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }
        });
        webView.loadUrl("https://todoist.com/oauth/authorize?client_id=5933403b0995424bb88a6626f0b9da1f&scope=data:read&secret=6a72fbb6359f41e698d1699a0e1e8438");
    }

    protected void fetchTodos() {
        StringRequest stringRequest = new StringRequest (Request.Method.GET, "https://todoist.com/API/v7/sync?token="+todoist_token+"&sync_token=*&resource_types=[\"items\"]",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respObj = new JSONObject(response);
                            ListView mListView = (ListView) findViewById(R.id.todoLists);
                            Log.d("todo", respObj.get("items").toString());

                            JSONArray arr = (JSONArray) respObj.get("items");
                            List<Todo> todos = new ArrayList<Todo>();
                            for (int i=0; i<arr.length(); i++) {
                                todos.add(new Todo((JSONObject) arr.get(i)));
                            }

                            TodoAdaptater adapter = new TodoAdaptater(TodoActivity.this, todos);
                            mListView.setAdapter(adapter);
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.getMessage());
                        Log.d("Error", new String(error.networkResponse.data));
                        throw new Error("Impossible de récupérer les todo-lists");
                    }
                }

        ) {};

        queue.add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_todo);
        getSupportActionBar().setTitle("Tâches à accomplir");

        queue = Volley.newRequestQueue(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        todoist_token = settings.getString("todoist_token", "");
        todoist_token = "";

        if (todoist_token.isEmpty()) {
            getAccessToken();
        } else {
            fetchTodos();
        }

    }
}
