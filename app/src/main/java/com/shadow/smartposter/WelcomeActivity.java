package com.shadow.smartposter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.shadow.smartposter.fragments.welcome.WelcomeBottomFragment;
import com.shadow.smartposter.fragments.welcome.WelcomeTopFragment;

public class WelcomeActivity extends AppCompatActivity
        implements GestureDetector.OnGestureListener {

    public static final int SWIPE_THRESHHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHHOLD = 100;
    private GestureDetectorCompat mCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //To Initialize the firebase
        FirebaseApp.initializeApp(this);

        //Getting and setting the toolbar with the title
        Toolbar toolbar = findViewById(R.id.welcome_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Smart Poster");

        mCompat = new GestureDetectorCompat(this, this);
    }

    public void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        float dY = moveEvent.getY() - downEvent.getY();
        float dX = moveEvent.getX() - downEvent.getY();

        if (Math.abs(dX) > Math.abs(dY)) {
            Toast.makeText(this, "Swipe Left/Right", Toast.LENGTH_SHORT).show();
        } else {
            if (Math.abs(dY) > SWIPE_THRESHHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHHOLD) {
                if (dY < 0) {
                    switchFragment(new WelcomeBottomFragment());
                } else {
                    switchFragment(new WelcomeTopFragment());
                }
            }
        }

        return true;
    }

    // Subscribing on touch Event
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        this.mCompat.onTouchEvent(event);

        return true;
    }
}
