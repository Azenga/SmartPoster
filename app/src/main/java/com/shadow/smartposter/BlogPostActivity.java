package com.shadow.smartposter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.shadow.smartposter.adapters.BlogPostAdapter;
import com.shadow.smartposter.models.Post;

import java.util.ArrayList;
import java.util.List;

public class BlogPostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView blogPostRV;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportActionBar().setTitle(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    getSupportActionBar().setTitle(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    getSupportActionBar().setTitle(R.string.title_notifications);
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
        BlogPostAdapter adapter = new BlogPostAdapter(this, getStaticData());
        blogPostRV.setAdapter(adapter);
    }

    public List<Post> getStaticData() {
        String[] onwerUsernames = getResources().getStringArray(R.array.usernames);
        int[] images = {R.mipmap.raila, R.mipmap.uhuru, R.mipmap.kalonzo, R.mipmap.obama, R.mipmap.musalia, R.mipmap.isaac, R.mipmap.ruto, R.mipmap.mutula, R.mipmap.sakaja, R.mipmap.joho};
        int[] likes = getResources().getIntArray(R.array.likes);
        String[] messages = getResources().getStringArray(R.array.messages);

        List<Post> returnList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            returnList.add(new Post(null, onwerUsernames[i], images[i], null, likes[i], messages[i]));
        }

        return returnList;
    }

}
