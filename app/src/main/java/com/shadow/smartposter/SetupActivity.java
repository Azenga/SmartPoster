package com.shadow.smartposter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    public static final int RC_EXTERNAL_STORAGE = 22;
    public static final int RC_CHOOSE_IMAGE = 10;
    public static final int RC_CROP_IMAGE = 19;
    //Widgets
    private EditText nameET, nickNameET, contactET, countyET, constituenctET, wardET, websiteET;
    private Button changeImageBtn, finishSetupBtn;
    private CircleImageView profilePicCIV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        initWidgets();

        changeImageBtn.setOnClickListener(view -> changeProfileImage());
    }

    private void initWidgets() {

        //EditTexts
        nameET = findViewById(R.id.name_et);
        nickNameET = findViewById(R.id.nick_name_et);
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
                    Uri imageUri = data.getData();
                    profilePicCIV.setImageURI(imageUri);
                    Snackbar.make(findViewById(R.id.activity_setup), "Crop the Image?", Snackbar.LENGTH_LONG)
                            .setAction("Crop the Image", (View v) -> openCroppingIntent(data.getData()))
                            .show();
                default:
                    super.onActivityResult(requestCode, resultCode, data);

            }
        }

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
}
