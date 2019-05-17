package com.shadow.smartposter.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.smartposter.R;
import com.shadow.smartposter.models.Post;
import com.shadow.smartposter.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogPostAdapter extends RecyclerView.Adapter<BlogPostAdapter.ViewHolder> {

    private static final String TAG = "BlogPostAdapter";

    private Context context;
    private List<Post> postList;

    private StorageReference mRef;
    private FirebaseFirestore mDb;

    public BlogPostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.postList = posts;

        mRef = FirebaseStorage.getInstance().getReference();
        mDb = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_single_photo_post_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final long MB = 1024 * 1024;
        Post post = postList.get(i);

        String ownerUid = post.getOwnerId();

        if (ownerUid != null) {
            //Setting Image
            String refName = ownerUid + ".jpg";
            StorageReference ownerAvatarRef = mRef.child("avatars").child(refName);

            ownerAvatarRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                viewHolder.ownerAvatarCIV.setImageBitmap(bitmap);
                            }
                    )
                    .addOnFailureListener(
                            e -> Log.e(TAG, "onBindViewHolder: Getting Owner Avatar", e)
                    );

            //Setting Owner Username
            mDb.collection("users")
                    .document(ownerUid)
                    .get()
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {

                                    DocumentSnapshot snapshot = task.getResult();

                                    if(snapshot.exists()){
                                        User user = snapshot.toObject(User.class);

                                        viewHolder.ownerUsernameTV.setText(user.getName());
                                    }

                                } else {
                                    Log.e(TAG, "onBindViewHolder: Getting Username", task.getException());
                                }
                            }
                    );

        }
        //Setting Image Post
        if (post.getImageName() != null) {
            StorageReference postImageRef = mRef.child("post_images").child(post.getImageName());

            postImageRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                viewHolder.postImageIV.setImageBitmap(bitmap);
                            }
                    )
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(context, "Getting Post Image Failed", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onBindViewHolder: Getting Post Image", e);
                            }
                    );
        }


        viewHolder.postTV.setText(post.getCaption());

        if(post.getLikes() != null) {
            viewHolder.likesTV.setText(post.getLikes().length + " likes");
        }else{
            viewHolder.likesTV.setText("0 likes");

        }

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ownerAvatarCIV;
        TextView ownerUsernameTV;
        ImageButton moreVertIB, shareIB, likeIB, commentIB;
        ImageView postImageIV;
        TextView postTV, likesTV, readMoreTV, viewCommentsTV;

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ownerAvatarCIV = itemView.findViewById(R.id.post_owner_avatar_civ);
            ownerUsernameTV = itemView.findViewById(R.id.owner_username_tv);
            moreVertIB = itemView.findViewById(R.id.more_vert_ib);
            postImageIV = itemView.findViewById(R.id.post_image_iv);
            shareIB = itemView.findViewById(R.id.share_ib);
            likesTV = itemView.findViewById(R.id.likes_tv);
            likeIB = itemView.findViewById(R.id.like_ib);
            commentIB = itemView.findViewById(R.id.comment_ib);
            postTV = itemView.findViewById(R.id.post_tv);
            readMoreTV = itemView.findViewById(R.id.read_more_tv);
            viewCommentsTV = itemView.findViewById(R.id.view_comments_tv);

            view = itemView;
        }
    }
}
