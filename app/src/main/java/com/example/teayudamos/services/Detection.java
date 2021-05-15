package com.example.teayudamos.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.content.Context;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import androidx.annotation.Nullable;

import com.example.teayudamos.R;

import java.text.DecimalFormat;

public class Detection extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    NotificationCompat.Builder notificationBuilder;
    Notification notification;
    NotificationManager manager;
    String NOTIFICATION_CHANNEL_ID = "com.example.teayudamos";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) Detection.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        if(accelerometer == null){
            Toast.makeText(this,"The divise has no Accelerometer!", Toast.LENGTH_LONG).show();

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "com.example.teayudamos";
            String channelName = "Detectando caida - TEAyudamos";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Detectando caida - TEAyudamos")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();

            startForeground(2, notification);
        } else {
            startForeground(1, new Notification());
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float xVal = event.values[0];
        float yVal = event.values[1];
        float zVal = event.values[2];

        double loAccelerationReader = Math.sqrt(Math.pow(xVal, 2)
                + Math.pow(yVal, 2)
                + Math.pow(zVal, 2));
        DecimalFormat precision = new DecimalFormat("0.00");
        double ldAccRound = Double.parseDouble(precision.format(loAccelerationReader));

        if (ldAccRound > 0.3d && ldAccRound<0.5d ) {
            fallDownDetect();
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(Detection.this, "Stop Detecting", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    public void fallDownDetect() {
        Toast.makeText(Detection.this,"Fall Detected",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
