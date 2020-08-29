package com.example.whatsapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class requests_fragment extends Fragment {

    View view;
    RecyclerView req_rec;
    DatabaseReference conReference, users_ref, ContactsRef;
    FirebaseAuth firebaseAuth;
    String user_id;

    public requests_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_requests_fragment, container, false);
        conReference = FirebaseDatabase.getInstance().getReference().child("chat requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        users_ref = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        req_rec = view.findViewById(R.id.request_rec);
        req_rec.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<contacts> contactsFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<contacts>
                ().setQuery(conReference.child(user_id), contacts.class).build();

        FirebaseRecyclerAdapter<contacts, view_holder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<contacts, view_holder>(contactsFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final view_holder holder, final int position, @NonNull contacts model) {
                holder.itemView.findViewById(R.id.accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.cancle_btn).setVisibility(View.VISIBLE);

                final String list_user_id = getRef(position).getKey();
                DatabaseReference get_rype_ref = getRef(position).child("request_type").getRef();
                get_rype_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String type = dataSnapshot.getValue().toString();
                            if (type.equals("received")) {

                                users_ref.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")) {
                                            final String req_user_name = dataSnapshot.child("name").getValue().toString();
                                            final String req_status = dataSnapshot.child("status").getValue().toString();
                                            final String req_profile_image = dataSnapshot.child("image").getValue().toString();
                                            holder.user_name.setText(req_user_name);
                                            holder.status.setText(req_status);
                                            Picasso.get().load(req_profile_image).placeholder(R.drawable.profile_image).into(holder.image);
                                        }
                                        else if (!dataSnapshot.hasChild("image")) {
                                            final String req_user_name = dataSnapshot.child("name").getValue().toString();
                                            final String req_status = dataSnapshot.child("status").getValue().toString();

                                            holder.user_name.setText(req_user_name);
                                            holder.status.setText(req_status);
                                        }

                                        final String req_user_name = dataSnapshot.child("name").getValue().toString();
                                        final String req_status = dataSnapshot.child("status").getValue().toString();

                                        holder.user_name.setText(req_user_name);
                                        holder.status.setText("Want TO Connect With You");

                                        holder.accept_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ContactsRef.child(user_id).child(list_user_id).child("Contacts")
                                                        .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            ContactsRef.child(list_user_id).child(user_id).child("Contacts")
                                                                    .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        conReference.child(user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    conReference.child(list_user_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                Toast.makeText(getContext(), " New Contact Saved ", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        });

                                        holder.cancle_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                conReference.child(user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            conReference.child(list_user_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getContext(), " The Request Is Deleted ", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else if (type.equals("sent")){
                                Button req_sent = holder.itemView.findViewById(R.id.cancle_btn);
                                req_sent.setText("Delete Req");
                                holder.itemView.findViewById(R.id.accept_btn).setVisibility(View.INVISIBLE);


                                users_ref.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")) {
                                            final String req_user_name = dataSnapshot.child("name").getValue().toString();
                                            final String req_status = dataSnapshot.child("status").getValue().toString();
                                            final String req_profile_image = dataSnapshot.child("image").getValue().toString();
                                            holder.user_name.setText(req_user_name);
                                            holder.status.setText(req_status);
                                            Picasso.get().load(req_profile_image).placeholder(R.drawable.profile_image).into(holder.image);
                                        }
                                        else if (!dataSnapshot.hasChild("image")) {
                                            final String req_user_name = dataSnapshot.child("name").getValue().toString();
                                            final String req_status = dataSnapshot.child("status").getValue().toString();

                                            holder.user_name.setText(req_user_name);
                                            holder.status.setText(req_status);
                                        }

                                        final String req_user_name = dataSnapshot.child("name").getValue().toString();
                                        final String req_status = dataSnapshot.child("status").getValue().toString();

                                        holder.user_name.setText(req_user_name);
                                        holder.status.setText("You Sent Request to " + req_user_name );

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                holder.cancle_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        conReference.child(user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    conReference.child(list_user_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getContext(), " The Request Is Deleted ", Toast.LENGTH_SHORT).show();
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
                return new requests_fragment.view_holder(view);
            }
        };
        req_rec.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class view_holder extends RecyclerView.ViewHolder {
        TextView user_name, status;
        ImageView image;
        Button accept_btn, cancle_btn;

        public view_holder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_profile_name);
            status = itemView.findViewById(R.id.user_stuts);
            image = itemView.findViewById(R.id.user_profile_image);
            accept_btn = itemView.findViewById(R.id.accept_btn);
            cancle_btn = itemView.findViewById(R.id.cancle_btn);

        }
    }

}
