package com.example.registration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.registration.Models.Chatroom;
import com.example.registration.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;

public class EnterPrivateMessage extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference ref;

    String userUid;
    String currentUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_private_message);

        final Button button = findViewById(R.id.enter_);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                ref = FirebaseDatabase.getInstance().getReference();

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final EditText user = findViewById(R.id.pm_userna);
                        final String username = user.getText().toString();

                        if (username.isEmpty()) {
                            user.setError("Empty");
                            return;
                        }
                        ref.child("/users/" + firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final User currentUser = dataSnapshot.getValue(User.class);
                                final String currentUsername = currentUser.getUsername();
                                currentUserUid = currentUser.getUid();
                                Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("username").equalTo(username);
                                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getChildrenCount() > 0) {
                                            if (currentUsername.equals(username)) {
                                                final String privateChatroomName = "@" + currentUsername;
                                                Query privateChatroomNameQuery = FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("chatroomName").equalTo(privateChatroomName);
                                                privateChatroomNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getChildrenCount() == 0) {
                                                            ref.child("/chatrooms/" + privateChatroomName).setValue(new Chatroom(privateChatroomName));
                                                            ref.child("/chatrooms/" + privateChatroomName + "/users/" + currentUsername).setValue(currentUserUid);
                                                        }
                                                        IntentWithData(privateChatroomName);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            } else {
                                                final String chatroomName = "@" + currentUsername + "@" + username;
                                                final String otherChatroomName = "@" + username + "@" + currentUsername;
                                                Query chatroomNameQuery = FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("chatroomName").equalTo(chatroomName);
                                                chatroomNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getChildrenCount() > 0) {
                                                            IntentWithData(chatroomName);
                                                        } else {
                                                            Query otherChatroomNameQuery = FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("chatroomName").equalTo(otherChatroomName);
                                                            otherChatroomNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.getChildrenCount() > 0) {
                                                                        IntentWithData(otherChatroomName);
                                                                    } else {

                                                                        Query query = ref.child("/users/").orderByChild("username").equalTo(username);
                                                                        query.addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                if (dataSnapshot.exists()) {
                                                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                                        userUid = snapshot.getKey();
                                                                                        ref.child("/chatrooms/" + chatroomName).setValue(new Chatroom(chatroomName));
                                                                                        ref.child("/chatrooms/" + chatroomName + "/users/" + currentUsername).setValue(currentUserUid);
                                                                                        ref.child("/chatrooms/" + chatroomName + "/users/" + username).setValue(userUid);
                                                                                        IntentWithData(chatroomName);
                                                                                    }

                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                @Override

                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        } else {
                                            user.setError("Not found");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                });
            }
        });
    }

    private void IntentWithData(String chatroomName) {
        Intent intent = new Intent(EnterPrivateMessage.this, Main_chat_activity.class);
        intent.putExtra("chatroomName", chatroomName);
        startActivity(intent);
        Log.d("createSavedMessages", "throwing intent with name:" + chatroomName);
        finish();
    }
}