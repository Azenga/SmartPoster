package com.shadow.smartposter.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.shadow.smartposter.R;
import com.shadow.smartposter.models.Chat;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatHolder> {

    private static final int RIGHT_CHAT = 0, LEFT_CHAT = 1;

    private List<Chat> mChats;

    private FirebaseAuth mAuth;

    public ChatsAdapter(List<Chat> chats) {
        mChats = chats;
        mAuth = FirebaseAuth.getInstance();
    }


    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;

        if (viewType == RIGHT_CHAT)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_chat_right, viewGroup, false);
        else if (viewType == LEFT_CHAT)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_chat_left, viewGroup, false);

        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder chatHolder, int position) {
        Chat chat = mChats.get(position);
        chatHolder.msgTV.setText(chat.getContent());
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView msgTV;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            msgTV = itemView.findViewById(R.id.msg_tv);

        }
    }

    @Override
    public int getItemViewType(int position) {
        String currentUserUid = mAuth.getCurrentUser().getUid();

        Chat chat = mChats.get(position);

        if (chat.getSender().equalsIgnoreCase(currentUserUid)) return RIGHT_CHAT;
        else return LEFT_CHAT;

    }
}
