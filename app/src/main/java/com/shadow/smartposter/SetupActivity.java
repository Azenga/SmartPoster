package com.shadow.smartposter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.smartposter.models.User;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    //Constants
    private static final String TAG = "SetupActivity";
    public static final int RC_EXTERNAL_STORAGE = 22;
    public static final int RC_CHOOSE_IMAGE = 10;
    public static final int RC_CROP_IMAGE = 19;

    //Widgets
    private EditText nameET, nicknameET, contactET, countyET, constituenctET, wardET, websiteET;
    private Button changeImageBtn, finishSetupBtn;
    private CircleImageView profilePicCIV;
    private ProgressDialog progressDialog;

    //Flags
    private boolean imageSelected = false;

    //Firebase Variables
    private FirebaseAuth mAth;
    private StorageReference mRef;
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAth = FirebaseAuth.getInstance();
        mRef = FirebaseStorage.getInstance().getReference().child("avatars");
        mDb = FirebaseFirestore.getInstance();

        initWidgets();

        changeImageBtn.setOnClickListener(view -> changeProfileImage());
        finishSetupBtn.setOnClickListener(view -> doUploads());
    }

    private void initWidgets() {

        //EditTexts
        nameET = findViewById(R.id.name_et);
        nicknameET = findViewById(R.id.nickname_et);
        contactET = findViewById(R.id.contact_et);
        countyET = findViewById(R.id.county_et);
        constituenctET = findViewById(R.id.constituency_et);
        wardET = findViewById(R.id.ward_et);
        websiteET = findViewById(R.id.website_et);

        //Buttons
        changeImageBtn = findViewById(R.id.change_img_btn);
        finishSetupBtn = findViewById(R.id.finish_setup_btn);

        //CircleImageView
        profilePicCIV = findViewById(R.id.profile_image_civ);

        //ProgressDialog
        progressDialog = new ProgressDialog(this);
    }

    private void uploadToFirestore(String imageName) {

        String name = nameET.getText().toString().trim();
        String nickname = nicknameET.getText().toString().trim();
        String contact = contactET.getText().toString().trim();
        String county = countyET.getText().toString().trim();
        String constituency = constituenctET.getText().toString().trim();
        String ward = wardET.getText().toString();
        String website = websiteET.getText().toString().trim();

        User user = new User(name, contact, county, constituency, ward, "voter");
        user.setNickName(nickname);
        user.setImageName(imageName);
        user.setWebsite(website);

        //Adding to Firestore
        if (mAth.getCurrentUser() != null) {
            mDb.collection("users")
                    .document(mAth.getCurrentUser().getUid())
                    .set(user)
                    .addOnSuccessListener(
                            aVoid -> {
                                Toast.makeText(this, "Account Setup was Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, PublicDashboardActivity.class));
                            }
                    )
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(this, "Sorry! Account Setup Failed, Try Again Later..,", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "uploadToFirestore: Failed", e);
                            }
                    );
        }


    }

    private void doUploads() {

        if (!hasEmptyRequiredFields()) {

            progressDialog.setTitle("Registering User");
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            //Uploading Image to FirebaseStorage
            if (imageSelected) {

                Bitmap bitmap = ((BitmapDrawable) profilePicCIV.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                byte[] bytes = baos.toByteArray();

                if (mAth.getCurrentUser() != null) {
                    String refName = mAth.getCurrentUser().getUid() + ".jpg";

                    StorageReference profilePicRef = mRef.child(refName);

                    profilePicRef.putBytes(bytes)
                            .addOnSuccessListener(
                                    taskSnapshot -> {
                                        Toast.makeText(this, "Profile Image Uploaded", Toast.LENGTH_SHORT).show();
                                        uploadToFirestore(taskSnapshot.getMetadata().getName());
                                    }
                            )
                            .addOnFailureListener(
                                    e -> {
                                        Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "doUploads: image upload failure", e);
                                        progressDialog.dismiss();
                                    }
                            );

                }
            } else {
                uploadToFirestore(null);
            }

        }

    }

    private boolean hasEmptyRequiredFields() {

        if (nameET.getText().toString().trim().isEmpty()) {
            nameET.setError("Name is Required");
            nameET.requestFocus();
            return true;
        } else if (contactET.getText().toString().trim().isEmpty()) {
            contactET.setError("Contact is Required");
            contactET.requestFocus();
            return true;
        } else if (countyET.getText().toString().trim().isEmpty()) {
            countyET.setError("County Name is Required");
            countyET.requestFocus();
            return true;
        } else if (constituenctET.getText().toString().trim().isEmpty()) {
            constituenctET.setError("Constituency Name is Required");
            constituenctET.requestFocus();
            return true;
        } else if (wardET.getText().toString().trim().isEmpty()) {
            wardET.setError("Ward Name is Required");
            wardET.requestFocus();
            return true;
        } else {
            return false;
        }

    }

    private void changeProfileImage() {

        if (storagePermissions()) {
            openImageChoosingActivity();
        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_EXTERNAL_STORAGE);
        }

    }

    private boolean storagePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        } else {
            return true;
        }
    }

    private void openImageChoosingActivity() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RC_CHOOSE_IMAGE);

    }

    public void openCroppingIntent(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("outputX", 512);
        intent.putExtra("outputY", 512);
        intent.putExtra("aspectX", 512);
        intent.putExtra("aspectY", 512);
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
        profilePicCIV.setImageBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {

                case RC_EXTERNAL_STORAGE:
                    openImageChoosingActivity();
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            }
        } else {

            Toast.makeText(this, "Requested Permission Denied, Sorry..?!..?", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {

            switch (requestCode) {

                case RC_CHOOSE_IMAGE:
                    imageSelected = true;
                    Uri imageUri = data.getData();
                    profilePicCIV.setImageURI(imageUri);
                    Snackbar.make(findViewById(R.id.activity_setup), "Crop the Image?", Snackbar.LENGTH_LONG)
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
}
