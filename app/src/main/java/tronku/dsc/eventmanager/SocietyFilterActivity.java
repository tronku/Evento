package tronku.dsc.eventmanager;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import tronku.dsc.eventmanager.Adapters.SocietyAdapter;
import tronku.dsc.eventmanager.POJO.API;
import tronku.dsc.eventmanager.POJO.Society;

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
    @BindView(R.id.societyRefresh) SwipeRefreshLayout societyRefreshLayout;
    @BindView(R.id.loader_society)
    ProgressBar loader;
    private ArrayList<Society> societyList = new ArrayList<>();
    private SocietyAdapter adapter;
    private boolean upcoming = true;
    private View view;
    private ConnectivityReceiverEvents receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_society_filter);
        ButterKnife.bind(this);

        view = findViewById(android.R.id.content);

        Snackbar snackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
        receiver = new ConnectivityReceiverEvents(this, snackbar);

        if(getIntent().hasExtra("upcoming"))
            upcoming = true;
        else
            upcoming = false;

        adapter = new SocietyAdapter(this, societyList, upcoming);

//        if (receiver.isConnected() && societyList.isEmpty())
//            fillData();
//        else {
//            Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT).show();
//            disconnectedPrev = true;
//        }

        societyRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (receiver.isConnected()) {
                    fillData();
                }
                else {
                    Toast.makeText(SocietyFilterActivity.this, "No internet!", Toast.LENGTH_SHORT).show();
                    societyRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void fillData() {
        final ArrayList<Society> societies = new ArrayList<>();
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
                        societies.add(new Society(name, uri, societyId, email, type, dept, phone));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

        request.setShouldCache(true);
        EventoApplication.getInstance().addToRequestQueue(request);
        EventoApplication.getInstance().getRequestQueue().addRequestFinishedListener(new RequestQueue.RequestFinishedListener<JSONObject>() {
            @Override
            public void onRequestFinished(Request<JSONObject> request) {
                societyList.clear();
                societyList = societies;
                adapter.updateData(societyList);
                societyRefreshLayout.setVisibility(View.VISIBLE);
                societyRecyclerView.setLayoutManager(new LinearLayoutManager(SocietyFilterActivity.this));
                societyRecyclerView.setAdapter(adapter);
                societyRecyclerView.setVisibility(View.VISIBLE);
                societyRefreshLayout.setRefreshing(false);
                loader.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

}
