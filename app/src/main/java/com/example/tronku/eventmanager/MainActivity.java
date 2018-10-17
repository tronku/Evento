package com.example.tronku.eventmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

    private ArrayList<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menu)));
        viewHolder = new ViewHolder();

        handleToolbar();
        handleDrawer();
        handleMenu();

        goToFragment(new DashboadFragment(), false);
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

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }

        transaction.replace(R.id.container, fragment).commit();
    }

    @Override
    public void onFooterClicked() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
                goToFragment(new DashboadFragment(), false);
                break;
            case 1:
                goToFragment(new PastEventsFragment(), false);
                break;
            default:
                goToFragment(new DashboadFragment(), false);
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
}
