package com.example.bitcoinvalueretreiver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Timer;
import okhttp3.OkHttpClient;



public class MainActivity extends AppCompatActivity {
    public static final long PERIOD = 60 * 1000; // 30 sec
    public static final String BITCOIN_PRICE_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";
    private FirebaseAuth mAuth;
    private Button refreshBtn, logoutBtn;
    private TextView bitcoinPrice, justText;
    ProgressBar progressBar;
    private String price;
    private OkHttpClient client = new OkHttpClient();
    private Timer timer;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference().child("BTC_price");
        progressBar = findViewById(R.id.mainProgressBar);
        refreshBtn = findViewById(R.id.refreshBtn);
        logoutBtn = findViewById(R.id.logOutBtn);
        justText = findViewById(R.id.bitTV);
        bitcoinPrice = findViewById(R.id.bitcoinPrice);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                justText.setVisibility(View.GONE);
                bitcoinPrice.setVisibility(View.GONE);
                refreshBtn.setVisibility(View.GONE);
                logoutBtn.setVisibility(View.GONE);
                getResponse();
                uploadPrice();
                retrivePrice();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    private void uploadPrice(){
        RootRef.setValue(bitcoinPrice.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Uploaded "+bitcoinPrice.getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void retrivePrice(){
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(getApplicationContext(), "Data changed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getResponse() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BITCOIN_PRICE_ENDPOINT, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("bpi").getJSONObject("USD");
                    Double priceDouble = jsonObject.getDouble("rate_float");
                    bitcoinPrice.setText("$"+ priceDouble.toString());
                    progressBar.setVisibility(View.GONE);
                    justText.setVisibility(View.VISIBLE);
                    bitcoinPrice.setVisibility(View.VISIBLE);
                    refreshBtn.setVisibility(View.VISIBLE);
                    logoutBtn.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });

        int socketTime = 3000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);

    }


    @SuppressLint("SetTextI18n")
    private void parseBitcoinPrice(String str) throws JSONException {
        JSONObject jsonObject = new JSONObject(str);
        Double currentPrice = jsonObject.getJSONObject("bpi").getJSONObject("USD").getDouble("rate_float");
        bitcoinPrice.setText(currentPrice.toString());
        uploadPrice();

    }



    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null)
        {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        getResponse();

    }

}