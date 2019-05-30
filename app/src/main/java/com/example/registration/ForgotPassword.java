package com.example.registration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText email_reset;
    Button reset;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setTitle("Password reset");
        email_reset = findViewById(R.id.email_reset);
        reset = findViewById(R.id.button_reset);
        firebaseAuth = FirebaseAuth.getInstance();
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_reset.getText().toString();
                if (email.isEmpty()) {
                    email_reset.setError("Empty");
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPassword.this,"Check you Email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPassword.this, LoginIn.class));
                                finish();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(ForgotPassword.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
