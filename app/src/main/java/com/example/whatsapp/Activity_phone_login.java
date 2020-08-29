package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Activity_phone_login extends AppCompatActivity {
    /*Button send_ver_code_btn, verfy_btn;
    EditText input_phone_num, input_verifycation_code;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth mAuth;
    ProgressDialog loadingbar;*/
    private EditText InputUserPhoneNumber, InputUserVerificationCode;
    private Button SendVerificationCodeButton, VerifyButton;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        //correct solution comment///////////////////////////////////////////////
        /*intiate();
        send_ver_code_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = input_phone_num.getText().toString();
                if (TextUtils.isEmpty(phone)) {

                    Toast.makeText(Activity_phone_login.this, "Phone number is required...", Toast.LENGTH_SHORT).show();
                } else {

                    loadingbar.setTitle("Phone Verification");
                    loadingbar.setMessage("Please wait while we are authetication your phone ");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phone,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            Activity_phone_login.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks


                }

            }
        });


        verfy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_ver_code_btn.setVisibility(View.INVISIBLE);
                input_phone_num.setVisibility(View.INVISIBLE);

                String verification_code = input_verifycation_code.getText().toString();
                if (TextUtils.isEmpty(verification_code)) {

                    Toast.makeText(Activity_phone_login.this, "verification code is required...", Toast.LENGTH_SHORT).show();
                } else {
                    loadingbar.setTitle("verification code");
                    loadingbar.setMessage("Please wait while we are authetication verification code ");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verification_code);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Activity_phone_login.this, "Invalid phone number ... please enter correct phone number with your country code ...  ", Toast.LENGTH_SHORT).show();
                send_ver_code_btn.setVisibility(View.VISIBLE);
                input_phone_num.setVisibility(View.VISIBLE);
                verfy_btn.setVisibility(View.INVISIBLE);
                input_verifycation_code.setVisibility(View.INVISIBLE);
                loadingbar.dismiss();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                loadingbar.dismiss();
                // Save verification ID and resending token so we can use them later

                Toast.makeText(Activity_phone_login.this, "The code has been sent...", Toast.LENGTH_SHORT).show();

                send_ver_code_btn.setVisibility(View.INVISIBLE);
                input_phone_num.setVisibility(View.INVISIBLE);
                verfy_btn.setVisibility(View.VISIBLE);
                input_verifycation_code.setVisibility(View.VISIBLE);
                // ...
            }

        };*/

        mAuth = FirebaseAuth.getInstance();


        InputUserPhoneNumber = (EditText) findViewById(R.id.phone_input);
        InputUserVerificationCode = (EditText) findViewById(R.id.verify_input);
        SendVerificationCodeButton = (Button) findViewById(R.id.btn_send_code);
        VerifyButton = (Button) findViewById(R.id.btn_verify);
        loadingBar = new ProgressDialog(this);


        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String phoneNumber = InputUserPhoneNumber.getText().toString();

                if (TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(Activity_phone_login.this, "Please enter your phone number first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait, while we are authenticating using your phone...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, Activity_phone_login.this, callbacks);
                }
            }
        });



        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                InputUserPhoneNumber.setVisibility(View.INVISIBLE);
                SendVerificationCodeButton.setVisibility(View.INVISIBLE);


                String verificationCode = InputUserVerificationCode.getText().toString();

                if (TextUtils.isEmpty(verificationCode))
                {
                    Toast.makeText(Activity_phone_login.this, "Please write verification code first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Verification Code");
                    loadingBar.setMessage("Please wait, while we are verifying verification code...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                Toast.makeText(Activity_phone_login.this, "Invalid Phone Number, Please enter correct phone number with your country code...", Toast.LENGTH_LONG).show();
                loadingBar.dismiss();

                InputUserPhoneNumber.setVisibility(View.VISIBLE);
                SendVerificationCodeButton.setVisibility(View.VISIBLE);

                InputUserVerificationCode.setVisibility(View.INVISIBLE);
                VerifyButton.setVisibility(View.INVISIBLE);
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token)
            {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;


                Toast.makeText(Activity_phone_login.this, "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

                InputUserPhoneNumber.setVisibility(View.INVISIBLE);
                SendVerificationCodeButton.setVisibility(View.INVISIBLE);

                InputUserVerificationCode.setVisibility(View.VISIBLE);
                VerifyButton.setVisibility(View.VISIBLE);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(Activity_phone_login.this, "Congratulations, you're logged in Successfully.", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(Activity_phone_login.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }




    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(Activity_phone_login.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


   /* private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loadingbar.dismiss();
                            Toast.makeText(Activity_phone_login.this, "You are logged in sucessfully", Toast.LENGTH_SHORT).show();
                            send_user_to_mainactivity();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            String message = task.getException().getMessage().toString();
                            Toast.makeText(Activity_phone_login.this, message, Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                });
    }

    private void send_user_to_mainactivity() {

        Intent intent = new Intent(Activity_phone_login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }*/


    private void intiate() {
        /*mAuth = FirebaseAuth.getInstance();
        send_ver_code_btn = findViewById(R.id.btn_send_code);
        verfy_btn = findViewById(R.id.btn_verify);
        input_phone_num = findViewById(R.id.phone_input);
        input_verifycation_code = findViewById(R.id.verify_input);
        loadingbar = new ProgressDialog(this);*/
    }
}
