package tronku.dsc.eventmanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import tronku.dsc.eventmanager.EventActivity;
import tronku.dsc.eventmanager.POJO.Event;
import tronku.dsc.eventmanager.R;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Event> eventArrayList;
    private ArrayList<Event> eventFilteredList;
    private HashMap<String, Drawable> categImg;

    public EventsAdapter(Context context, ArrayList<Event> list) {
        this.context = context;
        eventArrayList = list;
        eventFilteredList = list;
        categImg = new HashMap<>();
        categImg.put("Technical", context.getDrawable(R.drawable.technical));
        categImg.put("Dramatics", context.getDrawable(R.drawable.drama));
        categImg.put("Literary", context.getDrawable(R.drawable.literary));
        categImg.put("Music & Dance", context.getDrawable(R.drawable.musicdance));
        categImg.put("Personal Development", context.getDrawable(R.drawable.personaldev));
        categImg.put("Quizzing", context.getDrawable(R.drawable.quiz));
        categImg.put("Entrepreneurship", context.getDrawable(R.drawable.startup));
        categImg.put("Civil", context.getDrawable(R.drawable.civil));
        categImg.put("Elec", context.getDrawable(R.drawable.electronics));
        categImg.put("Mechanical", context.getDrawable(R.drawable.mechanical));
        categImg.put("Photo & Media", context.getDrawable(R.drawable.photography));
        categImg.put("Fun & Extra", context.getDrawable(R.drawable.fun));
        categImg.put("Others", context.getDrawable(R.drawable.workshop));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        String category = eventFilteredList.get(i).getType();
        if (!category.isEmpty())
            viewHolder.eventBanner.setImageDrawable(categImg.get(category));

        viewHolder.societyName.setText(eventFilteredList.get(i).getSocietyName());
        viewHolder.eventName.setText(eventFilteredList.get(i).getEventName());

        String timeString = getTime(eventFilteredList.get(i).getStartDateTime()) + " - " + getTime(eventFilteredList.get(i).getEndDateTime());
        viewHolder.eventTime.setText(timeString);

        String startDate = getDate(eventFilteredList.get(i).getStartDateTime());
        String startMon =  getMonth(eventFilteredList.get(i).getStartDateTime()).substring(0,3).toUpperCase();
        viewHolder.startDate.setText(startDate);
        viewHolder.startMonth.setText(startMon);

        getColor(viewHolder.layer);

        viewHolder.singleEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.eventLayer.setVisibility(View.VISIBLE);
                Intent event = new Intent(context, EventActivity.class);
                event.putExtra("societyName", eventFilteredList.get(i).getSocietyName());
                event.putExtra("eventName", eventFilteredList.get(i).getEventName());
                event.putExtra("eventDesc", eventFilteredList.get(i).getEventDesc());
                event.putExtra("eventStartTime", eventFilteredList.get(i).getStartDateTime());
                event.putExtra("eventEndTime", eventFilteredList.get(i).getEndDateTime());
                event.putExtra("eventVenue", eventFilteredList.get(i).getVenue());
                event.putExtra("contact_person", eventFilteredList.get(i).getContact_person());
                event.putExtra("contact_number", eventFilteredList.get(i).getContact_no());
                event.putExtra("image", eventFilteredList.get(i).getImgUrl());
                event.putExtra("societyLogo", eventFilteredList.get(i).getSocietyLogo());
                event.putExtra("regLink", eventFilteredList.get(i).getRegLink());
                event.putExtra("id", eventFilteredList.get(i).getId());
                context.startActivity(event);
                viewHolder.eventLayer.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventFilteredList.size();
    }

    //searching
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();
                if (searchString.isEmpty())
                    eventFilteredList = eventArrayList;
                else {
                    ArrayList<Event> filteredList = new ArrayList<>();
                    for (Event event: eventArrayList) {
                        if (event.getEventName().toLowerCase().contains(searchString) || event.getSocietyName().toLowerCase().contains(searchString))
                            filteredList.add(event);
                    }

                    eventFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = eventFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                eventFilteredList = (ArrayList<Event>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView societyName, eventName, eventTime, startDate, startMonth;
        private CardView singleEvent;
        private View layer;
        private LinearLayout eventLayer;
        private ImageView eventBanner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            singleEvent = itemView.findViewById(R.id.singleItem);
            societyName = itemView.findViewById(R.id.societyEvent);
            eventName = itemView.findViewById(R.id.nameEvent);
            eventTime = itemView.findViewById(R.id.timeEvent);
            startDate = itemView.findViewById(R.id.startDate);
            startMonth = itemView.findViewById(R.id.startMonth);
            layer = itemView.findViewById(R.id.layer);
            eventLayer = itemView.findViewById(R.id.eventLayer);
            eventBanner = itemView.findViewById(R.id.backgroundPic);
        }
    }

    public void updateEvents(ArrayList<Event> list){
        this.eventFilteredList = list;
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

    private void getColor(View layer) {
        Random random = new Random();
        int randno = random.nextInt(5);
        if(randno==0){
            layer.setBackgroundColor(context.getResources().getColor(R.color.yellowLayer));
        }
        else if(randno==1){
            layer.setBackgroundColor(context.getResources().getColor(R.color.greenLayer));
        }
        else if(randno==2){
            layer.setBackgroundColor(context.getResources().getColor(R.color.purpleLayer));
        }
        else if(randno==3){
            layer.setBackgroundColor(context.getResources().getColor(R.color.redLayer));
        }
        else {
            layer.setBackgroundColor(context.getResources().getColor(R.color.colorAccentDark));
        }
    }

}
