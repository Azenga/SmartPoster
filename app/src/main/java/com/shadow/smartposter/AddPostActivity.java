package com.shadow.smartposter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AddPostActivity extends AppCompatActivity {

    private EditText postCaptionET;
    private ImageButton gobackToPostIB;
    private ImageView postImageIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        initComponents();

        gobackToPostIB.setOnClickListener(view -> startActivity(new Intent(this, BlogPostActivity.class)));
    }

    private void initComponents() {

        postCaptionET = findViewById(R.id.caption_et);

        gobackToPostIB = findViewById(R.id.goback_to_posts_ib);

        postImageIV = findViewById(R.id.post_image_iv);
    }
}
