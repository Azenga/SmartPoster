package com.shadow.smartposter.fragments.publics;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shadow.smartposter.R;
import com.shadow.smartposter.adapters.ChatThreadAdapter;
import com.shadow.smartposter.models.Chat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";

    private FirebaseFirestore mDb;
    private Set<String> othersUids;

    private RecyclerView chatsThreadsRV;

    public ChatsFragment() {
        mDb = FirebaseFirestore.getInstance();
        othersUids = new HashSet<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        chatsThreadsRV = view.findViewById(R.id.chats_threads_rv);
        chatsThreadsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatsThreadsRV.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDb.collection("chats")
                .addSnapshotListener(
                        (queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Log.e(TAG, "onViewCreated: ", e);
                                return;
                            }

                            if (!queryDocumentSnapshots.isEmpty()) {
                                othersUids.clear();
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    Chat chat = snapshot.toObject(Chat.class);

                                    if (chat.getSender().equalsIgnoreCase(currentUserUid)) {
                                        othersUids.add(chat.getReceiver());
                                    }

                                    if (chat.getReceiver().equalsIgnoreCase(currentUserUid)) {
                                        othersUids.add(chat.getSender());
                                    }
                                }

                                List<String> userUids = new ArrayList<>(othersUids);

                                ChatThreadAdapter adapter = new ChatThreadAdapter(userUids);

                                chatsThreadsRV.setAdapter(adapter);

                            }else
                                Toast.makeText(getActivity(), "Empty Chats", Toast.LENGTH_SHORT).show();
                        }
                );
    }
}
