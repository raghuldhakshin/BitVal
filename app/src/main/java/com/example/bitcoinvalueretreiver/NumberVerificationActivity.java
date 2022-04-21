package com.example.bitcoinvalueretreiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class NumberVerificationActivity extends AppCompatActivity {

    private Button checkOtp, sendOtp;
    private EditText mobileNumEt, otpEt;
    private String mobileNum, otpString;
    private FirebaseAuth mAuth;
    private String vID = "";
    private String TAG = "TAGFIELD";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_verification);
        initializeFields();
        verificationId = getIntent().getStringExtra("verificationId");



        checkOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();

                
            }
        });

    }

    private void verifyOtp() {
        otpString = otpEt.getText().toString().trim();
        if(otpString.isEmpty()){
            Toast.makeText(NumberVerificationActivity.this, "No OTP entered", Toast.LENGTH_SHORT).show();
        }

        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,otpString);


        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(NumberVerificationActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NumberVerificationActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "The verification code is invalid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void initializeFields() {
        mAuth = FirebaseAuth.getInstance();
        checkOtp = findViewById(R.id.checkOtpBtn);
        otpEt = findViewById(R.id.otpET);
    }



}