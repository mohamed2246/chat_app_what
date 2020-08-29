package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class activity_group_chat extends AppCompatActivity {
    private Toolbar mtoolbar;
    ImageButton Send_message_button;
    EditText user_Message_Input;
    ScrollView scrollView;
    TextView Despaly_text_message;
    String current_group_name, username, userid, current_date, current_time;
    FirebaseAuth firebaseAuth;
    DatabaseReference userReference, group_name_referance, group_message_key_referance;
    String message_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        current_group_name = getIntent().getExtras().get("group_name").toString();
        Toast.makeText(this, current_group_name, Toast.LENGTH_SHORT).show();
        Intiate();

        firebaseAuth = FirebaseAuth.getInstance();
        userid = firebaseAuth.getCurrentUser().getUid().toString();

        getuserInfo();

        Send_message_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save_message_info_to_database();
                user_Message_Input.setText("");
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        group_name_referance.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Display_messages(dataSnapshot);
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Display_messages(dataSnapshot);
                }
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

    private void Display_messages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            String chat_data = (String) ((DataSnapshot) iterator.next()).getValue();
            String chat_message = (String) ((DataSnapshot) iterator.next()).getValue();
            String chat_name = (String) ((DataSnapshot) iterator.next()).getValue();
            String chat_time = (String) ((DataSnapshot) iterator.next()).getValue();
            Despaly_text_message.append(chat_name + " :\n" + chat_message + "\n" + chat_time + "     " + chat_data + "\n\n\n" );
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }


    private void Intiate() {
        mtoolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(current_group_name);
        Send_message_button = findViewById(R.id.send_message_button);
        user_Message_Input = findViewById(R.id.input_group_message);
        Despaly_text_message = findViewById(R.id.group_chat_text_display);
        scrollView = findViewById(R.id.my_scroll_view);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        group_name_referance = FirebaseDatabase.getInstance().getReference().child("Groups").child(current_group_name);

    }

    private void getuserInfo() {

        userReference.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Save_message_info_to_database() {
        String message = user_Message_Input.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please Write Message", Toast.LENGTH_SHORT).show();
        } else {
            Calendar cal_date = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd , yyyy");
            current_date = simpleDateFormat.format(cal_date.getTime());

            Calendar cal_time = Calendar.getInstance();
            SimpleDateFormat simpleTimeFormat2 = new SimpleDateFormat("hh,mm a");
            current_time = simpleTimeFormat2.format(cal_time.getTime());
            message_key = group_name_referance.push().getKey();

            HashMap<String, Object> group_message_key = new HashMap<>();
            group_name_referance.updateChildren(group_message_key);
            group_message_key_referance = group_name_referance.child(message_key);

            HashMap<String, Object> message_info_map = new HashMap<>();
            message_info_map.put("name", username);
            message_info_map.put("message", message);
            message_info_map.put("date", current_date);
            message_info_map.put("time", current_time);
            group_message_key_referance.updateChildren(message_info_map);


        }

    }


}
