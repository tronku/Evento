package com.example.tronku.eventmanager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Random;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Event> eventArrayList;

    public EventsAdapter(Context context, ArrayList<Event> list) {
        this.context = context;
        eventArrayList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.societyName.setText(eventArrayList.get(i).getSocietyName());
        viewHolder.eventName.setText(eventArrayList.get(i).getEventName());

        String timeString = getTime(eventArrayList.get(i).getStartDateTime()) + "- " + getTime(eventArrayList.get(i).getEndDateTime());
        viewHolder.eventTime.setText(timeString);

        String startDate = getDate(eventArrayList.get(i).getStartDateTime());
        String startMon =  getMonth(eventArrayList.get(i).getStartDateTime()).substring(0,3).toUpperCase();
        viewHolder.startDate.setText(startDate);
        viewHolder.startMonth.setText(startMon);

        Random random = new Random();
        ColorFilter cf = new PorterDuffColorFilter(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)),PorterDuff.Mode.OVERLAY);
        viewHolder.backPic.setColorFilter(cf);

        viewHolder.singleEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView societyName, eventName, eventTime, startDate, startMonth;
        private CardView singleEvent;
        private View colorLayer;
        private ImageView backPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            singleEvent = itemView.findViewById(R.id.singleItem);
            societyName = itemView.findViewById(R.id.societyEvent);
            eventName = itemView.findViewById(R.id.nameEvent);
            eventTime = itemView.findViewById(R.id.timeEvent);
            startDate = itemView.findViewById(R.id.startDate);
            startMonth = itemView.findViewById(R.id.startMonth);
            colorLayer = itemView.findViewById(R.id.colorLayer);
            backPic = itemView.findViewById(R.id.backgroundPic);
        }
    }

    public void updateEvents(ArrayList<Event> list){
        this.eventArrayList = list;
        notifyDataSetChanged();
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
        String s;
        int monthNo = Integer.parseInt(startFullDate.substring(5,7)) - 1;
        String month = null;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (monthNo >= 0 && monthNo <= 11 ) {
            month = months[monthNo];
        }
        return month;
    }
}
