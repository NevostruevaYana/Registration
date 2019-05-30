package com.example.registration.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.registration.ChatroomAdap;
import com.example.registration.CreateChatroom;
import com.example.registration.EnterChatroomMessage;
import com.example.registration.Models.Chatroom;
import com.example.registration.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private ChatroomAdap chatroomAdap;
    private List<Chatroom> mChatrooms;

    FirebaseUser fUser;
    DatabaseReference reference;

    private List<String> chatroomsList;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chatsmenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        chatroomsList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("/chatrooms/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatroomsList.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (!snapshot.toString().contains("@")) {
                        for (DataSnapshot d : snapshot.child("/users/").getChildren()) {
                            if (d.getValue().toString().equals(fUser.getUid())) {
                                chatroomsList.add(snapshot.getKey());
                                readChatroom();
                            }
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void readChatroom(){
        mChatrooms = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("/chatrooms/");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChatrooms.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatroom chatroom = snapshot.getValue(Chatroom.class);

                    for (String id : chatroomsList){
                        if (chatroom.getChatroomName().equals(id)){
                            mChatrooms.add(chatroom);
                        }
                    }
                }
                chatroomAdap = new ChatroomAdap(getContext(), mChatrooms);
                recyclerView.setAdapter(chatroomAdap);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.create_chatroom:
                Intent intentCC = new Intent(getActivity(), CreateChatroom.class);
                startActivity(intentCC);
                break;
            case R.id.chatroom_messages:
                Intent intentCM = new Intent(getActivity(), EnterChatroomMessage.class);
                startActivity(intentCM);
                break;
        }
        return true;
    }
}