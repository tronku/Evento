package com.example.tronku.eventmanager;

import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SocietyFilterActivity extends AppCompatActivity {

    @BindView(R.id.societyNameView) RecyclerView societyRecyclerView;
    private ArrayList<Society> societyList = new ArrayList<>();
    private SocietyAdapter adapter;
    private boolean upcoming = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_society_filter);
        ButterKnife.bind(this);

        if(getIntent().hasExtra("upcoming"))
            upcoming = true;
        else
            upcoming = false;

        adapter = new SocietyAdapter(this, societyList, upcoming);

        fillData();
    }

    public void fillData() {
        final String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("token", "token_no");
        Log.d("token", token);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API.SOCIETY_API,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length(); i++) {
                    try {
                        JSONObject society = response.getJSONObject(i);
                        String name = society.getString("name");
                        String uri = society.getString("logo");
                        String societyId = society.getString("id");
                        String dept = society.getString("department_name");
                        String type = society.getString("type");
                        String phone = society.getString("phone");
                        String email = society.getString("email");
                        societyList.add(new Society(name, uri, societyId, email, type, dept, phone));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.getCause().toString());
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        request.setShouldCache(true);
        queue.add(request);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<JSONObject>() {
            @Override
            public void onRequestFinished(Request<JSONObject> request) {
                adapter.updateData(societyList);
                societyRecyclerView.setLayoutManager(new LinearLayoutManager(SocietyFilterActivity.this));
                societyRecyclerView.setAdapter(adapter);
                societyRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

}
