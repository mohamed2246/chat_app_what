package com.example.whatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewPager mviewPager;
    TabLayout mtabLayout;
    tabs_accessors_fragment tabs_accessors_fragment;
    FirebaseAuth firebaseAuth;
    DatabaseReference root;
    String currnt_user;
    String User_id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Whats app");
        mviewPager = findViewById(R.id.main_tabs_pager);
        mtabLayout = findViewById(R.id.main_tabs);
        tabs_accessors_fragment = new tabs_accessors_fragment(getSupportFragmentManager());
        mviewPager.setAdapter(tabs_accessors_fragment);
        mtabLayout.setupWithViewPager(mviewPager);
        User_id = firebaseAuth.getUid();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.Logout_option) {
            update_user_stutas("offline");
            firebaseAuth.signOut();
            sendusertologinactivity();

        }
        if (item.getItemId() == R.id.main_sitings_option) {

            gotosittings();
        }
        if (item.getItemId() == R.id.msin_find_friends) {
            gotoFindFriends();

        }

        if (item.getItemId() == R.id.main_create_group_option) {
            RequestNewGroup();

        }


        return true;

    }


    private void RequestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g Coding Cafe");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write Group Name...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Create_new_group(groupName ,User_id);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void Create_new_group(String group_name , String User_id) {
        root.child("Groups").child(User_id).child(group_name).setValue("Hallo").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(MainActivity.this, "Group Creating Successfully", Toast.LENGTH_SHORT).show();

                }
                else {

                    Toast.makeText(MainActivity.this, "Group Creating Failed", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser =firebaseAuth.getCurrentUser();
        if (currentuser == null) {
            sendusertologinactivity();
        } else {
            update_user_stutas("online");
            virryvy_user_exicting();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentuser =firebaseAuth.getCurrentUser();
        if (currentuser != null) {
            update_user_stutas("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentuser =firebaseAuth.getCurrentUser();
        if (currentuser != null) {
            update_user_stutas("offline");
        }
    }

    private void update_user_stutas (String state){
        String current_date, current_time;
      Calendar cal_date = Calendar.getInstance();
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd , yyyy");
      current_date = simpleDateFormat.format(cal_date.getTime());

      Calendar cal_time = Calendar.getInstance();
      SimpleDateFormat simpleTimeFormat2 = new SimpleDateFormat("hh,mm a");
      current_time = simpleTimeFormat2.format(cal_time.getTime());

      HashMap<String, Object> online_state = new HashMap<>();
      online_state.put("time", current_time);
      online_state.put("data", current_date);
      online_state.put("status", state);
      currnt_user = firebaseAuth.getCurrentUser().getUid();
      root.child("Users").child(currnt_user).child("user_state")
              .updateChildren(online_state);
  }

    private void virryvy_user_exicting() {
        String User_id = firebaseAuth.getCurrentUser().getUid();
        root.child("Users").child(User_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.hasChild("name"))) {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                } else {
                    gotosittings_mendatory();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void gotosittings_mendatory() {
        Intent intent = new Intent(MainActivity.this, settings.class);
        startActivity(intent);
    }

    private void gotosittings() {
        Intent intent = new Intent(MainActivity.this, settings.class);
        startActivity(intent);
    }


    private void sendusertologinactivity() {
        Intent intent = new Intent(MainActivity.this, Login_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void gotoFindFriends() {
        Intent intent = new Intent(MainActivity.this, Find_Friend_Activity.class);
        startActivity(intent);
    }

}
