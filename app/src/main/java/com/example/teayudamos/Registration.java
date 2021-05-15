package com.example.teayudamos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.teayudamos.services.IntentActivity;
import com.example.teayudamos.services.SharePref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Registration extends AppCompatActivity {

    List<String> diseasesList = new ArrayList<String>();
    List<String> gradeList = new ArrayList<String>();
    List<String> schoolList = new ArrayList<String>();

    List<EditText> editTextList = new ArrayList<EditText>();
    List<Spinner> spinnerList = new ArrayList<Spinner>();

    HashMap<String, String> schoolsByUi = new HashMap<String, String>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    boolean valid = false;

    int diseasesListSize = 0;
    int gradeListSize = 0;
    int schoolsListSize = 0;

    Spinner txtSchools, txtGrades, txtDiseases;
    EditText txtAlumnName, txtAddress, txtBirthday, txtFather, txtMother, txtTelephone, txtEmail, txtPassword, txtRePassword;
    Button btRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();

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

        btRegister = findViewById(R.id.btn_register);

        addDiseasesToList();
        addGradeList();
        addFieldList();

        getSchoolList(); //addSchoolToList

        addAdapterToList(diseasesList, diseasesListSize, txtDiseases);
        addAdapterToList(gradeList, gradeListSize, txtGrades);

        for (Spinner txt: spinnerList) {
            txt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    txt.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_custom));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }
            });
        }


        for (EditText txt: editTextList) {
            txt.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String txtValue = txt.getText().toString();
                    if (txtValue.isEmpty()) {
                        Drawable image = getApplicationContext().getResources().getDrawable( R.drawable.ic_validation );
                        image.setBounds( 0, 0, 60, 60 );
                        txt.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_validation));
                        txt.setCompoundDrawables(image, null, null, null);
                        valid = false;
                    } else {
                        valid = true;
                        txt.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_custom));
                        txt.setCompoundDrawables(null, null, null, null);
                    }
                }
            });
        }

        btRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                validate();
                if(valid) {
                    createEmailAndPass(txtEmail.getText().toString(), txtPassword.getText().toString());
                }
            }
        });


        txtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();

                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                DatePickerDialog dpd = new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtBirthday.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, year, month, day);

                dpd.show();
            }
        });
    }

    private void addAdapterToList(List<String> list, int listSize, Spinner selector) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, list) {
            @Override
            public int getCount() {
                return listSize; // Truncate the list
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selector.setAdapter(dataAdapter);
        selector.setSelection(listSize);
    }

    private void createEmailAndPass(String email, String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            user.sendEmailVerification();
                            createAlumn(user.getUid());
                        }
                    }
                });
    }

    private void createAlumn(String uid) {
        // Create a new user with a first and last name
        HashMap<String, Object> alumn = new HashMap<>();

        SimpleDateFormat input = new SimpleDateFormat("dd/mm/yyyy");
        Date dateValue = null;
        try {
            dateValue = input.parse(txtBirthday.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat output = new SimpleDateFormat("yyy-mm-dd")
                ;
        String[] parents = {txtFather.getText().toString(), txtMother.getText().toString()};

        alumn.put("address", txtAddress.getText().toString());
        alumn.put("datebirth", output.format(dateValue).toString());
        alumn.put("disorder", txtDiseases.getSelectedItem().toString());
        alumn.put("grade", txtGrades.getSelectedItem().toString());
        alumn.put("name", txtAlumnName.getText().toString());
        alumn.put("schoolId", getSchoolId(txtSchools.getSelectedItem().toString()));
        alumn.put("telephone", txtTelephone.getText().toString());
        alumn.put("parents", Arrays.asList(parents));
        alumn.put("userId", uid);

        getTokenDevice(alumn);
    }
    private void getTokenDevice(HashMap<String, Object> alumn) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            alumn.put("token", token);
                            saveAlumn(alumn);
                        }

                    }
                });
    }

    private void saveAlumn(HashMap<String, Object> alumn) {
        db.collection("alumns")
                .add(alumn)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Registration.this, "Usuario registrado con exito, confirme su cuenta en el email de confirmación enviado", Toast.LENGTH_LONG).show();
                        Registration.this.goToLogin();
                    }
                });
    }

    private void getSchoolList() {
        db.collection("schools")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                java.util.Map<String, Object> data = document.getData();
                                String uid = document.getId();
                                String schoolName = data.get("schoolname").toString();
                                schoolsByUi.put(uid, schoolName);
                            }
                            addSchoolToList();
                        }
                    }
                });
    }

    private void validate() {
        valid = true;

        Drawable image = getApplicationContext().getResources().getDrawable(R.drawable.ic_validation);
        image.setBounds(0, 0, 60, 60);

        for (EditText txt: editTextList) {
            String txtValue = txt.getText().toString();
            if (txtValue.isEmpty()) {
                txt.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_validation));
                txt.setCompoundDrawables(image, null, null, null);
                valid = false;
            }
        }
        for (Spinner txt: spinnerList) {
            String txtValue = txt.getSelectedItem().toString();
            if (txtValue.isEmpty() || txtValue.contains("Selecciona")) {
                txt.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_validation));
                valid = false;
            } else {
                txt.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_custom));
            }
        }
        // Passwords
        if (!txtPassword.getText().toString().equals((txtRePassword.getText().toString()))) {
            valid = false;
            txtPassword.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_validation));
            txtPassword.setCompoundDrawables(image, null, null, null);
            txtRePassword.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_validation));
            txtRePassword.setCompoundDrawables(image, null, null, null);
        } else {
            txtPassword.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_custom));
            txtRePassword.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_custom));
        }
    }

    private void addFieldList() {
        editTextList.add(txtAlumnName);
        editTextList.add(txtAddress);
        editTextList.add(txtBirthday);
        editTextList.add(txtFather);
        editTextList.add(txtMother);
        editTextList.add(txtTelephone);
        editTextList.add(txtEmail);
        editTextList.add(txtPassword);
        editTextList.add(txtRePassword);

        spinnerList.add(txtDiseases);
        spinnerList.add(txtGrades);
        spinnerList.add(txtSchools);

    }

    private void addGradeList() {
        for (int i = 1; i < 5; ++i) {
            gradeList.add(i + "°A");
            gradeList.add(i + "°B");
            gradeList.add(i + "°C");
        }
        gradeList.add("Selecciona su curso");
        gradeListSize = gradeList.size() - 1;
    }

    private void addDiseasesToList() {
        diseasesList.add("Síndrome de Rett");
        diseasesList.add("Síndrome de Kanner");
        diseasesList.add("Síndrome de Asperger");
        diseasesList.add("Síndrome de Heller");
        diseasesList.add("Trastorno generalizado del desarrollo");
        diseasesList.add("Selecciona su transtorno");
        diseasesListSize = diseasesList.size() - 1;
    }

    private String generateId(int range) {
            String SALTCHARS = "abcdefghijklmnopqrstuvwxyz1234567890";
            StringBuilder salt = new StringBuilder();
            Random rnd = new Random();
            while (salt.length() < range) { // length of the random string.
                int index = (int) (rnd.nextFloat() * SALTCHARS.length());
                salt.append(SALTCHARS.charAt(index));
            }
            String saltStr = salt.toString();
            return saltStr;
    }

    private String getSchoolId(String selectedSchool) {
        String found = "";
        for (Map.Entry<String, String> entry : schoolsByUi.entrySet()) {
            if(selectedSchool.equals(entry.getValue())){
                found = entry.getKey();
            }
        }
        return found;
    }

    private void addSchoolToList() {
        for (String schoolname : schoolsByUi.values()) {
            schoolList.add(schoolname);
        }
        schoolList.add("Selecciona su colegio");
        schoolsListSize = schoolList.size() - 1;
        addAdapterToList(schoolList, schoolsListSize, txtSchools);
    }

    private void goToLogin() {
        IntentActivity intent = new IntentActivity(Registration.this,
                Login.class);
        intent.startActivity();
        intent.finishActivity();
    }
}