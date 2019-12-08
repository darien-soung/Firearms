package com.example.androidfinal;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Catalog extends AppCompatActivity implements ImageAdapter.OnItemClickListener{
    String weaponClass;
    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private TextView mLabel;
    private DatabaseReference mDatabaseRef;
    private List<Guns> mGuns;
    private ProgressBar mLoadingCircle;
    private ValueEventListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        weaponClass = getIntent().getStringExtra("WEAPON_CLASS");
        castVar();
        mLabel.setText(weaponClass + "s");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)); //set the layout for recyclerview
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(100)); //set spacing between each item in recyclerview
        mGuns = new ArrayList<>();

        setUpDbRef();
    }

    private void castVar()
    {
        mRecyclerView = findViewById(R.id.catalog_recycle);
        mRecyclerView.setHasFixedSize(true);

        mLabel = (TextView)findViewById(R.id.tv_label);

        mLoadingCircle = (ProgressBar)findViewById(R.id.loading_circle);
    }


    private void setUpDbRef() //seting up db reference
    {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Guns"); //setting the reference to guns

        listener = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    if(postSnapshot.getValue(Guns.class).getWeaponClass().equals(weaponClass))
                        //print out the guns with same
                        // weapon class as clicked in HomeFragment
                    {
                        Guns guns = postSnapshot.getValue(Guns.class);
                        mGuns.add(guns);
                    }
                }

                mImageAdapter = new ImageAdapter(Catalog.this, mGuns);

                mRecyclerView.setAdapter(mImageAdapter);
                //setting adapter for the recycler view

                mImageAdapter.setOnItemClickListener(Catalog.this);

                mLoadingCircle.setVisibility(View.INVISIBLE);
                //remove the loading circle that indicates that the loading is done
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Catalog.this,
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mLoadingCircle.setVisibility(View.INVISIBLE);
                //remove the loading circle that indicates that the loading is done
            }
        });

        mDatabaseRef.addValueEventListener(listener);
    }

    public void back_clicked(View view)
    {
        this.finish();
    }

    //OnClickMethod for the menu items
    @Override
    public void onItemClick(int pos) {
        //Toast.makeText(this, pos, Toast.LENGTH_SHORT).show();

        Intent i = new Intent(Catalog.this, GunDetails.class);
        i.putExtra("Gun", mGuns.get(pos)); //put in information of the current gun to be displayed in the next activity
        startActivity(i);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if(mDatabaseRef != null && listener != null) //closing the onDataChangedListener so it doesnt trigger when data is changed
        {                                               //in another activity
            mDatabaseRef.removeEventListener(listener);
        }
    }
}

class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration { //class for recyclerview decorations such as spacing

    private final int verticalSpaceHeight;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight) { //setting the gap between two items in the recyclerview
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = verticalSpaceHeight;
    }
}