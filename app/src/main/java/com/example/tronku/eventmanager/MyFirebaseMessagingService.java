package com.example.tronku.eventmanager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("fcm_token", s);
        editor.apply();
    }
}
