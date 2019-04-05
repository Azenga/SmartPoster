package com.shadow.smartposter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Sign Up");

        //Redirecting to login
        TextView gotoLoginTV = findViewById(R.id.goto_login_tv);
        gotoLoginTV.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));

    }
}
