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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SocietyFilterActivity extends AppCompatActivity {

    @BindView(R.id.societyNameView) RecyclerView societyRecyclerView;
    @BindView(R.id.apply) Button applyButton;
    private ArrayList<Society> societyList = new ArrayList<>();
    private SocietyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_society_filter);
        ButterKnife.bind(this);

        societyList.add(new Society("Developer Student Clubs", false));
        societyList.add(new Society("Nibble Computer Society", false));
        societyList.add(new Society("Illuminati", false));
        societyList.add(new Society("MMIL", false));
        societyList.add(new Society("EDC", false));

        adapter = new SocietyAdapter(this, societyList);
        societyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        societyRecyclerView.setAdapter(adapter);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter();
            }
        });
    }

    private void filter() {
        adapter.filterData();
    }

    public void fillData() {
        final String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("token", "token_no");

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, API.SOCIETY_API,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + token);
                return headers;
            }
        };

        req.setShouldCache(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

}
