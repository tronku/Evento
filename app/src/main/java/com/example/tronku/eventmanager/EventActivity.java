package com.example.tronku.eventmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.LocaleData;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;
import de.hdodenhof.circleimageview.CircleImageView;

public class EventActivity extends AppCompatActivity {

    @BindView(R.id.event_Name)
    TextView eventName;
    @BindView(R.id.society_Name)
    TextView societyName;
    @BindView(R.id.society_Logo)
    CircleImageView societyLogo;
    @BindView(R.id.timings)
    TextView eventTiming;
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

    private Intent intent, call;
    private static final int REQUEST_CODE = 101;
    private String event, society, logo, desc, startDate, endDate, image, regLink, venue, contact_person, contact_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);
        intent = getIntent();

        fillData();
        fillViews();

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
                call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact_no));
                if (ActivityCompat.checkSelfPermission(EventActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EventActivity.this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CODE);
                }
                startActivity(call);
            }
        });

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
    }

    private String getTime(String startFullDate) {
        int hr = Integer.parseInt(startFullDate.substring(11,13));
        int min = Integer.parseInt(startFullDate.substring(14,16));
        String time = hr%12 + ":" + min + " " + ((hr>=12) ? "PM" : "AM");
        return time;
    }

    private String getDate(String startFullDate) {
        return startFullDate.substring(8,10);
    }

    private String getMonth(String startFullDate) {
        int monthNo = Integer.parseInt(startFullDate.substring(5,7)) - 1;
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
        }
    }
}
