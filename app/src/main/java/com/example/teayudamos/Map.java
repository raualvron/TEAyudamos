package com.example.teayudamos;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.teayudamos.model.Location;
import com.example.teayudamos.recyclers.LocationAdapter;
import com.example.teayudamos.services.Constants;
import com.example.teayudamos.services.SharePref;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.teayudamos.databinding.ActivityMapBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapBinding binding;
    private FloatingActionButton dialogBtn;
    TextView txtAlumn, txtApp;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Location> locationList = new ArrayList<>();
    SharePref sharePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Typeface latoThin = ResourcesCompat.getFont(getApplicationContext(), R.font.lato_thin);
        Typeface latoBold = ResourcesCompat.getFont(getApplicationContext(), R.font.lato_bold);


        dialogBtn = findViewById(R.id.list);

        txtAlumn = findViewById(R.id.user);
        txtAlumn.setText("Localizaciones de " + getAlumnName());
        txtAlumn.setTypeface(latoThin);

        txtApp = findViewById(R.id.nameapp);
        txtApp.setTypeface(latoBold);

        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(Map.this);
            }
        });

        db.collection("routes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                java.util.Map<String, Object> data = document.getData();
                                String action = data.get("action").toString();
                                String datetime = data.get("datetime").toString();
                                String userId = data.get("userId").toString();
                                ArrayList<GeoPoint> points = (ArrayList<GeoPoint>) document.get("points");
                                String road = data.get("road").toString();
                                Location location = new Location(action, datetime, userId, road, points);
                                locationList.add(location);
                            }
                            Location location = locationList.get(locationList.size() -1);
                            addMarkersOnMap(location);
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void showDialog(Activity activity){

        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_list);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ListView listView = (ListView) dialog.findViewById(R.id.listview);
        LocationAdapter adapter = new LocationAdapter(this,R.layout.list_locations, locationList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location locationClicked = locationList.get(position);
                addMarkersOnMap(locationClicked);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void addMarkersOnMap(Location location) {
        ArrayList<GeoPoint> points = location.getPoints();
        LatLng latlng = null;
        for(int i=0; i < points.size(); i++) {
            GeoPoint point = points.get(i);
            latlng = new LatLng(point.getLatitude(), point.getLongitude());


        }
        mMap.addMarker(new MarkerOptions().position(latlng).title(location.getRoad()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latlng.latitude, latlng.longitude), 15));

    }

    private String getAlumnName() {
        sharePref = new SharePref(getBaseContext());
        return sharePref.getSharedPrefString(Constants.ALUMN);
    }
}