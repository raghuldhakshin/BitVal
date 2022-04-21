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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SendOtpActivity extends AppCompatActivity {

    private String TAG = "TAGFIELD";
    private FirebaseAuth mAuth;
    private EditText mobileNum;
    private String mobileNumString;
    private Button sendOtpBtn;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);
        mAuth = FirebaseAuth.getInstance();
        mobileNum = findViewById(R.id.mobileNumberET);
        sendOtpBtn = findViewById(R.id.sendOtpBtn);

        sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobileNumString = mobileNum.getText().toString().trim();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91"+ mobileNumString,60,
                        TimeUnit.SECONDS,SendOtpActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(SendOtpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                Intent verifyIntent = new Intent(getApplicationContext(), NumberVerificationActivity.class);
                                verifyIntent.putExtra("mobile", mobileNumString);
                                verifyIntent.putExtra("verificationId", verificationId);
                                startActivity(verifyIntent);
                            }
                        }
                );



            }
        });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(SendOtpActivity.this, NumberVerificationActivity.class));
                            finish();

                        } else {
                            Toast.makeText(SendOtpActivity.this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SendOtpActivity.this, "OTP mismatch", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}