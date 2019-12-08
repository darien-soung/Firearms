package com.example.androidfinal;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart extends AppCompatActivity implements ListAdapter.OnItemClickListener{
    private RecyclerView mRecyclerView;
    private TextView mTotal, mTax, mGrandTotal, mCheckout;
    private DatabaseReference databaseReferenceCart, databaseReferenceGuns, databaseReferenceWallet;
    private ValueEventListener listenerCart, listenerGuns, listenerWallet;
    private ListAdapter mAdapter;
    private List<Guns> mGuns;
    private List<String> mList;
    private List<String> mListKeys;
    private String curUserID;
    private boolean firstTimePopulated;
    private ProgressBar mProgressBar;
    private int totalPrice,totalGrandPrice, wallet;
    private SettingsSingleton singleton;
    private final double tax = 0.06;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        castVar();
        initializeDatabase();
    }

    private void castVar()
    {
        mRecyclerView = (RecyclerView)findViewById(R.id.cart_recycler);
        mTotal = (TextView)findViewById(R.id.tv_total);
        mTax = (TextView)findViewById(R.id.tv_tax);
        mGrandTotal = (TextView)findViewById(R.id.tv_grandTotal);
        mCheckout = (TextView)findViewById(R.id.tv_checkout);
        mProgressBar = (ProgressBar)findViewById(R.id.loading_circle);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(50));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mGuns = new ArrayList<>();
        mList = new ArrayList<>();
        mListKeys = new ArrayList<>();

        firstTimePopulated = false;
        totalPrice = 0;
        totalGrandPrice = 0;

        singleton = SettingsSingleton.getInstance();
    }

    private void initializeDatabase()
    {
        curUserID = FirebaseAuth.getInstance().getUid();

        //Gets items ID from cart
        databaseReferenceCart = FirebaseDatabase.getInstance().getReference("Users").child(curUserID)
                .child("cart"); //db reference pointed to the favourite guns of each user

        listenerCart = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren())
                    //iterates through all the favourite guns of the current user
                {
                    mList.add(child.getValue().toString());
                    //adds all the favourites into a list so that it can be used as a condition
                    // to print favourited guns into the recyclerview
                    mListKeys.add(child.getKey()); //gets the key for deletion
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ShoppingCart.this, "Error: "
                        + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReferenceCart.addValueEventListener(listenerCart);
        //Gets items ID from cart

        //Guns DATABASE INITIALIZER
        databaseReferenceGuns = FirebaseDatabase.getInstance().getReference("Guns");

        listenerGuns = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(firstTimePopulated)
                    mAdapter.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Guns newGun = postSnapshot.getValue(Guns.class);

                    for(int i=0; i<mList.size(); i++)
                    {
                        if(mList.get(i).equals(newGun.getName()))
                        {
                            mGuns.add(newGun);
                            totalPrice = totalPrice + newGun.getPrice();
                        }
                    }
                }

                mGuns = getArrangedList(mList,mGuns);
                //1st param: list with correct order, 2nd param list with different order

                mAdapter = new ListAdapter(ShoppingCart.this, mGuns);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(ShoppingCart.this);
                firstTimePopulated = true;

                if(singleton.getCurrency() == 0)
                {
                    mTotal.append("$ " + totalPrice);

                    Double tempDoubleTax = totalPrice * tax;
                    int tempTax = tempDoubleTax.intValue(); //casting to int

                    mTax.append("$ " + (tempDoubleTax));

                    totalGrandPrice = totalPrice + tempTax;
                    mGrandTotal.append("$ " + totalGrandPrice);
                }
                else if(singleton.getCurrency() == 1)
                {
                    mTotal.append("RM " + (totalPrice * 4));

                    Double tempDoubleTax = totalPrice * tax;
                    int tempTax = tempDoubleTax.intValue(); //casting to int

                    mTax.append("RM " + (tempDoubleTax * 4));

                    totalGrandPrice = totalPrice + tempTax;
                    mGrandTotal.append("RM " + (totalGrandPrice*4));
                }

                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ShoppingCart.this,
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReferenceGuns.addValueEventListener(listenerGuns);
        //Guns DATABASE INITIALIZER

        //Wallet DATABASE INITIALIZER
        databaseReferenceWallet = FirebaseDatabase.getInstance()
                .getReference("Users").child(curUserID).child("wallet");

        listenerWallet = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wallet = (dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ShoppingCart.this,
                        "Database Error: " +
                                databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReferenceWallet.addValueEventListener(listenerWallet);

        //Wallet DATABASE INITIALIZER

    }

    private List<Guns> getArrangedList(List<String> a, List<Guns> b)
    //gets the arranged list of the guns so that the positions of the guns can be used
    //to identify each item in the recyclerview
    {
        List<Guns> arrangedList;
        arrangedList = new ArrayList<>();

        for(int i=0 ;i<a.size(); i++)
        {
            for(int j=0; j<b.size(); j++)
            {
                if(a.get(i).equals(b.get(j).getName()))
                {
                    arrangedList.add(b.get(j));
                    break;
                }
            }
        }
        return arrangedList;
    }

    public void checkout_clicked(View view)
    {
        if(wallet >= totalGrandPrice)
        {
            if(!(mGuns.isEmpty()))
            {
                wallet = wallet - totalGrandPrice;
                databaseReferenceWallet.setValue(wallet);

                mAdapter.clear();
                databaseReferenceCart.removeValue();

                mTotal.setText("Total: ");
                mTax.setText("Tax: (6%): ");
                mGrandTotal.setText("Grand total: ");

                DatabaseReference databaseReferenceShipping;
                databaseReferenceShipping = FirebaseDatabase
                        .getInstance().getReference("Users").child(curUserID)
                        .child("itemsToShip");

                for(int i=0; i<mList.size(); i++)
                {
                    databaseReferenceShipping.push().setValue(mList.get(i));
                }

                mGuns.clear();
                mList.clear();
                mListKeys.clear();

                firstTimePopulated = false;
                totalPrice = 0;
                totalGrandPrice = 0;

                Toast.makeText(this,
                        "Items successfully bought, \nplease wait for them to arrive",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,
                        "Please add items to the cart", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            if(singleton.getCurrency() == 0)
            {
                Toast.makeText(this,
                        "Insufficient balance,\n" + "$ " + (totalGrandPrice - wallet)
                        + " more needed to complete order", Toast.LENGTH_SHORT).show();
            }
            else if(singleton.getCurrency() == 1)
            {
                Toast.makeText(this,
                        "Insufficient balance,\n" + "RM " + ((totalGrandPrice - wallet)*4)
                        + " more needed to complete order", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Gun clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        databaseReferenceCart.child(mListKeys.get(position)).removeValue();

        Toast.makeText(this, "Item removed from position: " + position, Toast.LENGTH_SHORT).show();

        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public void back_clicked(View view)
    {
        this.finish();
    }
}
