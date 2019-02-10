package tronku.dsc.eventmanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import tronku.dsc.eventmanager.EventActivity;
import tronku.dsc.eventmanager.POJO.Event;
import tronku.dsc.eventmanager.R;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Event> eventArrayList;
    //private ArrayList<Event> eventFilteredList;

    public EventsAdapter(Context context, ArrayList<Event> list) {
        this.context = context;
        eventArrayList = list;
        //eventFilteredList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.societyName.setText(eventArrayList.get(i).getSocietyName());
        viewHolder.eventName.setText(eventArrayList.get(i).getEventName());

        String timeString = getTime(eventArrayList.get(i).getStartDateTime()) + " - " + getTime(eventArrayList.get(i).getEndDateTime());
        viewHolder.eventTime.setText(timeString);

        String startDate = getDate(eventArrayList.get(i).getStartDateTime());
        String startMon =  getMonth(eventArrayList.get(i).getStartDateTime()).substring(0,3).toUpperCase();
        viewHolder.startDate.setText(startDate);
        viewHolder.startMonth.setText(startMon);

        getColor(viewHolder.layer);

        viewHolder.singleEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.eventLayer.setVisibility(View.VISIBLE);
                Intent event = new Intent(context, EventActivity.class);
                event.putExtra("societyName", eventArrayList.get(i).getSocietyName());
                event.putExtra("eventName", eventArrayList.get(i).getEventName());
                event.putExtra("eventDesc", eventArrayList.get(i).getEventDesc());
                event.putExtra("eventStartTime", eventArrayList.get(i).getStartDateTime());
                event.putExtra("eventEndTime", eventArrayList.get(i).getEndDateTime());
                event.putExtra("eventVenue", eventArrayList.get(i).getVenue());
                event.putExtra("contact_person", eventArrayList.get(i).getContact_person());
                event.putExtra("contact_number", eventArrayList.get(i).getContact_no());
                event.putExtra("image", eventArrayList.get(i).getImgUrl());
                event.putExtra("societyLogo", eventArrayList.get(i).getSocietyLogo());
                event.putExtra("regLink", eventArrayList.get(i).getRegLink());
                event.putExtra("id", eventArrayList.get(i).getId());
                context.startActivity(event);
                viewHolder.eventLayer.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    //searching
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                String searchString = constraint.toString();
//                if (searchString.isEmpty())
//                    eventFilteredList = eventArrayList;
//                else {
//                    ArrayList<Event> filteredList = new ArrayList<>();
//                    for (Event event: eventArrayList) {
//                        if (event.getEventName().contains(searchString) || event.getSocietyName().contains(searchString) ||
//                                event.getEventDesc().contains(searchString))
//                            filteredList.add(event);
//                    }
//
//                    eventFilteredList = filteredList;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = eventFilteredList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                eventFilteredList = (ArrayList<Event>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView societyName, eventName, eventTime, startDate, startMonth;
        private CardView singleEvent;
        private View layer;
        private LinearLayout eventLayer;

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

    private void getColor(View layer) {
        Random random = new Random();
        int randno = random.nextInt(5);
        if(randno==0){
            layer.setBackgroundColor(context.getResources().getColor(R.color.orangeLayer));
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
