package com.shadow.smartposter.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.smartposter.R;
import com.shadow.smartposter.models.Chat;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatThreadAdapter extends RecyclerView.Adapter<ChatThreadAdapter.ChatThreadHolder> {
    private static final String TAG = "ChatThreadAdapter";

    private List<String> mUserUids;

    private StorageReference mRef;
    private FirebaseFirestore mDb;

    public ChatThreadAdapter(List<String> mUserUids) {
        this.mUserUids = mUserUids;

        mRef = FirebaseStorage.getInstance().getReference("avatars");
        mDb = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ChatThreadHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_thread_view, viewGroup, false);
        return new ChatThreadHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatThreadHolder chatThreadHolder, int position) {

        String imageName = mUserUids.get(position) + ".jpg";
        StorageReference profileImageRef = mRef.child(imageName);
        final long MB = 1024 * 1024;
        profileImageRef.getBytes(MB)
                .addOnSuccessListener(
                        bytes -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            chatThreadHolder.profileImageCIV.setImageBitmap(bitmap);
                        }
                )
                .addOnFailureListener(e -> Log.e(TAG, "onBindViewHolder: ", e));

        chatThreadHolder.lastMessageTV.setText(getLastMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(), mUserUids.get(position)));
    }

    private String getLastMessage(String currentUserUid, String mOtherUserUid) {

        List<Chat> chats = new ArrayList<>();

        mDb.collection("chats")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(
                        (queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Log.e(TAG, "getLastMessage: ", e);
                                return;
                            }

                            if (!queryDocumentSnapshots.isEmpty()) {

                                chats.clear();

                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    Chat chat = snapshot.toObject(Chat.class);

                                    if (chat.getSender().equalsIgnoreCase(currentUserUid) && chat.getReceiver().equalsIgnoreCase(mOtherUserUid)) {
                                        chats.add(chat);
                                    }

                                    if (chat.getReceiver().equalsIgnoreCase(currentUserUid) && chat.getSender().equalsIgnoreCase(mOtherUserUid)) {
                                        chats.add(chat);
                                    }

                                }
                            }
                        }
                );

        return chats.get(0).getContent();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ChatThreadHolder extends BlogPostAdapter.ViewHolder {
        View mView;
        CircleImageView profileImageCIV;
        TextView lastMessageTV;

        public ChatThreadHolder(@NonNull View itemView) {

            super(itemView);

            mView = itemView;

            profileImageCIV = itemView.findViewById(R.id.profile_image_civ);
            lastMessageTV = itemView.findViewById(R.id.last_msg_tv);
        }
    }

}
