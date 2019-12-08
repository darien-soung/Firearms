package com.example.androidfinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Wallet extends AppCompatActivity {
    private EditText mAmount;
    private SettingsSingleton singleton;
    private int curWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        mAmount = (EditText)findViewById(R.id.ed_wallet);
        singleton = SettingsSingleton.getInstance();

        curWalletAmount = Integer.parseInt(getIntent().getStringExtra("WALLET_AMOUNT"));

        if(singleton.getCurrency() == 0)
        {
            mAmount.setHint("$");
        }
        else if(singleton.getCurrency() == 1)
        {
            mAmount.setHint("RM");
        }

    }

    public void add_clicked(View view)
    {
        if(Integer.parseInt(mAmount.getText().toString()) > 100000)
        {
            mAmount.setError("Amount more than 100K is invalid");
        }
        else
        {
            DatabaseReference databaseReference;

            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid())
                    .child("wallet");

            if(singleton.getCurrency() == 0)
            {
                int amount = Integer.parseInt(mAmount.getText().toString());
                databaseReference.setValue(amount + curWalletAmount);
                Toast.makeText(this,  "$ " + amount + " is credited into account", Toast.LENGTH_SHORT).show();
            }
            else if(singleton.getCurrency() == 1)
            {
                int amount = Integer.parseInt(mAmount.getText().toString()) / 4;
                databaseReference.setValue(amount + (curWalletAmount/4));
                Toast.makeText(this, "RM " + (amount*4) + " is credited into account", Toast.LENGTH_SHORT).show();
            }

            this.finish();
        }
    }

    public void back_clicked(View view)
    {
        this.finish();
    }
}
