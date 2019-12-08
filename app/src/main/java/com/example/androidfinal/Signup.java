package com.example.androidfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Signup extends AppCompatActivity {
    EditText ed_email, ed_password, ed_phone, ed_username;
    ProgressDialog mProgressDialog;

    DatabaseReference mDatabaseReference;
    FirebaseAuth mFirebaseAuth;
    String TAG = "Error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this);
        ed_email = (EditText)findViewById(R.id.ed_damage);
        ed_password = (EditText)findViewById(R.id.ed_password);
        ed_phone = (EditText)findViewById(R.id.ed_phone);
        ed_username = (EditText)findViewById(R.id.ed_username);
    }


    public void bt_signup_clicked(View view)
    {
        final String email, password, phone, username;
        final int wallet = 0;

        // ------------- Initializing User Details -----------------
        email = ed_email.getText().toString().trim();
        password = ed_password.getText().toString().trim();
        phone = ed_phone.getText().toString().trim();
        username = ed_username.getText().toString().trim();


        // ------------- Initializing User Details -----------------

        if(email.isEmpty())
        {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }

        else if(password.isEmpty() || phone.isEmpty() || username.isEmpty())
        {
            Toast.makeText(this, "Please fill in all the details required", Toast.LENGTH_SHORT).show();
        }

        else
        {
            mProgressDialog.setMessage("Registering User...");
            mProgressDialog.show();

            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new
                                User(username, email, phone))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mProgressDialog.dismiss();
                                signIn();
                            }
                        });
                    }
                    else
                    {
                        Log.w(TAG, "Failed registration", task.getException());
                        Toast.makeText(Signup.this, task.getException()
                                .getMessage(), Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
    }

    public void signIn()
    {
        finish();
        Intent i = new Intent(Signup.this, Menu.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void back_clicked(View view)
    {
        finish();
        Intent i = new Intent(Signup.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
