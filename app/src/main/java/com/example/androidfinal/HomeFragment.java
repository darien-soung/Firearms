package com.example.androidfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {
    TextView tx_welcome;
    ImageView mRifle, mSniper, mSMG, mGrenade;
    View inflatedView;
    FirebaseAuth mFirebaseAuth;
    String userID;
    SettingsSingleton singleton;
    FirebaseUser mFirebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_home, container, false);
        castVar();
        setListeners();

        //initializing firebase object
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userID = mFirebaseUser.getUid();
        //initializing firebase object

        tx_welcome.append(singleton.getCurUsername().toUpperCase());

        return inflatedView;
    }

    private void castVar()
    {
        tx_welcome = (TextView)inflatedView.findViewById(R.id.tv_welcome);
        mRifle = (ImageView) inflatedView.findViewById(R.id.iv_rifle);
        mSniper = (ImageView) inflatedView.findViewById(R.id.iv_sniper);
        mSMG = (ImageView) inflatedView.findViewById(R.id.iv_smg);
        mGrenade = (ImageView) inflatedView.findViewById(R.id.iv_grenade);

        singleton = SettingsSingleton.getInstance();
    }

    private void setListeners()
    {
        //rifle listener
        mRifle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCatalog(0);
            }
        });


        //sniper listener
        mSniper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCatalog(1);
            }
        });


        //smg listener
        mSMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCatalog(2);
            }
        });


        //grenade listener
        mGrenade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCatalog(3);
            }
        });
    }

    private void openCatalog(int weaponClass)
    {
        Intent i = new Intent(getActivity(), Catalog.class);

        switch(weaponClass)
        {
            case 0:
                i.putExtra("WEAPON_CLASS", "Rifle");
                startActivity(i);
                break;

            case 1:
                i.putExtra("WEAPON_CLASS", "Sniper");
                startActivity(i);
                break;

            case 2:
                i.putExtra("WEAPON_CLASS", "SMG");
                startActivity(i);
                break;

            case 3:
                i.putExtra("WEAPON_CLASS", "Grenade");
                startActivity(i);
                break;

            default:
                Toast.makeText(getActivity(), "Unexpected Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
