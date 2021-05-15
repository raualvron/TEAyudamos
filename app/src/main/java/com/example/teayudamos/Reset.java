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

import com.example.teayudamos.services.IntentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Reset extends AppCompatActivity {
    Button btnReset;
    FirebaseAuth auth;
    EditText txtUsername;
    boolean valid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reset);

        auth = FirebaseAuth.getInstance();

        txtUsername = findViewById(R.id.et_email);

        btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void goToHome() {
        IntentActivity intent = new IntentActivity(Reset.this,
                Home.class);
        intent.startActivity();
    }

    private void validate() {
        String username = txtUsername.getText().toString();

        Drawable image = getApplicationContext().getResources().getDrawable(R.drawable.ic_validation);
        image.setBounds(0, 0, 60, 60);

        if (username.isEmpty()) {
            txtUsername.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.et_validation));
            txtUsername.setCompoundDrawables(image, null, null, null);

            valid = false;
        } else {
            txtUsername.setText("");
            auth.sendPasswordResetEmail(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(
                                Reset.this,
                                "Se ha enviado un email a su correo electronico para resetear su contraseña",
                                Toast.LENGTH_LONG).show();
                        goToHome();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(
                            Reset.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    
                }
            });
        }
    }
}