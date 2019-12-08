package com.example.androidfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText ed_email, ed_password;
    Button bt_login, bt_signup;
    ProgressDialog mProgressDialog;
    FirebaseAuth mFirebaseAuth;
    String TAG = "Error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        castVar();

        if(mFirebaseAuth.getCurrentUser() != null)
        {
            signIn();
            //mFirebaseAuth.signOut();
        }

        ed_email.setText("vextorn.soung@gmail.com");
        ed_password.setText("abc123");
    }

    public void castVar()
    {
        mFirebaseAuth = FirebaseAuth.getInstance();
        ed_email = (EditText)findViewById(R.id.ed_damage);
        ed_password = (EditText)findViewById(R.id.ed_password);
        bt_login = (Button)findViewById(R.id.bt_login);
        bt_signup = (Button)findViewById(R.id.bt_signup);
        mProgressDialog = new ProgressDialog(this);


    }

    public void bt_login_clicked(View view)
    {
        final String email = ed_email.getText().toString().trim();
        String password = ed_password.getText().toString().trim();

        if(email.isEmpty())
        {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
        }

        else if(password.isEmpty())
        {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }

        else
        {
            mProgressDialog.setMessage("Logging in");
            mProgressDialog.show();

            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mProgressDialog.dismiss();
                    if(task.isSuccessful())
                    {
                         signIn();
                    }
                    else
                    {
                        Log.w(TAG, "Failed login", task.getException());
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void bt_signup_clicked(View view)
    {
        finish();
        Intent i = new Intent(MainActivity.this, Signup.class);
        startActivity(i);
    }

    public void signIn()
    {
        finish();
        Intent i = new Intent(MainActivity.this, Menu.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
