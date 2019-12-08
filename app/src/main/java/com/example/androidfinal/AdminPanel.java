package com.example.androidfinal;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AdminPanel extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button mButtonChooseImage;
    private Button mUpload;
    private EditText mFileName;
    private EditText mClass;
    private EditText mPrice;
    private EditText mFireMode;
    private EditText mWeight;
    private EditText mDamage;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        castVar();
        setListeners();

    }

    private void castVar()
    {
        mButtonChooseImage = findViewById(R.id.bt_chooseFile);
        mUpload = findViewById(R.id.bt_upload);
        mFileName = findViewById(R.id.ed_file);
        mClass = findViewById(R.id.ed_class);
        mPrice = findViewById(R.id.ed_price);
        mFireMode = findViewById(R.id.ed_firingMode);
        mWeight = findViewById(R.id.ed_weight);
        mDamage = findViewById(R.id.ed_damage);
        mImageView = findViewById(R.id.iv_image);
        mProgressBar = findViewById(R.id.prog_bar);

        mStorageReference = FirebaseStorage.getInstance().getReference("Guns");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Guns");

    }

    private void setListeners()
    {
        //Choose image on click listener
        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        //Guns image on click listener
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
    }



    //this listener is called when you choose your file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.getData() != null)
        {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(mImageView);
        }
    }

    private void openFileChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile()
    {
        if(mImageUri != null)
        {
            if(!(mClass.getText().toString().isEmpty()) &&
                    !(mPrice.getText().toString().isEmpty()) &&
                    !(mWeight.getText().toString().isEmpty()) &&
                    !(mDamage.getText().toString().isEmpty()) &&
                    !(mFileName.getText().toString().isEmpty()) &&
                    !(mFireMode.getText().toString().isEmpty()))
            {
                StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(mImageUri));

                fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressBar.setProgress(0);
                                    }
                                }, 500);

                                int intPrice = Integer.parseInt(mPrice.getText().toString().trim());
                                int intWeight = Integer.parseInt(mWeight.getText().toString().trim());
                                int intDamage = Integer.parseInt(mDamage.getText().toString().trim());

                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();


                                while(!urlTask.isSuccessful());

                                Uri downloadUrl = urlTask.getResult();

                                Guns guns = new Guns(downloadUrl.toString(),
                                        mFileName.getText().toString().trim(),
                                        mClass.getText().toString().trim(), intPrice,
                                        mFireMode.getText().toString().trim(),intWeight,
                                        intDamage);

                                String uploadId = mDatabaseReference.push().getKey();
                                mDatabaseReference.child(uploadId).setValue(guns);

                                Toast.makeText(AdminPanel.this,
                                        "Upload Successful", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminPanel.this,
                                "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = 100.0 * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount();
                        mProgressBar.setProgress((int)progress);
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Please enter all the fields specified", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
