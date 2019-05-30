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

public class PasswordChange extends AppCompatActivity {
    EditText newPassword;
    ProgressDialog dialog;
    Button changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        getSupportActionBar().setTitle("Password change");
        dialog = new ProgressDialog(this);
        newPassword = findViewById(R.id.new_password);
        changePassword = findViewById(R.id.change_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }
    public void changePassword(){
        String password = newPassword.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (password.isEmpty() || password.length() < 6){
            formatCheck(password);
            return;
        }
        if (user != null) {
            dialog.setMessage("Changing password");
            dialog.show();

            user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Your password has been changed", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(PasswordChange.this, LoginIn.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        dialog.dismiss();
                        String error = task.getException().getMessage();
                        Toast.makeText(PasswordChange.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void formatCheck(String a1){
        if (a1.isEmpty())
            newPassword.setError("Empty");

        if (a1.length() < 6)
            newPassword.setError("6 characters");
    }
}
