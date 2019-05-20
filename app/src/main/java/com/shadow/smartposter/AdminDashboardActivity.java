package com.shadow.smartposter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.smartposter.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "AdminDashboardActivity";

    //Firebase Variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private StorageReference mRef;

    //Header LayoutWidgets
    private CircleImageView profilePicCIV;
    private TextView usernameTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference().child("avatars");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Profile");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeaderView = navigationView.getHeaderView(0);

        profilePicCIV = navigationHeaderView.findViewById(R.id.profile_image_civ);
        usernameTV = navigationHeaderView.findViewById(R.id.username_tv);


    }

    public void switchFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (fragment != null) {
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle blog_post_bnv view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_posts) {
            Intent intent = new Intent(this, BlogPostActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_aspirants) {

        } else if (id == R.id.nav_chats) {

        } else if (id == R.id.nav_update_profile) {
            startActivity(new Intent(this, SetupActivity.class));
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }

    private void updateUI(User user) {

        if (user.getNickName() != null) usernameTV.setText(user.getNickName());
        else usernameTV.setText(user.getName());

        if (user.getImageName() != null) {
            StorageReference profilePicRef = mRef.child(user.getImageName());

            final long MB = 1024 * 1024;

            profilePicRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profilePicCIV.setImageBitmap(bitmap);
                            }
                    )
                    .addOnFailureListener(e -> Log.e(TAG, "updateUI: Failed Loading Image", e));
        }

    }

    private void getUserDetails() {

        //Get Details of the current signed in user from firestore

        String uid = mAuth.getCurrentUser().getUid();

        mDb.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(
                        documentSnapshot -> {

                            if (documentSnapshot.exists()) {

                                User user = documentSnapshot.toObject(User.class);

                                if (user != null)
                                    updateUI(user);

                            } else {

                                Snackbar.make(findViewById(android.R.id.content), "No Account Info", Snackbar.LENGTH_LONG)
                                        .setAction("Setup Your Account", view -> startActivity(new Intent(this, SetupActivity.class)))
                                        .show();
                            }


                        }
                )
                .addOnFailureListener(e -> Log.e(TAG, "getUserDetails: Failed to Get User", e));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        } else {
            getUserDetails();
        }
    }

}
