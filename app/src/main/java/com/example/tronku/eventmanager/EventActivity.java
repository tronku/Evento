package com.example.tronku.eventmanager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import java.text.DateFormatSymbols;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;
import de.hdodenhof.circleimageview.CircleImageView;

public class EventActivity extends AppCompatActivity {

    @BindView(R.id.event_Name) TextView eventName;
    @BindView(R.id.society_Name) TextView societyName;
    @BindView(R.id.society_Logo) CircleImageView societyLogo;
    @BindView(R.id.timings) TextView eventTiming;
    @BindView(R.id.venue)
    TextView eventVenue;
    @BindView(R.id.desc)
    TextView eventDesc;
    @BindView(R.id.regButton)
    Button regButton;
    @BindView(R.id.eventPic)
    ImageView eventImage;
    @BindView(R.id.dates)
    TextView eventDate;
    @BindView(R.id.contactPerson)
    TextView eventPerson;
    @BindView(R.id.contactNumber)
    TextView eventContact;
    @BindView(R.id.callNow)
    TextView callNow;
    @BindView(R.id.notif)
    Switch notificationSwitch;
    @BindView(R.id.counterCard)
    CardView counterCard;
    @BindView(R.id.counterView) CountdownView counterView;
    @BindView(R.id.liveText) TextView liveText;

