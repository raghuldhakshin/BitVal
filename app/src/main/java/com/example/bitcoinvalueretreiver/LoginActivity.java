package com.example.bitcoinvalueretreiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {


    private EditText emailIdET,passET;
    private Button loginBtn,createAccBtn,loginWithNum;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        InitializeFields();



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUsertoLogin();
            }
        });

        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUsertoRegisterActivity();
            }
        });

        loginWithNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SendOtpActivity.class));

            }
        });

    }



    private void AllowUsertoLogin()
    {
        String emailId = emailIdET.getText().toString();
        String password = passET.getText().toString();

        if(TextUtils.isEmpty(emailId))
        {
            Toast.makeText(this, "Email ID field empty", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Password Field empty", Toast.LENGTH_SHORT).show();

        }
        else
        {
            progressDialog.setTitle("Logging in!");
            progressDialog.setMessage("Please Wait..");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(emailId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        sendUsertoMainActivity();

                    }
                    else
                    {
                        String message =task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });


        }
    }

    private void sendUsertoMainActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }

    private void InitializeFields()
    {
        emailIdET = findViewById(R.id.EmailETLog);
        passET = findViewById(R.id.PasswordETLog);
        loginBtn = findViewById(R.id.loginBtn);
        CardView cardView = findViewById(R.id.loginCardView);
        loginWithNum = findViewById(R.id.loginWithNumBtn);
        cardView.getResources().getColor(R.color.cardColorYellow);
        progressDialog = new ProgressDialog(this);
        createAccBtn = findViewById(R.id.createAccTextLog);
    }

    private void sendUsertoRegisterActivity()
    {
        Intent regIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(regIntent);
    }



}
