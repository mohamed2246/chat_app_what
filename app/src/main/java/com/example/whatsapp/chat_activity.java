package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class chat_activity extends AppCompatActivity {
    String recieve_id;
    String recieve_name, recieved_image;
    Toolbar mtollbar;
    TextView user_name, user_last_seen;
    CircleImageView user_image;
    ImageButton send_mes_btn, send_files_btn;
    EditText message_input_text;
    FirebaseAuth firebaseAuth;
    String user_id;
    DatabaseReference root_ref;
    List<Messages> messagesList = new ArrayList<>();
    RecyclerView recycle_view;
    LinearLayoutManager linearLayoutManager;
    message_adapter message_adapter;
    int count = 0;
    String current_date, current_time;
    String cheacker, mu_url = "";
    Uri fileUri;
    StorageTask UploadTask;
    ProgressDialog loading_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);
        firebaseAuth = FirebaseAuth.getInstance();
        root_ref = FirebaseDatabase.getInstance().getReference();
        user_id = firebaseAuth.getCurrentUser().getUid();

        recieve_name = Objects.requireNonNull(getIntent().getExtras()).getString("name");

        recieve_id = getIntent().getExtras().getString("friend_id");
        Toast.makeText(this, recieve_id, Toast.LENGTH_SHORT).show();
        recieved_image = getIntent().getExtras().getString("image");
        Intial_controllers();


        user_name.setText(recieve_name);
        Picasso.get().load(recieved_image).placeholder(R.drawable.profile_image).into(user_image);

        send_mes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_message();
            }
        });
        send_files_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_files();
            }
        });
        message_adapter = new message_adapter(messagesList, this);
        recycle_view = findViewById(R.id.message_rec_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recycle_view.setLayoutManager(linearLayoutManager);
        recycle_view.setAdapter(message_adapter);
        recycle_view.getRecycledViewPool().setMaxRecycledViews(0, 0);

        display_last_seen();

        send_files_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{

                        "Images",
                        "PDF Files",
                        "Ms Word Files"
                };


                final AlertDialog.Builder builder = new AlertDialog.Builder(chat_activity.this);
                builder.setTitle("Select the File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            cheacker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select Image"), 438);
                        } else if (which == 1) {
                            cheacker = "pdf";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent, "Select PDF file "), 438);


                        } else if (which == 2) {
                            cheacker = "docx";
                            cheacker = "pdf";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent, "Select MS word file "), 438);
                        }

                    }
                });
                builder.show();
            }
        });

        messagesList.clear();
        root_ref.child("Messages").child(user_id).child(recieve_id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        message_adapter.notifyDataSetChanged();
                        recycle_view.smoothScrollToPosition(Objects.requireNonNull(recycle_view.getAdapter()).getItemCount());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            loading_dialog.setTitle("Sending File");
            loading_dialog.setMessage("please wait... we are sending this file ");
            loading_dialog.setCanceledOnTouchOutside(false);
            loading_dialog.show();
            fileUri = data.getData();
            if (!cheacker.equals("image")) {
                /*StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
                final String message_sender_ref = "Messages/" + user_id + "/" + recieve_id;
                final String message_reciever_ref = "Messages/" + recieve_id + "/" + user_id;
                DatabaseReference user_message_key = root_ref.child("Messages").child(message_sender_ref).child(message_reciever_ref).push();
                final String message_push_id = user_message_key.getKey();
                final StorageReference filepath = storageReference.child(message_push_id + "." + "jpg");
                filepath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.storage.UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Map message_text_body = new HashMap();
                            message_text_body.put("message", task.getResult().getStorage().getDownloadUrl().toString());
                            message_text_body.put("name", fileUri.getLastPathSegment());
                            message_text_body.put("type", cheacker);
                            message_text_body.put("from", user_id);
                            message_text_body.put("to", recieve_id);
                            message_text_body.put("message_id", message_push_id);
                            message_text_body.put("time", current_time);
                            message_text_body.put("date", current_date);


                            Map message_body_details = new HashMap();
                            message_body_details.put(message_sender_ref + "/" + message_push_id, message_text_body);
                            message_body_details.put(message_reciever_ref + "/" + message_push_id, message_text_body);
                            root_ref.updateChildren(message_body_details);
                            loading_dialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading_dialog.dismiss();
                        Toast.makeText(chat_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                     double p = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                     loading_dialog.setMessage((int) p + "% Uploading....");
                    }
                });*/

                //////////////////////////////////////////////////////////////
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
                final String message_sender_ref = "Messages/" + user_id + "/" + recieve_id;
                final String message_reciever_ref = "Messages/" + recieve_id + "/" + user_id;
                DatabaseReference user_message_key = root_ref.child("Messages").child(message_sender_ref).child(message_reciever_ref).push();
                final String message_push_id = user_message_key.getKey();
                final StorageReference filepath = storageReference.child(message_push_id + "." + cheacker);

                filepath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();

                                Map message_text_body = new HashMap();
                                message_text_body.put("message", downloadUrl);
                                message_text_body.put("name", fileUri.getLastPathSegment());
                                message_text_body.put("type", cheacker);
                                message_text_body.put("from", user_id);
                                message_text_body.put("to", recieve_id);
                                message_text_body.put("message_id", message_push_id);
                                message_text_body.put("time", current_time);
                                message_text_body.put("date", current_date);


                                Map message_body_details = new HashMap();
                                message_body_details.put(message_sender_ref + "/" + message_push_id, message_text_body);
                                message_body_details.put(message_reciever_ref + "/" + message_push_id, message_text_body);
                                root_ref.updateChildren(message_body_details);
                                loading_dialog.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loading_dialog.dismiss();
                                Toast.makeText(chat_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100.0* taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        loading_dialog .setMessage((int) p + " % Uploading...");
                    }
                });


