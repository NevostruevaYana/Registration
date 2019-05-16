package com.example.registration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CreateChatroom extends AppCompatActivity {

    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chatroom);

        final Button button = findViewById(R.id.create_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText createChatroomName = findViewById(R.id.create_chatroom_name);
                EditText createPasswordChatroom = findViewById(R.id.create_password_chatroom);

                final String chatroomNameFind = createChatroomName.getText().toString();
                final String passwordChatroomFind = createPasswordChatroom.getText().toString();

                ref = FirebaseDatabase.getInstance().getReference();

                if (chatroomNameFind.contains("@")){
                    Toast.makeText(CreateChatroom.this , "Don't use the @ symbol", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (chatroomNameFind.isEmpty() || passwordChatroomFind.isEmpty()) {
                    Toast.makeText(CreateChatroom.this , "Please enter text in chatroomName/pw", Toast.LENGTH_SHORT).show();
                    return;
                }

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                                Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("chatroomName").equalTo(chatroomNameFind);
                                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getChildrenCount() == 0){
                                            ref.child("/chatrooms/" + chatroomNameFind).setValue(new Chatroom(chatroomNameFind, passwordChatroomFind));
                                            Intent intent = new Intent(CreateChatroom.this, Message.class);
                                            intent.putExtra("chatroomName", chatroomNameFind);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(CreateChatroom.this, "Such chatroom exist", Toast.LENGTH_SHORT).show();
                                        }

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
}