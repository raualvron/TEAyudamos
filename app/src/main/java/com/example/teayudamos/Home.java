package com.example.teayudamos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.teayudamos.services.Constants;
import com.example.teayudamos.services.IntentActivity;
import com.example.teayudamos.services.SharePref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;

import java.util.HashMap;

public class Home extends AppCompatActivity {

    Button btnRegister, btnLogin, btnLoginAlumn;
    TextView resetPassword;
    SharePref sharePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        sharePref = new SharePref(getBaseContext());
        isTheUserLogged();

        setContentView(R.layout.activity_home);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnLoginAlumn = findViewById(R.id.btn_login_alumn);

        resetPassword = findViewById(R.id.forgotpassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivity(Login.class, false);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivity(Registration.class, false);
            }
        });

        btnLoginAlumn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivity(LoginAlumn.class, false);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivity(Reset.class, false);
            }
        });


    }


    private void goToActivity(Class<?> activityClass, boolean finish) {
        IntentActivity intent = new IntentActivity(Home.this,
                activityClass);
        intent.startActivity();

        if (finish) {
            intent.finishActivity();
            finish();
        }

    }

    private void isTheUserLogged() {
        boolean logged = sharePref.getSharedPrefBoolean(Constants.LOGGED);
        String token = sharePref.getSharedPrefString(Constants.TOKEN);
        if (logged) {
            if (!token.isEmpty()) {
                goToActivity(FallDown.class, true);
            } else {
                goToActivity(Dashboard.class, true);
            }

        }
    }
}