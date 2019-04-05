package com.shadow.smartposter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shadow.smartposter.R;
import com.shadow.smartposter.models.Post;

import java.util.List;

public class BlogPostAdapter extends RecyclerView.Adapter<BlogPostAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public BlogPostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_single_photo_post_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Post post = posts.get(i);

        viewHolder.ownerUsernameTV.setText(post.getOnwerUsername());
        viewHolder.postImageIV.setImageResource(post.getImage());
        viewHolder.postTV.setText(post.getMessage());
        viewHolder.likesTV.setText(post.getLikes() + " likes");

        viewHolder.view.setOnClickListener(view -> Toast.makeText(context, post.getOnwerUsername() + " post clicked", Toast.LENGTH_SHORT).show());

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ownerUsernameTV;
        public ImageView postImageIV;
        public TextView postTV;
        public TextView likesTV;

        public View view;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ownerUsernameTV = itemView.findViewById(R.id.owner_username_tv);
            postImageIV = itemView.findViewById(R.id.post_image_iv);
            postTV = itemView.findViewById(R.id.post_tv);
            likesTV = itemView.findViewById(R.id.likes_tv);

            view = itemView;
        }
    }
}
