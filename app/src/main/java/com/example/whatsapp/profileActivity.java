package com.example.whatsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileActivity extends AppCompatActivity {
    String recive_user_id, sender_id, current_state;
    DatabaseReference databaseReference, chat_request_ref, Contacts_ref, notification_ref;
    CircleImageView profile_image;
    TextView profile_name, prifile_stutas;
    Button send_message, Decline_message_btn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        sender_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        Contacts_ref = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notification_ref = FirebaseDatabase.getInstance().getReference().child("Notifications");
        chat_request_ref = FirebaseDatabase.getInstance().getReference().child("chat requests");
        recive_user_id = getIntent().getExtras().get("visit_user_id").toString();


        Intiate();
        Retrive_user_info();

    }

    private void Retrive_user_info() {
        databaseReference.child(recive_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && (dataSnapshot.hasChild("image"))) {

                    String User_image = dataSnapshot.child("image").getValue().toString();
                    String User_name = dataSnapshot.child("name").getValue().toString();
                    String User_status = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(User_image).placeholder(R.drawable.profile_image).into(profile_image);
                    profile_name.setText(User_name);
                    prifile_stutas.setText(User_status);
                    current_state = "new";
                    mange_chat_request();
                } else {

                    String User_name = dataSnapshot.child("name").getValue().toString();
                    String User_status = dataSnapshot.child("status").getValue().toString();
                    profile_name.setText(User_name);
                    prifile_stutas.setText(User_status);
                    current_state = "new";
                    mange_chat_request();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mange_chat_request() {

        chat_request_ref.child(sender_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(recive_user_id)) {

                    final String request_type = dataSnapshot.child(recive_user_id).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {
                        send_message.setText("Cancle Request");
                        current_state = "request_sent";

                    } else if (request_type.equals("received")) {
                        current_state = "request_received";
                        send_message.setText("Accept Chat Request");
                        Decline_message_btn.setVisibility(View.VISIBLE);
                        Decline_message_btn.setEnabled(true);
                        Decline_message_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Remove_request();
                            }
                        });
                    }

                } else {
                    Contacts_ref.child(sender_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(recive_user_id)) {
                                current_state = "friends";
                                send_message.setText("Remove this contacts ");


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (!sender_id.equals(recive_user_id)) {

            send_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send_message.setEnabled(false);
                    if (current_state.equals("new")) {
                        send_request();

                    }

                    if (current_state.equals("request_sent")) {
                        Remove_request();
                    }

                    if (current_state.equals("request_received")) {
                        Accept_chat_request();
                    }

                    if (current_state.equals("friends")) {
                        Contacts_ref.child(sender_id).child(recive_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Contacts_ref.child(recive_user_id).child(sender_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                send_message.setEnabled(true);
                                                current_state = "new";
                                                send_message.setText("Send Message");
                                                Decline_message_btn.setEnabled(false);
                                                Decline_message_btn.setVisibility(View.INVISIBLE);


                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });

        } else {

            send_message.setVisibility(View.INVISIBLE);


        }
    }

    private void Accept_chat_request() {
        Contacts_ref.child(sender_id).child(recive_user_id).child("Contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Contacts_ref.child(recive_user_id).child(sender_id).child("Contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            chat_request_ref.child(sender_id).child(recive_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        chat_request_ref.child(recive_user_id).child(sender_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    send_message.setEnabled(true);
                                                                    send_message.setText("Remove this contacts");
                                                                    current_state = "friends";
                                                                    Decline_message_btn.setVisibility(View.INVISIBLE);
                                                                    Decline_message_btn.setEnabled(false);

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    });
                        }
                    }
                });

    }

    private void Remove_request() {
        chat_request_ref.child(sender_id).child(recive_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    chat_request_ref.child(recive_user_id).child(sender_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                send_message.setEnabled(true);
                                current_state = "new";
                                send_message.setText("Send Message");
                                Decline_message_btn.setEnabled(false);
                                Decline_message_btn.setVisibility(View.INVISIBLE);


                            }
                        }
                    });
                }
            }
        });
    }

    private void send_request() {
        chat_request_ref.child(sender_id).child(recive_user_id).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chat_request_ref.child(recive_user_id).child(sender_id).child("request_type")
                                    .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    HashMap<String, String> chat_notification_map = new HashMap<>();
                                    chat_notification_map.put("from", sender_id);
                                    chat_notification_map.put("type", "request");
                                    notification_ref.child(recive_user_id).push().setValue(chat_notification_map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        send_message.setEnabled(true);
                                                        current_state = "request_sent";
                                                        send_message.setText("Cancle Request");
                                                    }
                                                }
                                            });

                                }
                            });

                        }
                    }
                });


    }


    private void Intiate() {
        Decline_message_btn = findViewById(R.id.decline_message_request);
        profile_image = findViewById(R.id.visit_profile_image);
        profile_name = findViewById(R.id.visit_profile_name);
        prifile_stutas = findViewById(R.id.visit_profile_stuts);
        send_message = findViewById(R.id.Send_message);
    }
}
