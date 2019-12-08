package com.example.androidfinal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GunDetails extends AppCompatActivity implements CartDialog.CartDialogListener, CommentDialog.CommentDialogListener {
    TextView mName, mFiringMode, mDamage, mWeight, mClass, mPrice;
    ImageView mImage, mFavourites;
    RecyclerView mRecyclerView;
    private CommentsAdapter mAdapter;
    private List<Comments> mCommentsList;
    String curUserID, curKey, curGunKey;
    Guns curGun;
    boolean isFavourited, firstTimePopulated;
    DatabaseReference databaseReferenceFav, databaseReferenceCart, databaseReferenceGuns, databaseReferenceComments;
    ValueEventListener listenerFav, listenerGuns, listenerComments;
    private SettingsSingleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gun_details);

        Intent i = getIntent();
        curGun = i.getParcelableExtra("Gun"); //getting gun info from previous activity because retrieving data through bundles is
                                                        //faster than calling valueListener
        castVar();
        populateWidgets(curGun); //populate the widgets with gun info
        initDatabase(); //database initializer
    }

    private void castVar()
    {
        mName = (TextView)findViewById(R.id.tv_name);
        mFiringMode = (TextView)findViewById(R.id.tv_firingMode);
        mDamage = (TextView)findViewById(R.id.tv_damage);
        mWeight = (TextView)findViewById(R.id.tv_weight);
        mClass = (TextView)findViewById(R.id.tv_class);
        mPrice = (TextView)findViewById(R.id.tv_price);

        mImage = (ImageView)findViewById(R.id.iv_weapon);
        mFavourites = (ImageView)findViewById(R.id.iv_favourite);
        singleton = SettingsSingleton.getInstance();

        curUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        isFavourited = false;
        firstTimePopulated = false;
        singleton = SettingsSingleton.getInstance();

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_comments);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCommentsList = new ArrayList<>();
    }

    private void populateWidgets(Guns gun)
    {
        mName.setText(gun.getName());

        mFiringMode.append(gun.getFiringMode());
        mDamage.append(gun.getDamage() + "");
        mWeight.append(gun.getWeight() + "");
        mClass.append(gun.getWeaponClass());

        if(singleton.getCurrency() == 1)
        {
            mPrice.setText("RM " + (gun.getPrice() * 4));
        }
        else
        {
            mPrice.setText("$" + gun.getPrice());
        }

        Picasso.with(this).load(gun
                .getImageUrl())
                .fit()
                .centerInside()
                .into(mImage);
    }

    private void initDatabase()
    {
        //Cart DATABASE INITIALIZER--------------------------------------------------------------------------------------------------------------
        databaseReferenceCart = FirebaseDatabase.getInstance().getReference("Users").child(curUserID)
                .child("cart");
        //Cart DATABASE INITIALIZER--------------------------------------------------------------------------------------------------------------




        //GUNS DATABASE INITIALIZER--------------------------------------------------------------------------------------------------------------
        databaseReferenceGuns = FirebaseDatabase.getInstance().getReference("Guns");

        listenerGuns = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                    //iterates the database to find the gun that matches with the gun selected
                {
                    if(postSnapshot.getValue(Guns.class).getName().equals(curGun.getName()))
                        //condition for finding gun document
                    {
                        //Guns guns = postSnapshot.getValue(Guns.class);
                        curGunKey = postSnapshot.getKey();
                        //get the key for the current gun to access its child documents

                         databaseReferenceComments = databaseReferenceGuns
                                 .child(curGunKey).child("comments");
                         //narrow down the reference to comments of each gun

                         listenerComments = new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 if(firstTimePopulated)
                                     //clears the recyclerview so that the old set of comments
                                     // are removed to make way for the new set
                                    mAdapter.clear();
                                 //of comments that include the new comment
                                 // when a new comment is inserted

                                 for(DataSnapshot child : dataSnapshot.getChildren())
                                     //iterates through each comment of the gun
                                 {
                                     Comments newComment = new
                                             Comments(child.getValue().toString());
                                     mCommentsList.add(newComment);
                                 }

                                 Collections.reverse(mCommentsList);
                                 //reversing collections so that newer comments are listed first
                                 mAdapter = new
                                         CommentsAdapter(GunDetails.this, mCommentsList);
                                 mRecyclerView.setAdapter(mAdapter);
                                 firstTimePopulated = true;
                                 //setting flag for the first time population
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {
                                 Toast.makeText(GunDetails.this,
                                         databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                             }
                         };

                        databaseReferenceComments.addValueEventListener(listenerComments);
                        break;
                    }
                }
                //mLoadingCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GunDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                //mLoadingCircle.setVisibility(View.INVISIBLE);
            }
        });

        databaseReferenceGuns.addValueEventListener(listenerGuns);

        //GUNS DATABASE INITIALIZER--------------------------------------------------------------------------------------------------------------



        //Favourites DATABASE INITIALIZER--------------------------------------------------------------------------------------------------------
        databaseReferenceFav = FirebaseDatabase.getInstance().getReference("Users").child(curUserID)
                .child("favourites"); //db reference pointed to the favourite guns of each user

        listenerFav = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot child : dataSnapshot.getChildren()) //iterates through all the favourite guns of the current user
                {
                    if(child.getValue().toString().equals(curGun.getName())) //set the favourites icon to red if it is already favourited by user
                    {
                        isFavourited = true;

                        curKey = child.getKey(); //gets the key for the favourited current gun for removal if prompted

                        mFavourites.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                                R.color.brightred));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GunDetails.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReferenceFav.addValueEventListener(listenerFav);
        //Favourites DATABASE INITIALIZER-------------------------------------------------------------------------------------------------------
    }

    public void back_clicked(View view)
    {
        this.finish();
    }

    public void favourite_clicked(View view)
    {
        if(!isFavourited) //triggers if gun isnt favourited
        {
            //favourite current gun
            databaseReferenceFav.push().setValue(curGun.getName()); //add the name of gun into the favourite field of the current users profile

            Toast.makeText(this, curGun.getName() + " favourited", Toast.LENGTH_SHORT).show();

            mFavourites.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                    R.color.brightred));
        }
        else //triggers if the gun is already favourited
        {
            //delete favourited record
            databaseReferenceFav.child(curKey).removeValue(); //remove the document
            isFavourited = false;

            Toast.makeText(this, curGun.getName()
                    + " removed from favourited", Toast.LENGTH_SHORT).show();
            mFavourites.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                    R.color.lightgreen));
        }
    }

    public void addCart_clicked(View view)
    {
        CartDialog cartDialog = new CartDialog();
        cartDialog.show(getSupportFragmentManager(), "Cart Dialog"); //opens a dialog to select item amounts
    }

    public void addComment_Clicked(View view)
    {
        CommentDialog commentDialog = new CommentDialog();
        commentDialog.show(getSupportFragmentManager(), "Commend Dialog"); //opens a dialog to enter comment
    }

    public void open_cart_clicked(View view)
    {
        Intent i = new Intent(GunDetails.this, ShoppingCart.class);
        startActivity(i);
    }

    private void add_cart(int amount)
    {
        for(int i=0; i<amount; i++)
        {
            databaseReferenceCart.push().setValue(curGun.getName()); //add the current gun to the shopping cart
        }

        Toast.makeText(this,  amount + " " + curGun.getName()
                + " added to cart", Toast.LENGTH_SHORT).show();
    }

    private void add_comment(String comment)
    {
        if(comment.length() > 50)
        {
            Toast.makeText(this, "Comment not posted, \nPlease write a shorter comment", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String commentWithUsername = singleton.getCurUsername() + " | " + comment; //concatenate the username with the comment they posted

            databaseReferenceGuns.child(curGunKey).child("comments").push().setValue(commentWithUsername); //add the comment

            Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void applyTexts(String amount) { //function that is inherited by CartDialog that retrieves data and perform tasks around it
        int i = Integer.parseInt(amount);
        add_cart(i);
    }

    @Override
    public void applyComments(String comment) { //function that is inherited by CommentDialog that retrieves data and perform tasks around it
        add_comment(comment);
    }

    @Override
    public void onStop() //disable all event listeners so that it doesn't trigger when data is changed in another activity
    {
        super.onStop();

        if(databaseReferenceFav != null && listenerFav != null)
        {
            databaseReferenceFav.removeEventListener(listenerFav);
        }

        if(databaseReferenceGuns != null && listenerGuns != null)
        {
            databaseReferenceGuns.removeEventListener(listenerGuns);
        }

        if(databaseReferenceComments != null && listenerComments != null)
        {
            databaseReferenceComments.removeEventListener(listenerComments);
        }
    }


}
