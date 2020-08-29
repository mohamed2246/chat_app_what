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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login_activity extends AppCompatActivity {
FirebaseUser current_user;
FirebaseAuth mfirebaseAuth;
Button mlogin_Button , msighn_up_button,mphone_button;
EditText musername,muserpassword;
ProgressDialog progressDialog;
DatabaseReference RootRef , user_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        mfirebaseAuth =FirebaseAuth.getInstance();
        intialfield();
        current_user= mfirebaseAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        user_ref = FirebaseDatabase.getInstance().getReference().child("Users");
        msighn_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotosignup();
            }
        });
        mphone_button = findViewById(R.id.phone_login_button);
        mlogin_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Allow_user_to_signin();
            }
        });
        mphone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendusertophoneactivity();
            }
        });

    }

    private void Allow_user_to_signin() {
        String email = musername.getText().toString();
        String password = muserpassword.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please Enter the Email.. ", Toast.LENGTH_SHORT).show();
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please Enter the Password.. ", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Sign In ");
            progressDialog.setMessage("Please Wait ...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            mfirebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String currentuserId = mfirebaseAuth.getUid();
                        String device_token = FirebaseInstanceId.getInstance().getToken();
                        user_ref.child(currentuserId).child("device_token").setValue(device_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    gotomainactivity();
                                    Toast.makeText(Login_activity.this, "Progrees Sucsses ", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });

                    } else {

                        String message = task.getException().getMessage();
                        Toast.makeText(Login_activity.this, "Error + " + message, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });




        }
    }

    private void gotomainactivity() {
        Intent intent = new Intent(Login_activity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    private void gotosignup() {
        Intent intent = new Intent(Login_activity.this,signup.class);
        startActivity(intent);
    }

    private void intialfield() {
        mlogin_Button = findViewById(R.id.login_button);
        msighn_up_button = findViewById(R.id.btSignUp);
        mphone_button =findViewById(R.id.phone_login_button);
        musername = findViewById(R.id.login_email);
        muserpassword = findViewById(R.id.password_email);
        progressDialog = new ProgressDialog(this);


    }



    private void sendusertologinactivity() {
        Intent intent = new Intent(Login_activity.this,MainActivity.class);
        startActivity(intent);
    }
    private void sendusertophoneactivity() {
        Intent intent = new Intent(Login_activity.this , Activity_phone_login.class);
        startActivity(intent);
    }
}
