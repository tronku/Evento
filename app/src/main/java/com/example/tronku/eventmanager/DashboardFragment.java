package com.example.tronku.eventmanager;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class DashboardFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView eventsRecyclerView;
    private ArrayList<Event> eventList = new ArrayList<>();
    private View view;
    private CardView filter;
    private EventsAdapter adapter;
    private ArrayList<String> societyFilter = new ArrayList<>();

    public DashboardFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        adapter = new EventsAdapter(getContext(), eventList);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        eventsRecyclerView = view.findViewById(R.id.eventsListView);
        filter = view.findViewById(R.id.filter);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsRecyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.filter_dialog);
                Button society = dialog.findViewById(R.id.societiesFilter);
                Button categ = dialog.findViewById(R.id.categFilter);

                society.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent society = new Intent(dialog.getContext(), SocietyFilterActivity.class);
                        startActivity(society);
                    }
                });

                dialog.show();
            }
        });

        Log.d("size", String.valueOf(societyFilter.size()));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        societyFilter = getArguments().getStringArrayList("societies");
    }
}
