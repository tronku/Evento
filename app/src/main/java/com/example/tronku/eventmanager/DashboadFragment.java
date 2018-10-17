package com.example.tronku.eventmanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboadFragment extends Fragment {

    @BindView(R.id.swiperefresh)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.eventsListView)RecyclerView eventsRecyclerView;
    @BindView(R.id.infoText)TextView infoTextView;

    public DashboadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboad, container, false);
    }

}
