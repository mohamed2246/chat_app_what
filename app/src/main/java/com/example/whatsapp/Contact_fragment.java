package com.example.whatsapp;


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
public class Contact_fragment extends Fragment {
    View view;
    RecyclerView contacts_recy;
    DatabaseReference contacts_ref , users_ref;
    FirebaseAuth  firebaseAuth;
    String user_id ;
    ImageView online_status;
    public Contact_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_fragment, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        contacts_ref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(user_id);
        users_ref = FirebaseDatabase.getInstance().getReference().child("Users");
        contacts_recy = view.findViewById(R.id.Contacts_rec);
        contacts_recy.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<contacts> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<contacts>().setQuery(contacts_ref,contacts.class).build();
        FirebaseRecyclerAdapter <contacts,contacts_viewHolder> adapter = new FirebaseRecyclerAdapter<contacts, contacts_viewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final contacts_viewHolder holder, int position, @NonNull final contacts model) {
                    String users_id = getRef(position).getKey();
                    users_ref.child(users_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           if (dataSnapshot.exists()){

                               if (dataSnapshot.child("user_state").hasChild("status")){
                                   String date = dataSnapshot.child("user_state").child("data").getValue().toString();
                                   String time = dataSnapshot.child("user_state").child("time").getValue().toString();
                                   String state = dataSnapshot.child("user_state").child("status").getValue().toString();
                                   if ( state.equals("online")  ){
                                       holder.online_status.setVisibility(View.VISIBLE);
                                   }
                                   else if (state.equals("offline")){

                                       holder.online_status.setVisibility(View.INVISIBLE);
                                   }


                               }
                               else {
                                   holder.online_status.setVisibility(View.INVISIBLE);
                               }

                               if (dataSnapshot.hasChild("image")){
                                   holder.user_name.setText(dataSnapshot.child("name").getValue().toString());
                                   holder.status.setText(dataSnapshot.child("status").getValue().toString());
                                   Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.profile_image).into(holder.image);
                               }
                               else {

                                   holder.user_name.setText(dataSnapshot.child("name").getValue().toString());
                                   holder.status.setText(dataSnapshot.child("status").getValue().toString());

                               }
                           }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            }

            @NonNull
            @Override
            public contacts_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                contacts_viewHolder contacts_viewHolder = new contacts_viewHolder(view);
                return contacts_viewHolder;
            }

        };

            contacts_recy.setAdapter(adapter);
            adapter.startListening();
    }

    public static class contacts_viewHolder extends RecyclerView.ViewHolder {
        TextView user_name, status;
        ImageView image;
        ImageView online_status;
        public contacts_viewHolder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_profile_name);
            status = itemView.findViewById(R.id.user_stuts);
            image = itemView.findViewById(R.id.user_profile_image);
            online_status = itemView.findViewById(R.id.user_online_status);

        }
    }

}
