package com.example.whatsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class message_adapter extends RecyclerView.Adapter<message_adapter.message_view_holder> {
    List<Messages> messagesList = new ArrayList<>();
    FirebaseAuth fireAuth;
    DatabaseReference user_ref;
    Context context;

    public message_adapter(List<Messages> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
    }

    public message_adapter(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public message_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout, parent, false);
        fireAuth = FirebaseAuth.getInstance();
        return new message_view_holder(view);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull final message_view_holder holder, final int position) {
        String user_id = fireAuth.getCurrentUser().getUid();
        Messages messages = messagesList.get(position);
        String from_user_id = messages.getFrom();
        String from_message_type = messages.getType();
        user_ref = FirebaseDatabase.getInstance().getReference("Users").child(from_user_id);
        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")) {
                    String recieve_image = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(recieve_image).placeholder(R.drawable.profile_image).into(holder.reciever_profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.reciever_message_text.setVisibility(View.GONE);
        holder.reciever_profile_image.setVisibility(View.GONE);

        holder.message_reciever_picture.setVisibility(View.GONE);
        holder.message_sender_picture.setVisibility(View.GONE);

        if (from_message_type.equals("text")) {

            if (from_user_id.equals(user_id)) {

                holder.sender_mes_text.setBackgroundResource(R.drawable.sender_message_layout);
                holder.sender_mes_text.setVisibility(View.VISIBLE);
                holder.reciever_message_text.setTextColor(R.color.white);
                holder.sender_mes_text.setText(messages.getMessage() + "\n" + messages.getTime());
                holder.reciever_message_text.setVisibility(View.INVISIBLE);

            } else {
                holder.sender_mes_text.setVisibility(View.INVISIBLE);
                holder.reciever_profile_image.setVisibility(View.VISIBLE);
                holder.reciever_message_text.setVisibility(View.VISIBLE);

                holder.reciever_message_text.setBackgroundResource(R.drawable.reciever_message_layout);
                holder.reciever_message_text.setTextColor(R.color.white);
                holder.reciever_message_text.setText(messages.getMessage() + "\n" + messages.getTime());


            }
        } else if (from_message_type.equals("image")) {
            if (from_user_id.equals(user_id)) {
                holder.sender_mes_text.setVisibility(View.GONE);
                holder.reciever_message_text.setVisibility(View.GONE);
                holder.message_sender_picture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.message_sender_picture);
            } else {
                holder.sender_mes_text.setVisibility(View.GONE);
                holder.reciever_message_text.setVisibility(View.GONE);
                holder.message_reciever_picture.setVisibility(View.VISIBLE);
                holder.reciever_profile_image.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.message_reciever_picture);
            }

        } else {

            if (from_user_id.equals(user_id)) {
                holder.sender_mes_text.setVisibility(View.GONE);
                holder.reciever_message_text.setVisibility(View.GONE);
                holder.message_sender_picture.setVisibility(View.VISIBLE);
                holder.message_sender_picture.setBackgroundResource(R.drawable.file);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messagesList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });

            } else {
                holder.sender_mes_text.setVisibility(View.GONE);
                holder.reciever_message_text.setVisibility(View.GONE);
                holder.message_reciever_picture.setVisibility(View.VISIBLE);
                holder.reciever_profile_image.setVisibility(View.VISIBLE);
                holder.message_reciever_picture.setBackgroundResource(R.drawable.file);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messagesList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    class message_view_holder extends RecyclerView.ViewHolder {

        TextView sender_mes_text, reciever_message_text;
        CircleImageView reciever_profile_image;
        ImageView message_sender_picture, message_reciever_picture;

        public message_view_holder(@NonNull View itemView) {
            super(itemView);
            sender_mes_text = itemView.findViewById(R.id.sender_message_text);
            reciever_message_text = itemView.findViewById(R.id.reciever_message_text);
            reciever_profile_image = itemView.findViewById(R.id.message_profile_image);
            message_sender_picture = itemView.findViewById(R.id.message_sender_image_view);
            message_reciever_picture = itemView.findViewById(R.id.message_reciever_image_view);

        }
    }

}
