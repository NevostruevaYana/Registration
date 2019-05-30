package com.example.registration;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailChange extends AppCompatActivity {
    ProgressDialog dialog;
    EditText newEmail;
    Button changeEmail;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change);
        getSupportActionBar().setTitle("Email change");
        dialog = new ProgressDialog(this);
        ref = FirebaseDatabase.getInstance().getReference();
        newEmail = findViewById(R.id.new_email);
        changeEmail = findViewById(R.id.change_email);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });
    }

    public void changeEmail(){
        final String email = newEmail.getText().toString();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (email.isEmpty()) {
            newEmail.setError("Empty");
            return;
        }

        if (user != null) {
            dialog.setMessage("Changing Email");
            dialog.show();
            user.updateEmail(newEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        ref.child("/users/" + user.getUid() + "/email/").setValue(email);
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Your Email has been changed", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(EmailChange.this, LoginIn.class);
                        startActivity(intent);
                        finish();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Email could not be changed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
