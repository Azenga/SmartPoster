package com.shadow.smartposter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Sign In");

        //Redirecting to signup
        TextView gotoSignUpTV = findViewById(R.id.goto_signup_tv);
        gotoSignUpTV.setOnClickListener(view -> startActivity(new Intent(this, SignUpActivity.class)));

        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, PublicDashboardActivity.class);
            startActivity(intent);
        });
    }
}
