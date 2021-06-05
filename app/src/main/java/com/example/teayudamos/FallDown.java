package com.example.teayudamos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.teayudamos.services.Detection;

import java.text.DecimalFormat;

public class FallDown extends AppCompatActivity {


    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falldown);
        getSupportActionBar().hide();
        startService();
    }

    public void startService() {
        Toast.makeText(getApplicationContext(),"Start Fall detection in Background",Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(getApplicationContext(), Detection.class);
        ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
    }
}