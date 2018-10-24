package com.example.tronku.eventmanager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class SocietyAdapter extends RecyclerView.Adapter<SocietyAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Society> societyList;
    private ArrayList<String> selectedSociety = new ArrayList<>();

    public SocietyAdapter(Context context, ArrayList<Society> societyList) {
        this.context = context;
        this.societyList = societyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.society_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.societyName.setText(societyList.get(viewHolder.getAdapterPosition()).getName());
        viewHolder.societyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(societyList.get(viewHolder.getAdapterPosition()).isSelected()) {
                    societyList.get(viewHolder.getAdapterPosition()).setSelected(false);
                    viewHolder.societyName.setTextColor(context.getResources().getColor(android.R.color.white));
                }
                else{
                    societyList.get(viewHolder.getAdapterPosition()).setSelected(true);
                    viewHolder.societyName.setTextColor(context.getResources().getColor(R.color.orangeLayer));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return societyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView societyName;
        private RelativeLayout societyItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            societyName = itemView.findViewById(R.id.societyName);
            societyItem = itemView.findViewById(R.id.societyItem);
        }
    }

    public void filterData() {

        for(int i=0;i<societyList.size();i++) {
            if(societyList.get(i).isSelected())
                selectedSociety.add(societyList.get(i).getName());
        }

        Intent filter = new Intent(context, MainActivity.class);
        filter.putStringArrayListExtra("selectedSocieties", selectedSociety);
        context.startActivity(filter);
    }
}
