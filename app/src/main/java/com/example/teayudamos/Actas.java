package com.example.teayudamos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.teayudamos.model.Acta;
import com.example.teayudamos.recyclers.ActaAdapter;
import com.example.teayudamos.services.Constants;
import com.example.teayudamos.services.SharePref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;

public class Actas extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Acta> actasList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ActaAdapter adapter;
    TextView txtAlumn;
    SharePref sharePref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acta);
        getSupportActionBar().hide();
        recyclerView = findViewById(R.id.actas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtAlumn = findViewById(R.id.user);
        txtAlumn.setText("Actas escolares de " + getAlumnName());
        loadActas();
    }


    private void loadAdapter() {
        Context context = getApplicationContext();
        adapter = new ActaAdapter(context, actasList);
        recyclerView.setAdapter(adapter);
    }

    private String getAlumnName() {
        sharePref = new SharePref(getBaseContext());
        return sharePref.getSharedPrefString(Constants.ALUMN);
    }

    private void loadActas() {
        db.collection("minutes").orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                java.util.Map<String, Object> data = document.getData();
                                String date = data.get("date").toString();
                                String description = data.get("description").toString();
                                String title = data.get("title").toString();
                                String type = data.get("type").toString();
                                Acta acta = new Acta(date, description, title, type);
                                actasList.add(acta);
                            }
                            loadAdapter();
                        }
                    }
                });
    }


}