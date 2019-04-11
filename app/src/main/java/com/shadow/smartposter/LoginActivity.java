package com.shadow.smartposter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 22;
    private static final String TAG = "LoginActivity";


    //Widgets registration
    private EditText emailET, pwdET;
    private ProgressDialog progressDialog;

    public GoogleSignInClient mGoogleSignInClient;

    //Firebase Variables
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Sign In");

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Redirecting to signup
        TextView gotoSignUpTV = findViewById(R.id.goto_signup_tv);
        gotoSignUpTV.setOnClickListener(view -> startActivity(new Intent(this, SignUpActivity.class)));

        initComponents();


        Button loginBtn = findViewById(R.id.email_signup_btn);
        loginBtn.setOnClickListener(view -> loginUser());

        Button googleAuthBtn = findViewById(R.id.google_auth_btn);
        googleAuthBtn.setOnClickListener(view -> googleSignIn());

    }

    private void initComponents() {

        //EditTexts
        emailET = findViewById(R.id.email_et);
        pwdET = findViewById(R.id.pwd_et);

        //ProgressDialog
        progressDialog = new ProgressDialog(this);


    }

    private void loginUser() {
        if (!emptyCredentials()) {

            String email = emailET.getText().toString().trim();
            String pwd = pwdET.getText().toString().trim();

            progressDialog.setTitle("Attempting Login");
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    sendToDashboard();
                                } else {
                                    Toast.makeText(this, "Login error: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                                progressDialog.dismiss();
                            }

                    );

        }

    }

    private void sendToDashboard() {

        Intent intent = new Intent(this, PublicDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private boolean emptyCredentials() {

        if (emailET.getText().toString().trim().isEmpty()) {
            emailET.setError("Empty Email");
            emailET.requestFocus();
            return true;
        } else if (pwdET.getText().toString().trim().isEmpty()) {

            pwdET.setError("No Password");
            pwdET.requestFocus();
            return true;
        }

        return false;

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            sendToDashboard();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                Log.e(TAG, "onActivityResult: Google SignIn Failed", e);
            }
        } else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);


        progressDialog.setTitle("Attempting Login");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                sendToDashboard();
                            } else {
                                Log.w(TAG, "firebaseAuthWithGoogle: failure", task.getException());
                                Snackbar.make(findViewById(R.id.activity_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            }

                            progressDialog.dismiss();
                        }
                );
    }
}
