package com.example.tronku.eventmanager;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocietyAdapter extends RecyclerView.Adapter<SocietyAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Society> societyList;
    private boolean upcoming;

    public SocietyAdapter(Context context, ArrayList<Society> societyList, boolean upcoming) {
        this.context = context;
        this.societyList = societyList;
        this.upcoming = upcoming;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.society_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.societyName.setText(societyList.get(i).getName());
        Picasso.get().load(societyList.get(i).getUri()).placeholder(context.getResources().getDrawable(R.drawable.placeholder)).into(viewHolder.societyLogo);
        viewHolder.societyType.setText(societyList.get(i).getType());
        viewHolder.societyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filter = new Intent(context, MainActivity.class);
                filter.putExtra("name", societyList.get(i).getName());
                filter.putExtra("logo", societyList.get(i).getUri());
                filter.putExtra("society", societyList.get(i).getId());
                if(upcoming)
                    filter.putExtra("upcoming","true");
                else
                    filter.putExtra("past", "true");

                filter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(filter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return societyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView societyName;
        private CardView societyItem;
        private TextView societyType;
        private CircleImageView societyLogo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            societyName = itemView.findViewById(R.id.societyName);
            societyItem = itemView.findViewById(R.id.societyItem);
            societyType = itemView.findViewById(R.id.societyType);
            societyLogo = itemView.findViewById(R.id.logo);
        }
    }

    public void updateData(ArrayList<Society> list){
        societyList = list;
        notifyDataSetChanged();
    }
}