    private Intent intent, call;
    private static final int REQUEST_CODE = 101, CALENDAR_CODE = 201;
    private String event, society, logo, desc, startDate, endDate, image, regLink, venue, contact_person, contact_no;
    private long id;
    private long eventId;
    private boolean needReminder = false;
    private java.util.Calendar startCal, endCal;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);
        intent = getIntent();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        fillData();
        fillViews();
        checkValidityAndStartCounter();

        call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact_no));

        if(hasAdded()) {
            notificationSwitch.setChecked(true);
        }

        counterView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                counterView.setVisibility(View.GONE);
            }
        });

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                needReminder = b;
                getPermission();
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(EventActivity.this, RegisterEventActivity.class);
                register.putExtra("website", regLink);
                startActivity(register);
            }
        });

        callNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(EventActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EventActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
                } else
                    startActivity(call);
            }
        });

    }


    private void checkValidityAndStartCounter() {
        java.util.Calendar current = java.util.Calendar.getInstance();

        int startHour = Integer.parseInt(startDate.substring(11, 13));
        int startMinutes = Integer.parseInt(startDate.substring(14, 16));
        int endHour = Integer.parseInt(endDate.substring(11, 13));
        int endMinutes = Integer.parseInt(endDate.substring(14, 16));

        //Start Date
        startCal = java.util.Calendar.getInstance();
        startCal.set(java.util.Calendar.MONTH, Integer.parseInt(startDate.substring(5, 7)) - 1);
        startCal.set(java.util.Calendar.DAY_OF_MONTH, Integer.parseInt(startDate.substring(8, 10)));
        startCal.set(java.util.Calendar.YEAR, Integer.parseInt(startDate.substring(0, 4)));
        startCal.set(java.util.Calendar.HOUR_OF_DAY, startHour);
        startCal.set(java.util.Calendar.MINUTE, startMinutes);
        startCal.set(java.util.Calendar.SECOND, 0);
        startCal.set(java.util.Calendar.MILLISECOND, 0);

        //End Date
        endCal = java.util.Calendar.getInstance();
        endCal.set(java.util.Calendar.MONTH, Integer.parseInt(endDate.substring(5, 7)) - 1);
        endCal.set(java.util.Calendar.DAY_OF_MONTH, Integer.parseInt(endDate.substring(8, 10)));
        endCal.set(java.util.Calendar.YEAR, Integer.parseInt(endDate.substring(0, 4)));
        endCal.set(java.util.Calendar.HOUR_OF_DAY, endHour);
        endCal.set(java.util.Calendar.MINUTE, endMinutes);
        endCal.set(java.util.Calendar.SECOND, 0);
        endCal.set(java.util.Calendar.MILLISECOND, 0);

        Log.i("currentTime", String.valueOf(current.getTimeInMillis()));
        Log.i("eventTime", String.valueOf(startCal.getTimeInMillis()));

        if(current.getTimeInMillis() >= endCal.getTimeInMillis()){
            notificationSwitch.setVisibility(View.GONE);
            counterCard.setVisibility(View.GONE);
        }

        else if(current.getTimeInMillis() <= endCal.getTimeInMillis() && current.getTimeInMillis() >= startCal.getTimeInMillis()) {
            counterView.setVisibility(View.GONE);
            liveText.setVisibility(View.VISIBLE);
            notificationSwitch.setVisibility(View.GONE);
        }

        else
            counterView.start(startCal.getTimeInMillis() - current.getTimeInMillis());
    }

    private void getPermission() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, CALENDAR_CODE);
        } else {
            addReminderInCalendar();
        }
    }


    public boolean hasAdded() {
        if(pref.contains(""+eventId)){
                Log.i("hasAdded","true");
            return true;
        }
        Log.i("hasAdded", "false");
        return false;
    }

    private void addReminderInCalendar() {

        if(needReminder && !hasAdded()) {

            ContentResolver cr = getContentResolver();
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Kolkata");

            /** Inserting an event in calendar. **/
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.TITLE, event);
            values.put(CalendarContract.Events.DESCRIPTION, desc);
            values.put(CalendarContract.Events.ALL_DAY, 0);
            values.put(CalendarContract.Events.DTSTART, startCal.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, endCal.getTimeInMillis());
            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            values.put(CalendarContract.Events.EVENT_LOCATION, venue);
            values.put(CalendarContract.Events.HAS_ALARM, false);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, CALENDAR_CODE);
            }

            Uri eventUri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            eventId = Long.parseLong(eventUri.getLastPathSegment());
            Toast.makeText(this, "Event added!", Toast.LENGTH_SHORT).show();

            /** Adding reminder for event added. */
            values = new ContentValues();
            values.put(CalendarContract.Reminders.EVENT_ID, eventId);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            values.put(CalendarContract.Reminders.MINUTES, 10);
            cr.insert(CalendarContract.Reminders.CONTENT_URI, values);

            pref.edit().putString(""+eventId, "Added").apply();
            Log.i("afterClickHasAdded", hasAdded()+"");
        }

        else if(!needReminder && hasAdded()){
            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
            getContentResolver().delete(uri, null, null);
            pref.edit().remove(""+eventId).apply();
            Toast.makeText(this, "Event removed!", Toast.LENGTH_SHORT).show();
        }

    }


    private void fillViews() {
        eventName.setText(event);
        societyName.setText(society);
        Picasso.get().load(logo).placeholder(getResources().getDrawable(R.drawable.placeholder)).into(societyLogo);
        eventDesc.setText(desc);
        Picasso.get().load(image).placeholder(getResources().getDrawable(R.drawable.placeholder)).into(eventImage);
        eventVenue.setText(venue);
        eventPerson.setText(contact_person);
        eventContact.setText(contact_no);
        eventTiming.setText(getTime(startDate) + " - " + getTime(endDate));
        String start = getDate(startDate);
        String end = getDate(endDate);
        if(start.equals(end)) {
            eventDate.setText(start + " " + getMonth(startDate).substring(0,3) + ", " + startDate.substring(0,4));
        }
        else {
            if(startDate.substring(5,7).equals(endDate.substring(5,7))) {
                eventDate.setText(start + " - " + end + " " + getMonth(startDate).substring(0,3) + ", " + startDate.substring(0,4));
            }
            else if(startDate.substring(0,4).equals(endDate.substring(0,4))) {
                eventDate.setText(start + " " + getMonth(startDate).substring(0,3) + " - " + end + " " + getMonth(endDate).substring(0,3) + ", " + startDate.substring(0,4));
            }
            else {
                eventDate.setText(start + " " + getMonth(startDate).substring(0,3) + ", " + startDate.substring(2,4) + " - " + end + " " + getMonth(endDate).substring(0,3) + ", " + endDate.substring(2,4));
            }
        }
    }


    private void fillData() {
        event = intent.getStringExtra("eventName");
        society = intent.getStringExtra("societyName");
        desc = intent.getStringExtra("eventDesc");
        logo = intent.getStringExtra("societyLogo");
        startDate = intent.getStringExtra("eventStartTime");
        endDate = intent.getStringExtra("eventEndTime");
        image = intent.getStringExtra("image");
        regLink = intent.getStringExtra("regLink");
        venue = intent.getStringExtra("eventVenue");
        contact_person = intent.getStringExtra("contact_person");
        contact_no = intent.getStringExtra("contact_number");
        id = intent.getLongExtra("id", 0);
    }


    private String getTime(String startFullDate) {
        int hr = Integer.parseInt(startFullDate.substring(11, 13));
        int min = Integer.parseInt(startFullDate.substring(14, 16));
        return (hr %12 + ":" + min + " " + ((hr >=12) ? "PM" : "AM"));
    }

    private String getDate(String startFullDate) {
        return startFullDate.substring(8,10);
    }

    private String getMonth(String startFullDate) {
        int monthNo = Integer.parseInt(startFullDate.substring(5, 7)) - 1;
        String month = null;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (monthNo >= 0 && monthNo <= 11 ) {
            month = months[monthNo];
        }
        return month;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(call);
                }
                else {
                    Toast.makeText(EventActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }

            case CALENDAR_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addReminderInCalendar();
                }
                else {
                    Toast.makeText(EventActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

}
