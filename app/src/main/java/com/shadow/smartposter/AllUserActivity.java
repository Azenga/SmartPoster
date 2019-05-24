package com.shadow.smartposter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.smartposter.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity {

    private static final String TAG = "AllUserActivity";

    private RecyclerView usersRV;

    private FirestoreRecyclerAdapter<User, UserHolder> adapter;
    private FirebaseFirestore mDb;
    private StorageReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Users");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDb = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference("avatars");

        usersRV = findViewById(R.id.users_rv);
        usersRV.setHasFixedSize(true);
        usersRV.setLayoutManager(new LinearLayoutManager(this));

        readUsers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void readUsers() {
        Query query = mDb.collection("users").limit(20);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<User, UserHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {

                holder.nameTV.setText(model.getName());
                if (model.getImageName() != null) {

                    StorageReference profilePicRef = mRef.child(model.getImageName());
                    final long MB = 1024 * 1024;
                    profilePicRef.getBytes(MB)
                            .addOnSuccessListener(
                                    bytes -> {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        holder.profileImageCIV.setImageBitmap(bitmap);
                                    }
                            )
                            .addOnFailureListener(e -> Log.e(TAG, "onBindViewHolder: ", e));
                }

                holder.mView.setOnClickListener(view -> {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                    model.setUid(snapshot.getId());

                    Intent messageIntent = new Intent(AllUserActivity.this, MessageActivity.class);
                    messageIntent.putExtra(MessageActivity.USER_UID_PARAM, model.getUid());
                    startActivity(messageIntent);

                });

            }

            @NonNull
            @Override
            public UserHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_user_view, viewGroup, false);
                return new UserHolder(view);

            }
        };

        usersRV.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
    }

    public static class UserHolder extends RecyclerView.ViewHolder {

        View mView;
        CircleImageView profileImageCIV;
        TextView nameTV;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            profileImageCIV = itemView.findViewById(R.id.profile_image_civ);
            nameTV = itemView.findViewById(R.id.name_tv);
        }
    }
}
