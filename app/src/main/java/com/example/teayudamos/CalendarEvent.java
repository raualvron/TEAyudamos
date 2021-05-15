package com.example.teayudamos;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.example.teayudamos.recyclers.MedicineAdapter;
import com.example.teayudamos.services.Constants;
import com.example.teayudamos.services.SharePref;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CalendarEvent extends AppCompatActivity {
    RecyclerView recyclerView;
    MedicineAdapter adapter;
    CompactCalendarView compactCalendar;
    private TextView compactMonth;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", new Locale("es", "ES"));
    String datetime = dateFormatMonth.format(new Date());
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharePref sharePref;
    TextView txtAlumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_calendar);

        recyclerView = findViewById(R.id.events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtAlumn = findViewById(R.id.user);
        txtAlumn.setText("Calendario de " + getAlumnName());

        compactMonth = findViewById(R.id.month);
        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setLocale(TimeZone.getTimeZone("ES"), new Locale("es", "ES"));
        compactCalendar.setUseThreeLetterAbbreviation(true);

        compactMonth.setText(datetime.toString());

        db.collection("medicine")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> data = document.getData();
                                String startEvent = data.get("start").toString();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                Date date = null;
                                try {
                                    date = sdf.parse(startEvent);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long millis = date.getTime();
                                int hexInt = Color.parseColor(data.get("color").toString());
                                String[] content = {data.get("title").toString(), data.get("start").toString(), data.get("end").toString(), data.get("color").toString()};

                                compactCalendar.addEvent(new Event(hexInt, millis, content));
                            }
                        } else {

                        }
                    }
                });

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();
                List<Event> events = compactCalendar.getEvents(dateClicked);

                adapter = new MedicineAdapter(context, events);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                compactMonth.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
    }

    private String getAlumnName() {
        sharePref = new SharePref(getBaseContext());
        return sharePref.getSharedPrefString(Constants.ALUMN);
    }
}