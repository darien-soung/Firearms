package com.example.androidfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Menu extends AppCompatActivity {
    String userID;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    ValueEventListener listener;
    SettingsSingleton singleton;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //------------ Initialize Firebase ------------------------------------------------------------------------------------
        mFirebaseAuth = FirebaseAuth.getInstance();
        singleton = SettingsSingleton.getInstance();

        if(mFirebaseAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(Menu.this, MainActivity.class));
        }
        else
        {
            currentUser = mFirebaseAuth.getCurrentUser();
            userID = currentUser.getUid();
        }
        // --------------- Initialize Firebase -----------------------------------------------------------------------------------------

        //----------------------Initialize user details-------------
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                singleton.setCurUsername(dataSnapshot.child(userID)
                        .child("name").getValue(String.class));

                final Fragment defaultFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, defaultFragment).commit();

                //-----------------------Inflate bottom nav layout------------------------
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
                bottomNav.setOnNavigationItemSelectedListener(navListener);
                bottomNav.setVisibility(View.VISIBLE);
                //-----------------------Inflate bottom nav layout--------------------------
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Menu.this,
                        "Database Error: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.addValueEventListener(listener);
        //---------------------Initialize user details-------------------
    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch(menuItem.getItemId())
            {

                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;

                case R.id.nav_profile:
                    selectedFragment = new ProfileFragment();
                    break;

                case R.id.nav_settings:
                    selectedFragment = new SettingsFragment();
                    break;

                default:
                    Toast.makeText(Menu.this,
                            "Unexpected Error..", Toast.LENGTH_SHORT).show();
                    break;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    public void onStop()
    {
        super.onStop();

        if(databaseReference != null && listener != null)
        {
            databaseReference.removeEventListener(listener);
        }
    }
}
