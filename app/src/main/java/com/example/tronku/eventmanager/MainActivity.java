package com.example.tronku.eventmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tronku.eventmanager.Fragments.AboutFragment;
import com.example.tronku.eventmanager.Fragments.UpcomingEventsFragment;
import com.example.tronku.eventmanager.Fragments.PastEventsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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
    private String fcm_token, currentFrag;
    private int pressedCount = 0;

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
        }
        if(intent.hasExtra("upcoming"))
            upcoming = true;
        if(intent.hasExtra("past"))
            upcoming = false;

        if(upcoming) {
            goToFragment(new UpcomingEventsFragment());
            menuAdapter.setViewSelected(0, true);
            setTitle(titles.get(0));
        }
        else {
            goToFragment(new PastEventsFragment());
            menuAdapter.setViewSelected(1, true);
            setTitle(titles.get(1));
        }

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(!pref.contains("fcm_token")) {

            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if(!task.isSuccessful()){
                        Log.w("FCM TOKEN", "not generated");
                    }
                    else{
                        fcm_token = task.getResult().getToken();
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("fcm_token", fcm_token);
                        editor.apply();
                    }
                }
            });
        }

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
        currentFrag = fragment.getClass().getName();
        if(hasExtra) {
            Bundle args = new Bundle();
            args.putString("name", intent.getStringExtra("name"));
            args.putString("society", intent.getStringExtra("society"));
            args.putString("logo", intent.getStringExtra("logo"));
            fragment.setArguments(args);
            hasExtra = false;
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
                goToFragment(new UpcomingEventsFragment());
                break;
            case 1:
                goToFragment(new PastEventsFragment());
                break;
            case 2:
                goToFragment(new AboutFragment());
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
        if(currentFrag.equals("com.example.tronku.eventmanager.Fragments.UpcomingEventsFragment")) {
            pressedCount++;
            if(pressedCount==1) {
                Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            }
            else if(pressedCount==2){
                finishAffinity();
            }
        }
        else {
            goToFragment(new UpcomingEventsFragment());
            setTitle(titles.get(0));
        }
    }
}
