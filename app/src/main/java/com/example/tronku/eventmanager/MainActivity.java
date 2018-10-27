package com.example.tronku.eventmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends AppCompatActivity implements DuoMenuView.OnMenuClickListener {

    @BindView(R.id.drawer)DuoDrawerLayout drawerLayout;
    private com.example.tronku.eventmanager.MenuAdapter menuAdapter;
    private ViewHolder viewHolder;
    private Intent intent;
    private ArrayList<String> titles = new ArrayList<>();
    private boolean hasExtra = false;
    private boolean upcoming = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menu)));
        viewHolder = new ViewHolder();
        intent = getIntent();

        handleToolbar();
        handleDrawer();
        handleMenu();

        if(intent.hasExtra("name")){
            hasExtra = true;
            if(intent.getStringExtra("upcoming").equals("true"))
                upcoming = true;
            else
                upcoming = false;
        }
        else if(intent.hasExtra("remove")) {
            hasExtra = false;
            if(intent.getStringExtra("upcoming").equals("true"))
                upcoming = true;
            else
                upcoming = false;
        }

        Log.d("upcoming", String.valueOf(upcoming));

        if(upcoming)
            goToFragment(new DashboardFragment());
        else
            goToFragment(new PastEventsFragment());

        menuAdapter.setViewSelected(0, true);
        setTitle(titles.get(0));

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        viewHolder.name.setText(pref.getString("name", "Me"));
        viewHolder.email.setText(pref.getString("email", "My email"));
    }

    private void handleToolbar() {
        setSupportActionBar(viewHolder.mToolbar);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                viewHolder.mDuoDrawerLayout,
                viewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        viewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();
    }

    private void handleMenu() {
        menuAdapter = new MenuAdapter(titles);

        viewHolder.mDuoMenuView.setOnMenuClickListener(this);
        viewHolder.mDuoMenuView.setAdapter(menuAdapter);
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(hasExtra) {
            Bundle args = new Bundle();
            args.putString("name", intent.getStringExtra("name"));
            args.putString("society", intent.getStringExtra("society"));
            args.putString("logo", intent.getStringExtra("logo"));
            fragment.setArguments(args);
        }
        transaction.replace(R.id.container, fragment).commit();
    }

    @Override
    public void onFooterClicked() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
        Intent logOut = new Intent(MainActivity.this, LoginActivity.class);
        logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logOut);
    }

    @Override
    public void onHeaderClicked() {

    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        setTitle(titles.get(position));

        menuAdapter.setViewSelected(position, true);

        switch (position) {
            case 0:
                goToFragment(new DashboardFragment());
                break;
            case 1:
                goToFragment(new PastEventsFragment());
                break;
            default:
                goToFragment(new DashboardFragment());
                break;
        }

        viewHolder.mDuoDrawerLayout.closeDrawer();
    }

    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;
        private TextView name;
        private TextView email;

        ViewHolder() {
            mDuoDrawerLayout = findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = findViewById(R.id.toolbar);
            name = findViewById(R.id.nameHeader);
            email = findViewById(R.id.emailHeader);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
