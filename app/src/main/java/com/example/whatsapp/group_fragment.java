package com.example.whatsapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class group_fragment extends Fragment {
    View view;
    ListView listView;
    ArrayAdapter<String> arrayAdapter ;
    ArrayList <String> arrayList = new ArrayList<>();
    DatabaseReference Group_ref;
    FirebaseAuth firebaseAuth;
    String user_id;



    public group_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_group_fragment, container, false);
        Intiate();
        Group_ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(user_id);
        Display_and_retrive_group();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String group_name = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent(getContext(),activity_group_chat.class);
                intent.putExtra("group_name",group_name);
                startActivity(intent);
            }
        });

        return view;
    }

    private void Display_and_retrive_group() {

        Group_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){

                    set.add(((DataSnapshot) iterator.next()).getKey());
                }
                arrayList.clear();
                arrayList.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Intiate() {
        listView = view.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_activated_1,arrayList);
        listView.setAdapter(arrayAdapter);
        firebaseAuth =FirebaseAuth.getInstance();
        user_id = firebaseAuth.getUid();
    }

}
