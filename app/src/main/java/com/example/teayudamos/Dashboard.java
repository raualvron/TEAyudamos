package com.example.teayudamos;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.teayudamos.services.Constants;
import com.example.teayudamos.services.IntentActivity;
import com.example.teayudamos.services.SharePref;


public class Dashboard extends AppCompatActivity {

    SharePref sharePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dashboard);

        RelativeLayout calendar = findViewById(R.id.calendar_layout);
        RelativeLayout chat = findViewById(R.id.chat_layout);
        RelativeLayout location = findViewById(R.id.location_layout);
        RelativeLayout scolar = findViewById(R.id.scolar_layout);
        RelativeLayout profile = findViewById(R.id.profile_layout);
        RelativeLayout logout = findViewById(R.id.logout_layout);

        //Set Alumn name
        TextView alumn = findViewById(R.id.user);
        alumn.setText(getAlumnName());

        calendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivity(CalendarEvent.class);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivity(Chat.class);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivity(Map.class);
            }
        });

        scolar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivity(Actas.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sharePref.clearAllSharedPref();
                IntentActivity intent = new IntentActivity(Dashboard.this,
                        Login.class);
                intent.startActivity();
                intent.finishActivity();

            }
        });
    }

    private void goToActivity(Class activityClass) {
        IntentActivity intent = new IntentActivity(Dashboard.this,
                activityClass);
        intent.startActivity();
    }

    private String getAlumnName() {
        sharePref = new SharePref(getBaseContext());
        return sharePref.getSharedPrefString(Constants.ALUMN);
    }
}