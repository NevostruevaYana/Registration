package com.example.registration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.registration.Models.Chatroom;
import com.example.registration.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateChatroom extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference ref;
    Button createButton;
    Button selectPhotoButton;
    CircleImageView imageCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chatroom);
        getSupportActionBar().setTitle("Create chatroom");

        createButton = findViewById(R.id.create_button);
        selectPhotoButton = findViewById(R.id.selectphoto_button_cc);

        selectPhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 2);
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference();
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        EditText createChatroomName = findViewById(R.id.create_chatroom_name);
                        EditText createPasswordChatroom = findViewById(R.id.create_password_chatroom);

                        final String chatroomName = createChatroomName.getText().toString();
                        final String chatroomPassword = createPasswordChatroom.getText().toString();

                        if(chatroomName.isEmpty() || chatroomName.contains("a") || chatroomPassword.isEmpty()) {
                            if (chatroomName.isEmpty()) {
                                createChatroomName.setError("Empty");
                            } else {
                                if (chatroomName.contains("@")) {
                                    createChatroomName.setError("Invalid name'@'");
                                }
                            }
                            if (chatroomPassword.isEmpty()) {
                                createPasswordChatroom.setError("Empty");
                            }
                            return;
                        }
                        ref.child("/users/" + firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final User currentUser = dataSnapshot.getValue(User.class);
                                final String currentUid = currentUser.getUid();
                                final String currentUsername = currentUser.getUsername();
                                Query chatroomNameQuery = FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("chatroomName").equalTo(chatroomName);
                                chatroomNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getChildrenCount() == 0) {
                                            String filename = UUID.randomUUID().toString();
                                            final StorageReference imageRef = FirebaseStorage.getInstance().getReference("/image/" + filename);
                                            imageRef.putFile(selectedPhoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            ref.child("/chatrooms/" + chatroomName).setValue(new Chatroom(chatroomName, chatroomPassword, uri.toString()));

                                                            ref.child("/chatrooms/" + chatroomName + "/users/" + currentUsername).setValue(currentUid);
                                                            Intent intent = new Intent(CreateChatroom.this, Main_chat_activity.class);
                                                            intent.putExtra("chatroomName", chatroomName);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.getMessage();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        databaseError.getMessage();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                databaseError.getMessage();
                            }
                        });
                    }
                });
            }
        });
    }

    Uri selectedPhoto;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CreateChatroom.super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhoto = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageCircle = findViewById(R.id.selectphoto_imagevies_circle_cc);
            selectPhotoButton = findViewById(R.id.selectphoto_button_cc);
            imageCircle.setImageBitmap(bitmap);
            selectPhotoButton.setAlpha(0f);
        }
    }
}