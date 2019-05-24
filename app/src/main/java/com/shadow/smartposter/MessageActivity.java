package com.shadow.smartposter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.smartposter.adapters.ChatsAdapter;
import com.shadow.smartposter.models.Chat;
import com.shadow.smartposter.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MessageActivity";
    public static final String USER_UID_PARAM = "user-uid-param";

    private Toolbar toolbar;
    private TextView usernameTV;
    private CircleImageView profilePicCIV;
    private EditText msgET;
    private ImageButton sendMsgBtn;
    private RecyclerView chatsRV;

    private String mOtherUserUid = null;
    private List<Chat> mChats;


    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private StorageReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initComponents();

        if (getIntent() != null) mOtherUserUid = getIntent().getStringExtra(USER_UID_PARAM);

        if (getSupportActionBar() == null) setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getUserDetails(mOtherUserUid);

        sendMsgBtn.setOnClickListener(view -> sendMessage());
    }

    private void sendMessage() {
        String senderUid = mAuth.getCurrentUser().getUid();
        String content = String.valueOf(msgET.getText());

        if (TextUtils.isEmpty(content)) {
            msgET.setError("Type in a Message");
            msgET.requestFocus();
            return;
        }

        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("sender", senderUid);
        msgMap.put("receiver", mOtherUserUid);
        msgMap.put("content", content);
        msgMap.put("timestamp", FieldValue.serverTimestamp());

        mDb.collection("chats")
                .add(msgMap)
                .addOnSuccessListener(
                        documentReference -> {
                            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
                            msgET.setText("");
                        }
                )
                .addOnFailureListener(e -> Log.e(TAG, "sendMessage: ", e));

    }

    private void initComponents() {

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference("avatars");
        mChats = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar);
        usernameTV = findViewById(R.id.name_tv);
        profilePicCIV = findViewById(R.id.profile_image_civ);
        chatsRV = findViewById(R.id.chats_rv);
        chatsRV.setLayoutManager(new LinearLayoutManager(this));
        chatsRV.setHasFixedSize(true);

        sendMsgBtn = findViewById(R.id.send_msg_btn);
        msgET = findViewById(R.id.msg_et);

    }

    private void readMessages() {
        String currentUserUid = mAuth.getCurrentUser().getUid();
        mDb.collection("chats")
                .addSnapshotListener(
                        (queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Log.e(TAG, "readMessages: ", e);
                                return;
                            }

                            if (!queryDocumentSnapshots.isEmpty()) {

                                mChats.clear();

                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    Chat chat = snapshot.toObject(Chat.class);

                                    if (chat.getSender().equalsIgnoreCase(currentUserUid) && chat.getReceiver().equalsIgnoreCase(mOtherUserUid)) {
                                        mChats.add(chat);
                                    }

                                    if (chat.getReceiver().equalsIgnoreCase(currentUserUid) && chat.getSender().equalsIgnoreCase(mOtherUserUid)) {
                                        mChats.add(chat);
                                    }

                                }

                                ChatsAdapter adapter = new ChatsAdapter(mChats);
                                chatsRV.setAdapter(adapter);
                            }
                        }
                );
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        readMessages();

    }

    private void updateUI(User user) {

        usernameTV.setText(user.getName());

        if (user.getImageName() != null) {

            StorageReference profPicRef = mRef.child(user.getImageName());

            final long MB = 1024 * 1024;

            profPicRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profilePicCIV.setImageBitmap(bitmap);
                            }
                    )
                    .addOnFailureListener(e -> Log.e(TAG, "updateUI: ", e));

        }

    }

    private void getUserDetails(String uid) {

        mDb.document("users/" + uid)
                .get()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {

                                User user = task.getResult().toObject(User.class);

                                if (user != null) updateUI(user);

                            } else {
                                Log.e(TAG, "getUserDetails: ", task.getException());

                            }
                        }
                );

    }
}
