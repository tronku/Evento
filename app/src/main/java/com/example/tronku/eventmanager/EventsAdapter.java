package com.example.tronku.eventmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        viewHolder.societyName.setText("Developer Student Clubs");
        viewHolder.eventName.setText("Python Workshop");
        viewHolder.eventTime.setText("4:30 - 6:30 pm");
        viewHolder.startDate.setText("24");
        viewHolder.startMonth.setText("OCT");
        //int colorId = getLayerColor(i);
        //viewHolder.colorLayer.setBackgroundColor(getLayerColor(i));
        viewHolder.singleEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView societyName, eventName, eventTime, startDate, startMonth;
        private CardView singleEvent;
        private View colorLayer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            singleEvent = itemView.findViewById(R.id.singleItem);
            societyName = itemView.findViewById(R.id.societyEvent);
            eventName = itemView.findViewById(R.id.nameEvent);
            eventTime = itemView.findViewById(R.id.timeEvent);
            startDate = itemView.findViewById(R.id.startDate);
            startMonth = itemView.findViewById(R.id.startMonth);
            colorLayer = itemView.findViewById(R.id.colorLayer);
        }
    }

    public int getLayerColor(int i) {
        int randomColorCode = i%2;
        if(randomColorCode==0)
            return R.color.orangeLayer;
        //else if(randomColorCode==1)
          //  return R.color.greenLayer;
        /*else if(randomColorCode==2)
            return R.color.orangeLayer;
        else if(randomColorCode==3)
            return R.color.purpleLayer;*/

        else
            return R.color.colorAccentDark;
    }
}
