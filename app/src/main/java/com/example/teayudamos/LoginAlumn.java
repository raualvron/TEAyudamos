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

import com.example.teayudamos.services.Constants;
import com.example.teayudamos.services.IntentActivity;
import com.example.teayudamos.services.SharePref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class LoginAlumn extends AppCompatActivity {

    Button btLogin;
    EditText txtUserId;
    boolean valid = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharePref sharePref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_alumn);
        getSupportActionBar().hide();
        sharePref = new SharePref(getBaseContext());

        btLogin = findViewById(R.id.btn_login);
        txtUserId = findViewById(R.id.et_user_id);

        btLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void goToFallDownActivity() {
        IntentActivity intent = new IntentActivity(LoginAlumn.this,
                FallDown.class);
        intent.startActivity();
        intent.finishActivity();
    }

    private void loadSharedPref(Map<String, Object> data) {
        String name = data.get("name").toString();
        String userId = data.get("userId").toString();
        String token = data.get("token").toString();

        sharePref.setSharedPrefBoolean(Constants.LOGGED, true);
        sharePref.setSharedPrefString(Constants.ALUMN, name);
        sharePref.setSharedPrefString(Constants.USER_ID, userId);
        sharePref.setSharedPrefString(Constants.TOKEN, token);

        goToFallDownActivity();
    }

    private void validate() {
        String userId = txtUserId.getText().toString();

        Drawable image = getApplicationContext().getResources().getDrawable(R.drawable.ic_validation);
        image.setBounds(0, 0, 60, 60);

        if (userId.isEmpty()) {
            txtUserId.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_validation));
            txtUserId.setCompoundDrawables(image, null, null, null);

            valid = false;
        } else {
            db.collection("alumns").whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    loadSharedPref(data);
                                }
                            }
                        }
                    });
        }
    }
}