package com.example.teayudamos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teayudamos.model.Acta;
import com.example.teayudamos.services.Constants;
import com.example.teayudamos.services.SharePref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    EditText txtSchools, txtGrades, txtDiseases, txtAlumnName, txtAddress, txtBirthday, txtFather, txtMother, txtTelephone, txtEmail, txtPassword, txtRePassword;
    Button btnUpdate;
    TextView btnUsername;
    FirebaseFirestore db;
    SharePref sharePref;
    Map<String,Object> alumn;
    String schoolName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        db = FirebaseFirestore.getInstance();
        sharePref = new SharePref(getBaseContext());
        getAlumnByUser();


        txtAlumnName = findViewById(R.id.et_name);
        txtAddress = findViewById(R.id.et_address);
        txtBirthday = findViewById(R.id.et_birthday);
        // Not editable
        txtBirthday.setKeyListener(null);
        txtFather = findViewById(R.id.et_father);
        txtMother = findViewById(R.id.et_mother);
        txtTelephone = findViewById(R.id.et_telephone);
        txtEmail = findViewById(R.id.et_email);
        txtPassword = findViewById(R.id.et_password);
        txtRePassword = findViewById(R.id.et_repassword);

        txtDiseases = findViewById(R.id.et_diseases);
        txtGrades = findViewById(R.id.et_grades);
        txtSchools = findViewById(R.id.et_schools);

        btnUpdate = findViewById(R.id.btn_update);
        btnUsername = findViewById(R.id.user);

        txtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();

                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                DatePickerDialog dpd = new DatePickerDialog(Profile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtBirthday.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, year, month, day);

                dpd.show();
            }
        });

    }

    private void getAlumnByUser() {
        db.collection("alumns").whereEqualTo("userId", sharePref.getSharedPrefString(Constants.USER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            alumn = task.getResult().getDocuments().get(0).getData();
                            fillAlumnForm(alumn);
                        }
                    }
                });
    }

    private void getSchoolById(String schoolId) {
        db.collection("schools")
                .document(schoolId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        txtSchools.setText(document.get("schoolname").toString());
                    }
                }
            }
        });
    }

    private void fillAlumnForm(Map<String,Object> alumn) {
        ArrayList<String> parent = (ArrayList<String>) alumn.get("parent");

        getSchoolById(alumn.get("schoolId").toString());

        txtAlumnName.setText(alumn.get("name").toString());
        txtAddress.setText(alumn.get("address").toString());
        txtBirthday.setText(alumn.get("datebirth").toString());
        txtDiseases.setText(alumn.get("disorder").toString());
        txtGrades.setText(alumn.get("grade").toString());
        txtTelephone.setText(alumn.get("telephone").toString());
        txtEmail.setText(sharePref.getSharedPrefString(Constants.EMAIL));

        txtFather.setText(parent.get(0));
        txtMother.setText(parent.get(1));

        txtPassword.setText("contraseña");
        txtRePassword.setText("contraseña");
        txtEmail.setText(sharePref.getSharedPrefString(Constants.EMAIL));

        btnUsername.setText("Perfil de " + sharePref.getSharedPrefString(Constants.ALUMN));
    }
}