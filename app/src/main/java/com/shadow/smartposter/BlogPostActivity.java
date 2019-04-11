package com.shadow.smartposter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class BlogPostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView blogPostRV;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_aspirants:
                    getSupportActionBar().setTitle("Aspirants Posts");
                    return true;
                case R.id.nav_all_posts:
                    getSupportActionBar().setTitle("All Posts");
                    return true;
                case R.id.nav_favourites:
                    getSupportActionBar().setTitle("Favourites Posts");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Posts");

        blogPostRV = findViewById(R.id.blog_post_rv);
        blogPostRV.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton gotoBlogPostFAB = findViewById(R.id.goto_add_post_fab);
        gotoBlogPostFAB.setOnClickListener(view -> startActivity(new Intent(this, AddPostActivity.class)));
    }


}
