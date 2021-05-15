package com.example.teayudamos.recyclers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teayudamos.R;
import com.example.teayudamos.model.Acta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActaAdapter extends RecyclerView.Adapter<ActaAdapter.ViewHolder>  {
    ArrayList<Acta> actas;
    Context context;

    public ActaAdapter(Context context, ArrayList<Acta> actas) {
        this.actas = actas;
        this.context = context;
    }

    @NonNull
    @Override
    public ActaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_actas, parent, false);
        ActaAdapter.ViewHolder viewHolder = new ActaAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ActaAdapter.ViewHolder holder, int position) {
        Locale esLocale = new Locale("es", "ES");
        String title = actas.get(position).getTitle();
        String description = actas.get(position).getDescription();
        String date = actas.get(position).getDate();

        SimpleDateFormat sdp =  new SimpleDateFormat("yyyy-MM-dd");
        Date dateFormat = null;
        try {
            dateFormat = sdp.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy", esLocale);

        String type = actas.get(position).getType();
        holder.title.setText(title);
        holder.description.setText(description);
        holder.date.setText(sdf.format(dateFormat).toString());

        if (type.equals("success")) {
            holder.layout.setBackgroundColor(Color.parseColor("#95e1d3"));
        } else if (type.equals("warning")) {
            holder.layout.setBackgroundColor(Color.parseColor("#fce28a"));
        } else {
            holder.layout.setBackgroundColor(Color.parseColor("#ff75a0"));
        }
    }

    @Override
    public int getItemCount() {
        return actas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView description;
        TextView title;
        CardView layout;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            date = itemView.findViewById(R.id.dat);
            description = itemView.findViewById(R.id.description);
            title = itemView.findViewById(R.id.title);
            layout = itemView.findViewById(R.id.layoutacta);
        }
    }
}
