package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class Find_Friend_Activity extends AppCompatActivity {
    Toolbar mtollbar;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__friend_);
        mtollbar = findViewById(R.id.find_friends_tool_bar);
        recyclerView = findViewById(R.id.rec_find_friens);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(mtollbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Find Friends");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<contacts> contactsFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<contacts>()
                .setQuery(databaseReference,contacts.class).build();
        FirebaseRecyclerAdapter<contacts, FindFriend_viewholder> adpter = new FirebaseRecyclerAdapter<contacts, FindFriend_viewholder>(contactsFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriend_viewholder holder, final int position, @NonNull contacts model) {
                holder.user_name.setText(model.getName());
                holder.status.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        Intent intent = new Intent(Find_Friend_Activity.this , profileActivity.class);
                        intent.putExtra("visit_user_id" ,visit_user_id );
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public FindFriend_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                return new FindFriend_viewholder(view);
            }
        };
        recyclerView.setAdapter(adpter);
        adpter.startListening();

    }

    public static class FindFriend_viewholder extends RecyclerView.ViewHolder {
        TextView user_name, status;
        ImageView image;

        FindFriend_viewholder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_profile_name);
            status = itemView.findViewById(R.id.user_stuts);
            image = itemView.findViewById(R.id.user_profile_image);

        }

    };
    

}
