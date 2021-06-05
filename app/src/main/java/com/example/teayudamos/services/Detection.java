package com.example.teayudamos.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.annotation.Nullable;

import com.example.teayudamos.Home;
import com.example.teayudamos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Detection extends Service implements SensorEventListener {
    public final static String EXTRA_MESSAGE = "com.example.teayudamos";
    final String TAG = "NOTIFICATION TAG";
    public double ax, ay, az;
    public double a_norm;
    public int i = 0;
    static int BUFF_SIZE = 50;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    FirebaseFirestore db;
    SharePref sharePref;
    NotificationCompat.Builder notificationBuilder;
    String NOTIFICATION_CHANNEL_ID = "com.example.teayudamos";
    Notification notification;
    NotificationManager manager;
    static public double[] window = new double[BUFF_SIZE];
    double sigma = 0.5, th = 19.6, th1 = 5, th2 = 2;
    public static String curr_state, prev_state;
    String previousRequested = "";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
        sharePref = new SharePref(getBaseContext());
        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        initialize();
        locationRequested();
    }

    private void initialize() {
        for (i = 0; i < BUFF_SIZE; i++) {
            window[i] = 0;
        }
        prev_state = "none";
        curr_state = "none";
    }

    private void AddData(double ax, double ay, double az) {
        a_norm = Math.sqrt((ax * ax) + (ay * ay) + (az * az));
        for (i = 0; i <= BUFF_SIZE - 2; i++) {
            window[i] = window[i + 1];
        }
        window[BUFF_SIZE - 1] = a_norm;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) Detection.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (accelerometer == null) {
            Toast.makeText(this, "The divise has no Accelerometer!", Toast.LENGTH_LONG).show();

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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
            AddData(ax, ay, az);
            fallQuestionMark(window);
            SystemState(curr_state, prev_state);
            if (!prev_state.equalsIgnoreCase(curr_state)) {
                prev_state = curr_state;
            }
        }
    }

    private void SystemState(String curr_state1, String prev_state1) {
        if (!prev_state1.equalsIgnoreCase(curr_state1)) {
            if (curr_state1.equalsIgnoreCase("fall")) {
                fallDownDetect();
                saveCoordinates("caida");
                sendPushNotification();
                makeEmergencyCall();
            }
        }
    }

    private String getAddressByGeo(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        return addresses.get(0).getAddressLine(0);
    }

    private void locationRequested() {
        final DocumentReference docRef = db.collection("alumns").document(sharePref.getSharedPrefString(Constants.DOCUMENT_ID));
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String currentRequested = snapshot.get("requested").toString();
                if (snapshot != null && snapshot.exists()) {
                    if (!previousRequested.isEmpty() && !previousRequested.equals(currentRequested)) {
                        saveCoordinates("ruta");
                    }
                }
                previousRequested = currentRequested;
            }
        });
    }

    private void sendPushNotification() {
        new PushNotification(getApplicationContext());
    }

    private void makeEmergencyCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sharePref.getSharedPrefString(Constants.TELEPHONE)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void saveCoordinates(String action) {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        HashMap<String,Object> dataMessage = new HashMap<>();
        dataMessage.put("action", action);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        dataMessage.put("datetime", currentDate);
        try {
            dataMessage.put("road", getAddressByGeo(latitude, longitude));
        } catch (IOException e) {
            dataMessage.put("road", "");
        }
        GeoPoint geopoints = new GeoPoint(latitude, longitude);

        dataMessage.put("points", Arrays.asList(geopoints));
        dataMessage.put("userId", sharePref.getSharedPrefString(Constants.USER_ID));

        // Add a new document with a generated ID
        db.collection("routes")
                .add(dataMessage)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Chat Activity", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Chat Activity", "Error adding document", e);
                    }
                });
    }

    private int calcFall(double[] window2){
        int count = 0;
        for(i=1;i<BUFF_SIZE;i++){
            if((window2[i]-window2[i-1])>th) {
                count = count+1;
            }
        }
        return count;
    }

    private void fallQuestionMark(double window12[]){
        int cek = calcFall(window12);
        if(cek>0){
            curr_state = "fall";
        }else{
            curr_state = "none";
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(Detection.this, "Stop detectando caida", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    public void fallDownDetect() {
        Toast.makeText(Detection.this,"Caida detectada",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