///////////////////////////////////////////////////////////////////////////////////////////
            } else if (cheacker.equals("image")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image Files");
                final String message_sender_ref = "Messages/" + user_id + "/" + recieve_id;
                final String message_reciever_ref = "Messages/" + recieve_id + "/" + user_id;
                DatabaseReference user_message_key = root_ref.child("Messages").child(message_sender_ref).child(message_reciever_ref).push();
                final String message_push_id = user_message_key.getKey();

                final StorageReference filepath = storageReference.child(message_push_id + "." + "jpg");
                UploadTask = filepath.putFile(fileUri);
                UploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }


                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri uridownload_uri = task.getResult();
                            mu_url = uridownload_uri.toString();

                            Map message_text_body = new HashMap();
                            message_text_body.put("message", mu_url);
                            message_text_body.put("name", fileUri.getLastPathSegment());
                            message_text_body.put("type", cheacker);
                            message_text_body.put("from", user_id);
                            message_text_body.put("to", recieve_id);
                            message_text_body.put("message_id", message_push_id);
                            message_text_body.put("time", current_time);
                            message_text_body.put("date", current_date);


                            Map message_body_details = new HashMap();
                            message_body_details.put(message_sender_ref + "/" + message_push_id, message_text_body);
                            message_body_details.put(message_reciever_ref + "/" + message_push_id, message_text_body);

                            root_ref.updateChildren(message_body_details).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(chat_activity.this, "Send sucessfully ", Toast.LENGTH_SHORT).show();
                                        loading_dialog.dismiss();

                                    } else {

                                        Toast.makeText(chat_activity.this, "Error ", Toast.LENGTH_SHORT).show();
                                        loading_dialog.dismiss();
                                    }
                                    message_input_text.setText("");
                                }
                            });


                        }
                        loading_dialog.dismiss();
                    }
                });
            } else {

                Toast.makeText(this, "No thing Selected , Error  ", Toast.LENGTH_SHORT).show();
                loading_dialog.dismiss();

            }
        }


    }

    private void send_files() {


    }

    void display_last_seen() {
        root_ref.child("Users").child(recieve_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("user_state").hasChild("status")) {
                    String date = dataSnapshot.child("user_state").child("data").getValue().toString();
                    String time = dataSnapshot.child("user_state").child("time").getValue().toString();
                    String state = dataSnapshot.child("user_state").child("status").getValue().toString();
                    if (state.equals("online")) {
                        user_last_seen.setText("online");
                    } else if (state.equals("offline")) {

                        user_last_seen.setText("Last seen : " + "\n" + time + "      " + date);
                    }


                } else {

                    user_last_seen.setText("Offline");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void send_message() {
        String message_text = message_input_text.getText().toString();
        if (TextUtils.isEmpty(message_text)) {
            Toast.makeText(this, "First Write Your Message", Toast.LENGTH_SHORT).show();
        } else {

            String message_sender_ref = "Messages/" + user_id + "/" + recieve_id;
            String message_reciever_ref = "Messages/" + recieve_id + "/" + user_id;
            DatabaseReference user_message_key = root_ref.child("Messages").child(message_sender_ref).child(message_reciever_ref).push();
            final String message_push_id = user_message_key.getKey();
            Map message_text_body = new HashMap();
            message_text_body.put("message", message_text);
            message_text_body.put("type", "text");
            message_text_body.put("from", user_id);
            message_text_body.put("to", recieve_id);
            message_text_body.put("message_id", message_push_id);
            message_text_body.put("time", current_time);
            message_text_body.put("date", current_date);


            Map message_body_details = new HashMap();
            message_body_details.put(message_sender_ref + "/" + message_push_id, message_text_body);
            message_body_details.put(message_reciever_ref + "/" + message_push_id, message_text_body);

            root_ref.updateChildren(message_body_details).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(chat_activity.this, "Send sucessfully ", Toast.LENGTH_SHORT).show();

                     DatabaseReference mes_req =  root_ref.child("message_req").child(recieve_id).child(message_push_id).child("from");
                     mes_req.setValue(user_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(chat_activity.this, "dend request done", Toast.LENGTH_SHORT).show();
                                }
                         }
                     });



                    } else {

                        Toast.makeText(chat_activity.this, "Error ", Toast.LENGTH_SHORT).show();
                    }
                    message_input_text.setText("");
                }
            });


        }
    }

    private void Intial_controllers() {

        mtollbar = findViewById(R.id.find_friends_tool_bar);
        setSupportActionBar(mtollbar);
        ActionBar acation_bar = getSupportActionBar();
        acation_bar.setDisplayShowCustomEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        acation_bar.setCustomView(action_bar_view);
        user_image = findViewById(R.id.custom_profile_image);
        user_last_seen = findViewById(R.id.custom_profile_last_seen);
        user_name = findViewById(R.id.custom_profile_name);
        send_mes_btn = findViewById(R.id.send_message);
        send_files_btn = findViewById(R.id.send_files_byn);
        message_input_text = findViewById(R.id.input_chat_message);
        Calendar cal_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd , yyyy");
        current_date = simpleDateFormat.format(cal_date.getTime());

        Calendar cal_time = Calendar.getInstance();
        SimpleDateFormat simpleTimeFormat2 = new SimpleDateFormat("hh,mm a");
        current_time = simpleTimeFormat2.format(cal_time.getTime());
        loading_dialog = new ProgressDialog(this);

    }
}
