package com.example.bitcoinvalueretreiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    private EditText NameReg,EmailReg,PassReg;
    private Button createAccRegBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private TextView alreadyHaveAnAccount;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InitializeFields();
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUsertoLoginActivity();
            }
        });

        createAccRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });



    }

    private void InitializeFields()
    {
        NameReg = findViewById(R.id.NameETReg);
        EmailReg = findViewById(R.id.EmailETReg);
        PassReg = findViewById(R.id.PasswordETReg);
        createAccRegBtn = findViewById(R.id.createAccBtnReg);
        alreadyHaveAnAccount = findViewById(R.id.alreadyHaveAnAcc);
        loadingBar = new ProgressDialog(this);
    }

    private void sendUsertoLoginActivity()
    {
        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    private void createAccount()
    {
        final String nameStr = NameReg.getText().toString();
        String EmailStr = EmailReg.getText().toString();
        String passStr = PassReg.getText().toString();

        if(TextUtils.isEmpty(nameStr))
        {
            Toast.makeText(this, "Name field empty", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(EmailStr))
        {
            Toast.makeText(this, "Enter a Valid Email ID", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(passStr))
        {
            Toast.makeText(this, "Enter a Valid Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating an Account");
            loadingBar.setMessage("Hang On ;)");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(EmailStr,passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        sendUsertoMainActivity();
                        Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error : " +message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });
        }
    }

    private void sendUsertoMainActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }
}
