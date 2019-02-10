package tronku.dsc.eventmanager.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import tronku.dsc.eventmanager.MainActivity;
import tronku.dsc.eventmanager.POJO.Event;
import tronku.dsc.eventmanager.Adapters.EventsAdapter;
import tronku.dsc.eventmanager.POJO.API;
import tronku.dsc.eventmanager.R;
import tronku.dsc.eventmanager.SocietyFilterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UpcomingEventsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView eventsRecyclerView;
    private ArrayList<Event> eventList = new ArrayList<>();
    private View view;
    public EventsAdapter adapter;
    private FloatingActionButton filter, remove;
    private boolean hasExtra = false;
    private TextView noEvent;
    private Toast noEventToast;

    public UpcomingEventsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);

        adapter = new EventsAdapter(getContext(), eventList);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        eventsRecyclerView = view.findViewById(R.id.eventsListView);
        noEvent = view.findViewById(R.id.noevents);
        filter = view.findViewById(R.id.filter);
        remove = view.findViewById(R.id.remove);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsRecyclerView.setAdapter(adapter);

        noEventToast = Toast.makeText(getContext(), "No events found!", Toast.LENGTH_SHORT);

        updateEvents(hasExtra);
        if(hasExtra)
            remove.setVisibility(View.VISIBLE);
        else
            remove.setVisibility(View.GONE);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    if(isConnected()) {
                        updateEvents(hasExtra);
                    }
                    else
                        networkCheck();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent society = new Intent(getContext(), SocietyFilterActivity.class);
                society.putExtra("upcoming", "true");
                startActivity(society);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasExtra = false;
                remove.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Filters removed!", Toast.LENGTH_SHORT).show();
                updateEvents(false);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            hasExtra = true;
            Log.e("Upcoming", "onCreate: true");
        }
    }

    public void updateEvents(boolean hasExtra) {
        Log.e("UpcomingUpdate", "updateEvents: " + hasExtra);
        eventList.clear();
        String url;
        if(hasExtra){
            url = "http://13.126.64.67/api/society/" + getArguments().getString("society") + "/events/upcoming";
        }
        else
            url = API.EVENTS_API;

        final String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("token", "token_no");
        Log.d("token", token);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length(); i++) {
                    try {
                        JSONObject event = response.getJSONObject(i);
                        String name = event.getString("name");
                        String society = event.getString("society_name");
                        String desc = event.getString("notes");
                        String image = event.getString("image");
                        String startFullDate = event.getString("start_day");
                        String endFullDate = event.getString("end_day");
                        String contact_person = event.getString("contact_person");
                        String contact_no = event.getString("contact_number");
                        String venue = event.getString("venue");
                        String logo = event.getString("society_logo");
                        String regLink = event.getString("registration_link");
                        long id = event.getLong("id");

                        eventList.add(new Event(society, name, desc, startFullDate, endFullDate, image, contact_person, contact_no, venue, logo, regLink, id));

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

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<JSONObject>() {
            @Override
            public void onRequestFinished(Request<JSONObject> request) {
                if(eventList.size()!=0) {
                    adapter.updateEvents(eventList);
                    eventsRecyclerView.setVisibility(View.VISIBLE);
                    noEvent.setVisibility(View.INVISIBLE);
                }
                else {
                    noEvent.setVisibility(View.VISIBLE);
                    noEventToast.show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        networkCheck();
    }


    public void networkCheck(){
        Snackbar snackbar = Snackbar.make(view, "No Internet Connection!", Snackbar.LENGTH_INDEFINITE);

        try {
            if(!isConnected()){
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red));
                snackbar.setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        networkCheck();
                    }
                });
                snackbar.setActionTextColor(getResources().getColor(R.color.orange));
                snackbar.show();
            }
            else {
                snackbar.dismiss();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateEvents(eventList);
    }

    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec(command).waitFor() == 0);
    }


}
