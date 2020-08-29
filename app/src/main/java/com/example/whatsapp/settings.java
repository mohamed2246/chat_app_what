package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class settings extends AppCompatActivity {
    EditText userName, user_stuts;
    Button update;
    CircleImageView circleImageView;
    DatabaseReference root;
    FirebaseUser currentUse;
    FirebaseAuth firebaseAuth;
    String currentuserId;
    StorageReference user_profile_image_referance;
    ProgressDialog loading_dialog;
    int galary = 1;
    Toolbar mtollbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        firebaseAuth = FirebaseAuth.getInstance();
        mtollbar = findViewById(R.id.find_friends_tool_bar);
        currentuserId = firebaseAuth.getUid();
        root = FirebaseDatabase.getInstance().getReference();
        user_profile_image_referance = FirebaseStorage.getInstance().getReference().child("profile images");
        loading_dialog = new ProgressDialog(this);
        setSupportActionBar(mtollbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intiate();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uodate_account();
            }
        });
        Retreve_Use_details();

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galary_intent = new Intent();
                galary_intent.setAction(Intent.ACTION_GET_CONTENT);
                galary_intent.setType("image/*");
                startActivityForResult(galary_intent, galary);
            }
        });


    }



    private void Retreve_Use_details() {
        root.child("Users").child(currentuserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image")) {
                    String user_nam = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    String stats = dataSnapshot.child("status").getValue().toString();
                    userName.setText(user_nam);
                    user_stuts.setText(stats);

                    Picasso.get().load(image).into(circleImageView);




                }

                if (dataSnapshot.exists() && dataSnapshot.hasChild("name")) {
                    String user_nam = dataSnapshot.child("name").getValue().toString();
                    String stats = dataSnapshot.child("status").getValue().toString();
                    userName.setText(user_nam);
                    user_stuts.setText(stats);
                } else {

                    Toast.makeText(settings.this, "Please Update your profile information ... ", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galary && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            Picasso.get().load(uri).into(circleImageView);
            CropImage.activity(uri)
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {
                loading_dialog.setTitle("Set profile image");
                loading_dialog.setMessage("please wait... image is updating");
                loading_dialog.setCanceledOnTouchOutside(false);
                loading_dialog.show();
                Uri uri = result.getUri();
                StorageReference storageReference = user_profile_image_referance.child(currentuserId + ".jpg");
                storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Toast.makeText(settings.this, "Im heeeeeeeeeeeeere ..." , Toast.LENGTH_SHORT).show();
                        if (task.isSuccessful()) {
                            Toast.makeText(settings.this, "Profile Image Uploaded", Toast.LENGTH_SHORT).show();
                            String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                           // String downloadUrl=   task.getResult().getDownloadUrl().toString();
                            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    if (taskSnapshot.getMetadata() != null) {
                                        if (taskSnapshot.getMetadata().getReference() != null) {
                                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageUrl = uri.toString();
                                                    //createNewPost(imageUrl);
                                                    root.child("Users").child(currentuserId).child("image").setValue(imageUrl)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(settings.this, "Image saved in Database sucessfully ...", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        String message = task.getException().getMessage().toString();
                                                                        Toast.makeText(settings.this, "Image unsaved in Database faild ..." + message, Toast.LENGTH_SHORT).show();

                                                                    }


                                                                }
                                                            });


                                                    loading_dialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                }});


                        } else {
                            loading_dialog.dismiss();
                            String message;
                            message = task.getException().getMessage();
                            Toast.makeText(settings.this, "Failed Upload " + message, Toast.LENGTH_SHORT).show();
                        }
                    }

                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(settings.this, "Image unsaved in Database faild ..." + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                ;

            }

        }

    }

    private void Uodate_account() {
        String user_name = userName.getText().toString();
        String stuts = user_stuts.getText().toString();
        if (TextUtils.isEmpty(user_name)) {
            Toast.makeText(this, "Please Enter the User Name Please ...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(stuts)) {
            Toast.makeText(this, "Please Enter the Stuts Please ...", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", currentuserId);
            hashMap.put("name", user_name);
            hashMap.put("status", stuts);
            root.child("Users").child(currentuserId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(settings.this, "Profile Updating Successfully", Toast.LENGTH_SHORT).show();
                            gotomainactivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(settings.this, "Profile Not Updating " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }


    }


    private void Intiate() {
        userName = findViewById(R.id.set_progile_name);
        user_stuts = findViewById(R.id.set_progile_stuts);
        update = findViewById(R.id.Update);
        circleImageView = findViewById(R.id.set_profile_image);
    }

    private void gotomainactivity() {
        Intent intent = new Intent(settings.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

}
