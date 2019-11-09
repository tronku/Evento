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
import android.widget.TextView;

import tronku.dsc.eventmanager.MainActivity;
import tronku.dsc.eventmanager.POJO.Society;
import tronku.dsc.eventmanager.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocietyAdapter extends RecyclerView.Adapter<SocietyAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Society> societyList;
    private ArrayList<Society> societyFilteredList;
    private boolean upcoming;

    public SocietyAdapter(Context context, ArrayList<Society> societyList, boolean upcoming) {
        this.context = context;
        this.societyList = societyList;
        this.upcoming = upcoming;
        societyFilteredList = societyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.society_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.societyName.setText(societyFilteredList.get(i).getName());
        if (societyFilteredList.get(i).getUri() != null){
            Picasso.get()
                    .load(societyFilteredList.get(i).getUri())
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.societyLogo);
        }
        else {
            Picasso.get()
                    .load(R.drawable.placeholder)
//                .placeholder(context.getResources().getDrawable(R.drawable.placeholder))
                    .into(viewHolder.societyLogo);
        }
        viewHolder.societyType.setText(societyFilteredList.get(i).getType());
        viewHolder.societyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filter = new Intent(context, MainActivity.class);
                filter.putExtra("name", societyFilteredList.get(i).getName());
                filter.putExtra("logo", societyFilteredList.get(i).getUri());
                filter.putExtra("society", societyFilteredList.get(i).getId());
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
        return societyFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();
                if (searchString.isEmpty())
                    societyFilteredList = societyList;
                else {
                    ArrayList<Society> filteredList = new ArrayList<>();
                    for (Society society : societyList) {
                        if (society.getName().toLowerCase().contains(searchString))
                            filteredList.add(society);
                    }

                    societyFilteredList = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = societyFilteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                societyFilteredList = (ArrayList<Society>) results.values;
                notifyDataSetChanged();
            }
        };
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
        societyFilteredList = list;
        notifyDataSetChanged();
    }
}
