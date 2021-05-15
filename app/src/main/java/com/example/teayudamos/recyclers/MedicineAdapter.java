package com.example.teayudamos.recyclers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.example.teayudamos.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder>  {
    List<Event> events;
    Context context;

    public MedicineAdapter(Context context, List<Event> events) {
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public MedicineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_events, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineAdapter.ViewHolder holder, int position) {
        String[] content = (String[]) events.get(position).getData();
        Locale esLocale = new Locale("es", "ES");

        String title = content[0];
        String colour = content[3];
        String start = content[1];
        String end = content[2];
        SimpleDateFormat sdp =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date dateStart = null;
        Date dateEnd = null;
        try {
            dateStart = sdp.parse(start);
            dateEnd = sdp.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy", esLocale);
        SimpleDateFormat sdt = new SimpleDateFormat("h:mm a", esLocale);

        int hexInt = Color.parseColor(colour);

        holder.title.setText(title + " | " + sdf.format(dateStart).toString());
        holder.start.setText("Comienzo: " + sdt.format(dateStart).toString());
        holder.end.setText("Fin: " + sdt.format(dateEnd).toString());
        holder.view.setCardBackgroundColor(hexInt);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView start;
        TextView end;
        CardView  view;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.title);
            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.end);
            view = itemView.findViewById(R.id.card);
        }
    }
}
