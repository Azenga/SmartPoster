package com.shadow.smartposter.fragments.publics;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.smartposter.PublicDashboardActivity;
import com.shadow.smartposter.R;
import com.shadow.smartposter.SetupActivity;
import com.shadow.smartposter.models.User;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    //Widgets
    private ImageView profilePicIV;
    private TextView nameTV, countyTV, constituencyTV, wardTV, contactTV, websiteTV;
    private Button editProfileBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private StorageReference mRef;

    public ProfileFragment() {
        // Required empty public constructor
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference().child("avatars");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameTV = view.findViewById(R.id.name_tv);
        countyTV = view.findViewById(R.id.county_tv);
        constituencyTV = view.findViewById(R.id.constituency_tv);
        wardTV = view.findViewById(R.id.ward_tv);
        contactTV = view.findViewById(R.id.contact_tv);
        websiteTV = view.findViewById(R.id.website_tv);
        profilePicIV = view.findViewById(R.id.profile_pic_iv);
        editProfileBtn = view.findViewById(R.id.edit_profile_btn);


        return view;
    }

    private void updateUI(User user) {

        nameTV.setText(user.getName());
        countyTV.setText(user.getCounty());
        constituencyTV.setText(user.getConstituency());
        wardTV.setText(user.getWard());
        contactTV.setText(user.getContact());
        websiteTV.setText(user.getWebsite());

        if (user.getImageName() != null) {
            StorageReference profilePicRef = mRef.child(user.getImageName());

            final long MB = 1024 * 1024;
            profilePicRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profilePicIV.setImageBitmap(bitmap);
                            }
                    )
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(getActivity(), "Failed to load the Image", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "updateUI: Failed to LOad Profile Pic", e);
                            }
                    );
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editProfileBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), SetupActivity.class)));
        if (mAuth.getCurrentUser() != null) {
            mDb.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();

                                    if (doc.exists()) {
                                        User user = doc.toObject(User.class);
                                        if (user != null) updateUI(user);
                                    }
                                } else
                                    Log.e(TAG, "onViewCreated: Error Getting User details", task.getException());
                            }
                    );
        }

    }

}
