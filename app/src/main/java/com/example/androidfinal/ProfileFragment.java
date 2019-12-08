package com.example.androidfinal;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment{
    TextView mUsername, mWallet, mItemsToShip, mFavourites;
    View inflatedView;
    DatabaseReference databaseReferenceWallet, databaseReferenceFav, databaseReferenceShip;
    ValueEventListener listenerWallet, listenerFav, listenerShip;
    String curUID, walletAmount;
    ImageView mShopCart;
    int favCount, shipCount;
    SettingsSingleton singleton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_profile, container, false);

        castVar();
        initDatabase();
        populateWidgets();

        mWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Wallet.class);
                i.putExtra("WALLET_AMOUNT", walletAmount);
                startActivity(i);
            }
        });

        mItemsToShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ItemLister.class);
                i.putExtra("LIST_MODE", "ship");
                startActivity(i);
            }
        });

        mFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ItemLister.class);
                i.putExtra("LIST_MODE", "favourites");
                startActivity(i);
            }
        });

        mShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ShoppingCart.class);
                startActivity(i);
            }
        });

        return inflatedView;
    }

    private void castVar()
    {
        mUsername = (TextView)inflatedView.findViewById(R.id.tv_username2);
        mWallet = (TextView)inflatedView.findViewById(R.id.tv_wallet);
        mItemsToShip = (TextView)inflatedView.findViewById(R.id.tv_shipping);
        mFavourites = (TextView)inflatedView.findViewById(R.id.tv_favourites);
        mShopCart = (ImageView)inflatedView.findViewById(R.id.shop_button);

        singleton = SettingsSingleton.getInstance();

        favCount = 0;
        shipCount = 0;
    }

    private void initDatabase()
    {
        curUID = FirebaseAuth.getInstance().getUid();

        //get wallet value----------------------------------------------------------
        databaseReferenceWallet = FirebaseDatabase.getInstance().getReference("Users")
                .child(curUID).child("wallet");

        listenerWallet = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                switch(singleton.getCurrency())
                {
                    case 0:
                        mWallet.setText("$ " + dataSnapshot.getValue().toString());
                        walletAmount = dataSnapshot.getValue().toString();
                        break;

                    case 1:
                        int i = Integer.parseInt(dataSnapshot.getValue().toString());
                        mWallet.setText("RM " + i*4);
                        walletAmount = i*4 + "";
                        break;

                    default:
                        Toast.makeText(getActivity(), "Currency error", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReferenceWallet.addValueEventListener(listenerWallet);
        //get wallet value----------------------------------------------------------


        //get favourites count----------------------------------------------------------
        databaseReferenceFav = FirebaseDatabase.getInstance().getReference("Users")
                .child(curUID).child("favourites");

        listenerFav = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favCount = 0;

                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    favCount++;
                }

                mFavourites.setText(favCount + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReferenceFav.addValueEventListener(listenerFav);
        //get favourites count----------------------------------------------------------

        //get items to ship count----------------------------------------------------------
        databaseReferenceShip = FirebaseDatabase.getInstance().getReference("Users")
                .child(curUID).child("itemsToShip");

        listenerShip = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shipCount = 0;

                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    shipCount++;
                }
                mItemsToShip.setText(shipCount + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReferenceShip.addValueEventListener(listenerShip);

        //get items to ship count----------------------------------------------------------
    }

    private void populateWidgets()
    {
        if(!(singleton.getCurUsername().equals("")))
            mUsername.setText(singleton.getCurUsername());
    }


    @Override
    public void onStop() //disable all event listeners so that it doesn't trigger when data is changed in another activity
    {
        super.onStop();

        /*
        if(databaseReferenceFav != null && listenerFav != null)
        {
            databaseReferenceFav.removeEventListener(listenerFav);
        }


        if(databaseReferenceWallet != null && listenerWallet != null)
        {
            databaseReferenceWallet.removeEventListener(listenerWallet);
        }

        if(databaseReferenceShip != null && listenerShip != null)
        {
            databaseReferenceShip.removeEventListener(listenerShip);
        }
         */
    }
}
