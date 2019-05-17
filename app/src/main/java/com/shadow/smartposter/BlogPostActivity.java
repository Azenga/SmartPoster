package com.shadow.smartposter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shadow.smartposter.adapters.BlogPostAdapter;
import com.shadow.smartposter.models.Post;

import java.util.ArrayList;
import java.util.List;

public class BlogPostActivity extends AppCompatActivity {

    private static final String TAG = "BlogPostActivity";

    private List<Post> postList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.nav_aspirants:
                getSupportActionBar().setTitle("Aspirants Posts");
                populatePosts("aspirant");
                return true;
            case R.id.nav_all_posts:
                getSupportActionBar().setTitle("All Posts");
                populatePosts(null);
                return true;
            case R.id.nav_favourites:
                getSupportActionBar().setTitle("Favourites Posts");
                populatePosts("favourites");
                return true;
        }
        return false;
    };
    private BlogPostAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        postList = new ArrayList<>();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Posts");

        RecyclerView blogPostRV = findViewById(R.id.blog_post_rv);
        blogPostRV.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BlogPostAdapter(this, postList);
        blogPostRV.setAdapter(adapter);

        populatePosts(null);

        FloatingActionButton gotoBlogPostFAB = findViewById(R.id.goto_add_post_fab);
        gotoBlogPostFAB.setOnClickListener(view -> startActivity(new Intent(this, AddPostActivity.class)));
    }

    private void populatePosts(String filter) {

        // TODO: 4/14/19 Filter the Posts correctly
        Toast.makeText(this, filter, Toast.LENGTH_SHORT).show();

        postList.clear();
        adapter.notifyDataSetChanged();

        mDb.collection("posts")
                .addSnapshotListener(
                        (queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Log.e(TAG, "populatePosts: Error occurred", e);
                                return;
                            }

                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                    Post post = documentSnapshot.toObject(Post.class);
                                    post.setId(documentSnapshot.getId());
                                    postList.add(post);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                );
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
