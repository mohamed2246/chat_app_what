package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class signup extends AppCompatActivity {
    Button mcreate_Button, magain_login_button;
    EditText musername, muserpassword;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String firebaseUser;
    DatabaseReference root;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        root = FirebaseDatabase.getInstance().getReference();
        intialfield();
        mcreate_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAcount();
            }
        });

        magain_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotosignin();

            }
        });


    }

    private void createAcount() {
        String email = musername.getText().toString();
        String password = muserpassword.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please Enter the Email.. ", Toast.LENGTH_SHORT).show();
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please Enter the Password.. ", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Creating New Account");
            progressDialog.setMessage("Please wait , while we are creating new account for you ...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String device_token = FirebaseInstanceId.getInstance().getToken();
                                root.child("Users").child(firebaseUser).setValue("");
                                root.child("Users").child(firebaseUser).child("device_token").setValue(device_token);
                                Toast.makeText(signup.this, "The Account is created", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                gotomainactivity();
                            } else {

                                String message = task.getException().getMessage();
                                Toast.makeText(signup.this, "Error + " + message, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });


        }


    }

    private void gotomainactivity() {
        Intent intent = new Intent(signup.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    private void gotosignin() {
        Intent intent = new Intent(signup.this, Login_activity.class);
        startActivity(intent);
    }

    private void intialfield() {
        mcreate_Button = findViewById(R.id.create_btn_acoount);
        magain_login_button = findViewById(R.id.login_from_signup);
        musername = findViewById(R.id.email_login);
        muserpassword = findViewById(R.id.email_password);

        progressDialog =new ProgressDialog(this);

    }

}
