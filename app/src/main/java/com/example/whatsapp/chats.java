package com.example.whatsapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class chats extends Fragment {

    View view;
    RecyclerView recyclerView;
    DatabaseReference chat_ref, user_ref;
    FirebaseAuth firebaseAuth;
    String user_id;
    String image_url = "defult_id";

    public chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        chat_ref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(user_id);
        user_ref = FirebaseDatabase.getInstance().getReference().child("Users");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<contacts> contactsFirebaseRecyclerOptions = new
                FirebaseRecyclerOptions.Builder<contacts>().setQuery(chat_ref, contacts.class).build();

        FirebaseRecyclerAdapter<contacts, view_holder> adapter = new FirebaseRecyclerAdapter<contacts, view_holder>(contactsFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final view_holder holder, final int position, @NonNull contacts model) {
                String Users_ids = getRef(position).getKey();
                final String[] retImage = {"default_image"};
                user_ref.child(Users_ids).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            if (dataSnapshot.hasChild("image")) {

                                retImage[0] = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(retImage[0]).into(holder.image);
                            }

                            final String name = dataSnapshot.child("name").getValue().toString();
                            String stutas = dataSnapshot.child("status").getValue().toString();
                            holder.user_name.setText(name);
                            holder.status.setText("Last seen : " + "\n"+ "Time" +"Date "  );

                            if (dataSnapshot.child("user_state").hasChild("status")){
                                String date = dataSnapshot.child("user_state").child("data").getValue().toString();
                                String time = dataSnapshot.child("user_state").child("time").getValue().toString();
                                String state = dataSnapshot.child("user_state").child("status").getValue().toString();
                                if ( state.equals("online")  ){
                                    holder.status.setText("online");
                                }
                                else if (state.equals("offline")){

                                    holder.status.setText("Last seen : " + "\n"+time  + "   " +date);
                                }


                            }
                            else {

                                holder.status.setText("Offline");
                            }



                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String friend_id = getRef(position).getKey();
                                    Intent intent = new Intent(getContext() , chat_activity.class);
                                    intent.putExtra("friend_id" , friend_id);
                                    intent.putExtra("name",name);
                                    intent.putExtra("image" , retImage[0]);
                                    startActivity(intent);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }

            @NonNull
            @Override
            public view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                view_holder contacts_viewHolder = new view_holder(view);
                return contacts_viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    class view_holder extends RecyclerView.ViewHolder {
        TextView user_name, status;
        ImageView image;

        public view_holder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_profile_name);
            status = itemView.findViewById(R.id.user_stuts);
            image = itemView.findViewById(R.id.user_profile_image);
        }
    }
}
