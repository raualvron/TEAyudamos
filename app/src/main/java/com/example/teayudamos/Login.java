package com.example.teayudamos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.teayudamos.model.Location;
import com.example.teayudamos.services.Constants;
import com.example.teayudamos.services.IntentActivity;
import com.example.teayudamos.services.SharePref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class Login extends AppCompatActivity {
    Button btLogin;
    EditText txtUsername, txtPassword;
    boolean valid = false;
    FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharePref sharePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        sharePref = new SharePref(getBaseContext());

        btLogin = findViewById(R.id.btn_login);
        txtUsername = findViewById(R.id.et_email);
        txtPassword = findViewById(R.id.et_password);

        auth = FirebaseAuth.getInstance();

        btLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login.this.validate();
            }
        });
    }

    private void validate() {
        String password = txtPassword.getText().toString();
        String username = txtUsername.getText().toString();

        Drawable image = getApplicationContext().getResources().getDrawable(R.drawable.ic_validation);
        image.setBounds(0, 0, 60, 60);

        if (username.isEmpty() || password.isEmpty()) {
            txtUsername.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_validation));
            txtUsername.setCompoundDrawables(image, null, null, null);

            txtPassword.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_validation));
            txtPassword.setCompoundDrawables(image, null, null, null);
            valid = false;
        } else {
            auth.signInWithEmailAndPassword(username, password)  .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if(user.isEmailVerified()) {
                            Login.this.getAlumnByUid(user.getUid());
                        } else {
                            user.sendEmailVerification();
                            Toast.makeText(Login.this, "Su cuenta no esta verificada, recibe su bandeja de correo", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(Login.this, "Usuario no encontrado, pruebe de nuevo por favor", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void goToDashboard() {
        IntentActivity intent = new IntentActivity(Login.this,
                Dashboard.class);
        intent.startActivity();
        intent.finishActivity();
    }

    private void loadSharedPref(Map<String, Object> data, String documentId) {
        String name = data.get("name").toString();
        String userId = data.get("userId").toString();

        sharePref.setSharedPrefBoolean(Constants.LOGGED, true);
        sharePref.setSharedPrefString(Constants.ALUMN, name);
        sharePref.setSharedPrefString(Constants.USER_ID, userId);
        sharePref.setSharedPrefString(Constants.DOCUMENT_ID, documentId);
        sharePref.setSharedPrefString("email", txtUsername.getText().toString());

        Login.this.goToDashboard();
    }

    private void getAlumnByUid(String uid) {
        db.collection("alumns").whereEqualTo("userId", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                Login.this.loadSharedPref(data, document.getId());
                            }
                        }
                    }
                });
    }
}