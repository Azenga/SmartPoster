package com.shadow.smartposter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.smartposter.models.Post;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";
    public static final int RC_CHOOSE_IMAGE = 10;
    public static final int RC_CROP_IMAGE = 19;

    private EditText postCaptionET;
    private ImageButton gobackToPostIB;
    private ImageView postImageIV;
    private Button addPostBtn;

    private ProgressDialog progressDialog;

    private boolean imageSelected = false;


    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private StorageReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Add Post");

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference().child("post_images");

        initComponents();

        gobackToPostIB.setOnClickListener(view -> startActivity(new Intent(this, BlogPostActivity.class)));
        postImageIV.setOnClickListener(view -> openChooseImageActivity());
        addPostBtn.setOnClickListener(view -> uploadPost());
    }

    private void initComponents() {

        postCaptionET = findViewById(R.id.post_caption_et);

        gobackToPostIB = findViewById(R.id.goback_to_posts_ib);

        postImageIV = findViewById(R.id.post_image_iv);

        addPostBtn = findViewById(R.id.add_post_btn);

        progressDialog = new ProgressDialog(this);
    }

    private void uploadPost() {
        if (postCaptionET.getText().toString().trim().isEmpty()) {
            postCaptionET.setError("Need Some Caption");
            postCaptionET.requestFocus();
            return;
        } else if (!imageSelected) {
            Toast.makeText(this, "Select an Image to Post", Toast.LENGTH_SHORT).show();
            return;
        }

        String caption = postCaptionET.getText().toString().trim();

        progressDialog.setTitle("Uploading Post");
        progressDialog.setTitle("Just a Moment...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        //Upload the Image to Storage
        StorageReference postImageRef = mRef.child(System.currentTimeMillis() + ".jpg");

        Bitmap bitmap = ((BitmapDrawable) postImageIV.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        postImageRef.putBytes(bytes)
                .addOnSuccessListener(
                        taskSnapshot -> {
                            String postImageName = taskSnapshot.getMetadata().getName();
                            String ownerId = mAuth.getCurrentUser().getUid();
                            Post post = new Post(ownerId, postImageName, null, caption, new Timestamp(new Date()));
                            takePostToFirestore(post);
                        }
                )
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Uploading Image Failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "uploadPost: Image Upload Failed", e);
                });


    }

    private void takePostToFirestore(Post post) {
        mDb.collection("posts")
                .add(post)
                .addOnCompleteListener(
                        task -> {

                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Post Added", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, BlogPostActivity.class));
                                finish();

                            } else {
                                Toast.makeText(this, "Post failed, Please try again later", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "takePostToFirestore: Failed", task.getException());
                            }

                        }
                );
    }

    private void openChooseImageActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RC_CHOOSE_IMAGE);
    }

    public void openCroppingIntent(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("outputX", 384);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 2);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        PackageManager packageManager = getPackageManager();

        List<ResolveInfo> croppingApps = packageManager.queryIntentActivities(intent, 0);

        if (croppingApps.size() > 0) startActivityForResult(intent, RC_CROP_IMAGE);
        else
            Toast.makeText(this, "Please Install a cropping Activity to Use this", Toast.LENGTH_SHORT).show();

    }

    private void populateProfileIV(Intent data) {
        Bundle bundle = data.getExtras();
        Bitmap bitmap = bundle.getParcelable("data");
        postImageIV.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {

            switch (requestCode) {

                case RC_CHOOSE_IMAGE:
                    imageSelected = true;
                    Uri imageUri = data.getData();
                    postImageIV.setImageURI(imageUri);
                    Snackbar.make(findViewById(android.R.id.content), "Crop the Image?", Snackbar.LENGTH_LONG)
                            .setAction("Crop the Image", (View v) -> {
                                imageSelected = false;
                                openCroppingIntent(data.getData());
                            })
                            .show();
                    break;

                case RC_CROP_IMAGE:
                    imageSelected = true;
                    populateProfileIV(data);
                    break;

                default:
                    super.onActivityResult(requestCode, resultCode, data);

            }
        }

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
