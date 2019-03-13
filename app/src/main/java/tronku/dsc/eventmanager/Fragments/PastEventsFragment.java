package tronku.dsc.eventmanager.Fragments;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
import tronku.dsc.eventmanager.Adapters.EventsAdapter;
import tronku.dsc.eventmanager.EventoApplication;
import tronku.dsc.eventmanager.POJO.API;
import tronku.dsc.eventmanager.POJO.Event;
import tronku.dsc.eventmanager.R;
import tronku.dsc.eventmanager.ConnectivityReceiverEvents;
import tronku.dsc.eventmanager.SocietyFilterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PastEventsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView eventsRecyclerView;
    private ArrayList<Event> eventList = new ArrayList<>();
    private FloatingActionButton filter,remove;
    private EventsAdapter adapter;
    private boolean hasExtra = false;
    private TextView noEvent;
    private ProgressBar loader;
    private ConnectivityReceiverEvents receiver;

    public PastEventsFragment() {

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_past_events, container, false);
        setHasOptionsMenu(true);
        adapter = new EventsAdapter(getContext(), eventList);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        eventsRecyclerView = view.findViewById(R.id.eventsListView);
        noEvent = view.findViewById(R.id.noevents);
        filter = view.findViewById(R.id.filter);
        remove = view.findViewById(R.id.remove);
        loader = view.findViewById(R.id.loader_past);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsRecyclerView.setAdapter(adapter);

        Snackbar snackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getContext().getResources().getColor(R.color.red));
        receiver = new ConnectivityReceiverEvents(this, "past", hasExtra, snackbar, filter);

//        if (receiver.isConnected() && eventList.isEmpty())
//            updateEvents(hasExtra);
//        else
//            disconnectedPrev = true;

        if(hasExtra)
            remove.setVisibility(View.VISIBLE);
        else
            remove.setVisibility(View.GONE);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (receiver.isConnected()) {
                    updateEvents(hasExtra);
                }
                else {
                    Toast.makeText(getContext(), "No internet!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent society = new Intent(getContext(), SocietyFilterActivity.class);
                society.putExtra("past", "true");
                startActivity(society);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasExtra = false;
                remove.setVisibility(View.GONE);
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
        }
    }

    public void updateEvents(boolean hasExtra) {
        final ArrayList<Event> events = new ArrayList<>();
        String url;
        if(hasExtra){
            url = "http://13.126.64.67/api/society/" + getArguments().getString("society") + "/events/past";
        }
        else
            url = API.PAST_API;

        final String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("token", "token_no");
        Log.d("token", token);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("RESPONSE", response.toString());
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
                        String type = event.getString("society_type");
                        long id = event.getLong("id");

                        events.add(new Event(society, name, desc, startFullDate, endFullDate, image, contact_person, contact_no, venue, logo, regLink, id, type));

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

        EventoApplication.getInstance().addToRequestQueue(request);
        EventoApplication.getInstance().getRequestQueue().addRequestFinishedListener(new RequestQueue.RequestFinishedListener<JSONObject>() {
            @Override
            public void onRequestFinished(Request<JSONObject> request) {
                if(events.size()!=0) {
                    eventList.clear();
                    eventList = events;
                    adapter.updateEvents(eventList);
                    eventsRecyclerView.setVisibility(View.VISIBLE);
                    noEvent.setVisibility(View.INVISIBLE);
                }
                else {
                    noEvent.setVisibility(View.VISIBLE);
                }
                loader.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
    }
}
