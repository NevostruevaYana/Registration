package com.example.registration;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.registration.Models.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainChatFragment extends Fragment {
    String chatroomPath;
    String currentUser;
    Uri photoURL;
    String currentUID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup vg, Bundle state) {
        return inflater.inflate(R.layout.fragment_chatroom, vg, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String chatroomName = getArguments().getString("chatroomName");

        super.onCreate(savedInstanceState);
        //getSupportActionBar().setTitle(arguments.get("chatroomName").toString());
        //TODO: add the action bar title changer in main activity cuz title change should be done there

        chatroomPath = "/chatrooms/" + chatroomName + "/messages";

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("/users/")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()){
                            if(child.getKey().equals(uid)){
                                currentUser = child.child("/username/").getValue().toString();
                                currentUID = uid;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        photoURL = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

        Log.d("ChatActivity", "getting the chatroom path, current user and URL");


        final RecyclerView listOfMessages = getView().findViewById(R.id.frag_list_of_messages);
        listOfMessages.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        listOfMessages.setLayoutManager(layoutManager);

        Query query = FirebaseDatabase.getInstance().getReference().child(chatroomPath).limitToLast(50);

        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();

        final FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter =
                new FirebaseRecyclerAdapter<Message, MessageViewHolder>
                        (options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
                        //setting the view of a single message
                        holder.setMessage(model);
                        Log.d("chatActivity", "catched a new message, yay");
                    }

                    @Override
                    public int getItemViewType(int position){
                        //if it is our message - return 1
                        //if it was sent by the other person - return 0
                        try {
                            if (currentUID.equals(getItem(position).getSender())) return 1;
                            else return 0;
                        } catch (NullPointerException e) {
                            System.out.println(currentUID);
                            System.out.println(getItem(position).getSender());

                            e.printStackTrace();
                        }
                        return 0;
                    }

                    @Override
                    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        if(viewType == 0) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.message, parent, false);
                            return new MessageViewHolder(view);
                        }
                        else if (viewType == 1){
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.this_user_message, parent, false);
                            return new MessageViewHolder(view);
                        }
                        else{
                            throw new RuntimeException("incorrect type while trying to get viewType");
                        }
                    }
                };
        adapter.startListening();
        listOfMessages.setAdapter(adapter);


        final FloatingActionButton sendMessageButton = getView().findViewById(R.id.frag_fab);

        final EditText userMessageInput = getView().findViewById(R.id.frag_messageInput);

        View.OnClickListener fabClickListener = new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Message userMessage = new Message(
                        userMessageInput.getText().toString(),
                        currentUser,
                        photoURL.toString(),
                        currentUID
                );

                FirebaseDatabase.getInstance().getReference()
                        .child(chatroomPath).push().setValue(userMessage);

                userMessageInput.getText().clear();

                if(adapter.getItemCount() >= 1){
                    listOfMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
            }
        };

        sendMessageButton.setOnClickListener(fabClickListener);
    }
}
