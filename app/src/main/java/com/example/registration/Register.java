package com.example.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.registration.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity implements
        View.OnClickListener {
    DatabaseReference ref;
    FirebaseAuth mAuth;
    StorageReference imageRef;

    String uid;
    String usernameFound;
    String emailFound;
    String passwordFound;
    String filename;

    CircleImageView imageCircle;
    EditText username;
    EditText password;
    EditText email;
    Button photo;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username_edittext_register);
        email = findViewById(R.id.email_edittext_register);
        password = findViewById(R.id.password_eddittext_register);
        imageCircle = findViewById(R.id.selectphoto_imagevies_circle);
        photo = findViewById(R.id.selectphoto_button_register);

        findViewById(R.id.already_registartion).setOnClickListener(this);
        findViewById(R.id.register_button_register).setOnClickListener(this);
        findViewById(R.id.selectphoto_button_register).setOnClickListener(this);

        dialog = new ProgressDialog(this);
    }

    Uri selectedPhoto = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Register.super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RegisterActivity", "Photo selected");
            selectedPhoto = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageCircle.setImageBitmap(bitmap);
            photo.setAlpha(0f);
        }
    }

    public void uploadImageToStorage(){
        if (selectedPhoto == null){
            return;
        }
        filename = UUID.randomUUID().toString();
        imageRef = FirebaseStorage.getInstance().getReference("/image/" + filename);
        imageRef.putFile(selectedPhoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("RegisterActivity", "Successfully uploaded image:" + Objects.requireNonNull(taskSnapshot.getMetadata()).getPath());
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("RegisterActivity", "FileLocation " + uri);
                        saveUserInDatabase(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Log.d("RegisterActivity", "Failed to upload image to storage");
            }
        });
    }

    private void saveUserInDatabase (String profileImageUrl) {
        uid = FirebaseAuth.getInstance().getUid();
        ref = FirebaseDatabase.getInstance().getReference();
        User user = new User(email.getText().toString(), uid, profileImageUrl, username.getText().toString());
        ref.child("/users/" + user.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("RegisterActivity", "Saved in Database");
                dialog.dismiss();
                Intent intent = new Intent(Register.this, LoginIn.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Log.d("RegisterActivity", "Failed to save in Database");
            }
        });
    }

    public void imageSelect() {
        Log.d("RegisterActivity", "Try to show photo selector");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    public void register() {
        usernameFound = username.getText().toString();
        emailFound = email.getText().toString();
        passwordFound = password.getText().toString();

        if (emailFound.isEmpty() || usernameFound.isEmpty() || passwordFound.isEmpty() || passwordFound.length() < 6){
            formatCheck();
            return;
        }

        Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("username").equalTo(usernameFound);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    username.setError("already taken");
                }
                else {
                    dialog.setMessage("Register...");
                    dialog.show();
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(emailFound, passwordFound)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("emailCreateSuccess", "createUserWithEmail:success");
                                        uploadImageToStorage();
                                    } else {
                                        dialog.dismiss();
                                        Log.w("emailCreateFail", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Register.this, "Authentication failed.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.selectphoto_button_register:
                imageSelect();
                break;
            case R.id.register_button_register:
                register();
                break;
            case R.id.already_registartion:
                finish();
                break;
            case R.id.forgot_you_password:
                Intent intent = new Intent(Register.this, ForgotPassword.class);
                startActivity(intent);
            default:
        }
    }

    public void formatCheck(){
        if (passwordFound.length() < 6)
            password.setError("6 characters");

        if (emailFound.isEmpty())
            email.setError("Empty");

        if (passwordFound.isEmpty())
            password.setError("Empty");

        if (usernameFound.isEmpty())
            username.setError("Empty");
    }
}
