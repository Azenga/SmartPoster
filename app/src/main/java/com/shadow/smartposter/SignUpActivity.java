package com.shadow.smartposter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    //Widgets
    private Button emailSignUpBtn;
    private EditText emailET, pwdET, confirmPwdET;
    private TextView gotoLoginTV;

    private ProgressDialog progressDialog;

    //Firebase Variables
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Sign Up");

        mAuth = FirebaseAuth.getInstance();

        initWidgets();

        //Redirecting to login
        gotoLoginTV.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));
        emailSignUpBtn.setOnClickListener(view -> signUpUser());


    }

    private void initWidgets() {

        gotoLoginTV = findViewById(R.id.goto_login_tv);

        emailET = findViewById(R.id.email_et);
        pwdET = findViewById(R.id.pwd_et);
        confirmPwdET = findViewById(R.id.confirm_pwd_et);

        emailSignUpBtn = findViewById(R.id.email_signup_btn);

        progressDialog = new ProgressDialog(this);


    }

    private boolean emptyFields() {

        if (emailET.getText().toString().trim().isEmpty()) {
            emailET.setError("Email is required");
            emailET.requestFocus();
            return true;
        } else if (pwdET.getText().toString().trim().isEmpty()) {
            pwdET.setError("Password is required");
            pwdET.requestFocus();
            return true;
        } else if (confirmPwdET.getText().toString().trim().isEmpty()) {
            confirmPwdET.setError("Password is required");
            confirmPwdET.requestFocus();
            return true;
        } else {
            return false;
        }

    }

    private void signUpUser() {

        String email = emailET.getText().toString().trim();
        String pwd = pwdET.getText().toString().trim();
        String confirmPwd = confirmPwdET.getText().toString().trim();

        if (!emptyFields()) {

            if (!pwd.equals(confirmPwd)) {
                confirmPwdET.setError("Passwords do not match");
                confirmPwdET.requestFocus();
                return;
            }

            progressDialog.setTitle("Signing Up");
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {

                                    finishSetup();

                                } else {
                                    Log.w(TAG, "signUpUser: Sign Up Failed", task.getException());
                                }

                                progressDialog.dismiss();
                            }
                    );


        }

    }

    private void finishSetup() {
        startActivity(new Intent(this, SetupActivity.class));
        finish();
    }

}
