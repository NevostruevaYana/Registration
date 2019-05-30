package com.example.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginIn extends AppCompatActivity implements View.OnClickListener{
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    EditText email;
    EditText password;

    String emailFound;
    String passwordFound;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.login_button_login).setOnClickListener(this);
        findViewById(R.id.login_registration).setOnClickListener(this);
        findViewById(R.id.forgot_you_password).setOnClickListener(this);

        email = findViewById(R.id.email_edittext_login);
        password = findViewById(R.id.password_edittext_login);

        dialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            Intent intent = new Intent(LoginIn.this, SlideMenu.class);
            startActivity(intent);
            finish();
        }
    }

    public void signIn() {
        emailFound = email.getText().toString();
        passwordFound = password.getText().toString();

        if (passwordFound.length() < 6 || emailFound.isEmpty() || passwordFound.isEmpty()){
            formatCheck();
            return;
        }
        dialog.setMessage("Logining...");
        dialog.show();

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(emailFound, passwordFound)
                .addOnCompleteListener(LoginIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("signInWithEmailSuccess", "signInWithEmail:success");
                            dialog.dismiss();
                            Intent intent = new Intent(LoginIn.this, SlideMenu.class);
                            startActivity(intent);
                            finish();
                            //TODO: showChat() for chowing main chat layout
                        } else {
                            dialog.dismiss();
                            Log.w("signInWithEmailFail", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.login_registration:
                Intent intent = new Intent(LoginIn.this, Register.class);
                startActivity(intent);
                break;
            case R.id.login_button_login:
                signIn();
                break;
            case R.id.forgot_you_password:
                Intent intent1 = new Intent(LoginIn.this, ForgotPassword.class);
                startActivity(intent1);
                break;
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
    }
}