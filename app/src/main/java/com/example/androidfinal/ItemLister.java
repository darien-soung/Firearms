package com.example.androidfinal;

import android.graphics.Rect;
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
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ItemLister extends AppCompatActivity implements ListAdapter.OnItemClickListener {
    String listMode, curUserID;
    TextView mLabel;
    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    private Boolean firstTimePopulated;
    private DatabaseReference databaseReferenceFav, databaseReferenceGuns, databaseReferenceShip;
    private ValueEventListener listenerFav, listenerGuns, listenerShip;
    private ListAdapter mAdapter;
    private List<Guns> mGuns;
    private List<String> mList;
    private List<String> mListKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_lister);

        listMode = getIntent().getStringExtra("LIST_MODE");

        castVar();

        if(listMode.equals("ship"))
            mLabel.setText("Items to ship");

        else if(listMode.equals("favourites"))
            mLabel.setText("Favourites");

        else
            Toast.makeText(this, "List mode error", Toast.LENGTH_SHORT).show();


        initializeDatabase();
    }

    private void castVar()
    {
        mLabel = (TextView)findViewById(R.id.tv_label);
        mRecyclerView = (RecyclerView)findViewById(R.id.lister_recycler);
        mProgressBar = (ProgressBar)findViewById(R.id.loading_circle);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(50));

        mGuns = new ArrayList<>();
        mList = new ArrayList<>();
        mListKeys = new ArrayList<>();

        firstTimePopulated = false;
    }

    private void initializeDatabase()
    {
        curUserID = FirebaseAuth.getInstance().getUid();

        if(listMode.equals("ship"))
        {
            databaseReferenceShip = FirebaseDatabase.getInstance().getReference("Users").child(curUserID)
                    .child("itemsToShip"); //db reference pointed to the guns to be shipped of each user

            listenerShip = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren()) //iterates through all the favourite guns of the current user
                    {
                        mList.add(child.getValue().toString()); //adds all the guns to ship into a list so that it can be used as a condition
                                                                    //to print guns to ship into the recyclerview
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ItemLister.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            databaseReferenceShip.addValueEventListener(listenerShip);
        }

        else if(listMode.equals("favourites")) //Show favourites if the user clicked on the favourites TextView in ProfileFragment
        {
            databaseReferenceFav = FirebaseDatabase.getInstance().getReference("Users").child(curUserID)
                    .child("favourites"); //db reference pointed to the favourite guns of each user

            listenerFav = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren()) //iterates through all the favourite guns of the current user
                    {
                        mList.add(child.getValue().toString()); //adds all the favourites into a list so that it can be used as a condition
                                                                    // to print favourited guns into the recyclerview
                        mListKeys.add(child.getKey()); //gets the key for deletion
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ItemLister.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            databaseReferenceFav.addValueEventListener(listenerFav);
        }

        //Guns DATABASE INITIALIZER---------------------------------------------------------------------------------------------
        databaseReferenceGuns = FirebaseDatabase.getInstance().getReference("Guns");

        listenerGuns = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(firstTimePopulated)
                    mAdapter.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Guns newGun = postSnapshot.getValue(Guns.class);

                    if(mList.contains(newGun.getName()))
                    {
                        mGuns.add(newGun);
                    }
                }

                mGuns = getArrangedList(mList,mGuns);

                mAdapter = new ListAdapter(ItemLister.this, mGuns);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(ItemLister.this);
                firstTimePopulated = true;

                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ItemLister.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReferenceGuns.addValueEventListener(listenerGuns);

        //Guns DATABASE INITIALIZER---------------------------------------------------------------------------------------------

    }

    private List<Guns> getArrangedList(List<String> a, List<Guns> b) //gets the arranged list of the guns so that the positions of the guns can be used
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

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Gun clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        if(listMode.equals("favourites"))
        {
            databaseReferenceFav.child(mListKeys.get(position)).removeValue();

            Toast.makeText(this, "Item removed from position: " + position, Toast.LENGTH_SHORT).show();

            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
        else if(listMode.equals("ship"))
        {
            Toast.makeText(this, "Cannot delete items you already bought", Toast.LENGTH_SHORT).show();
        }

    }

    public void back_clicked(View view)
    {
        this.finish();
    }
}
