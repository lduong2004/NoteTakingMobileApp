package com.example.notetakingmobileapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.notetakingmobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GetStarted extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(GetStarted.this, RecentNotes.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Luôn luôn ép buộc sử dụng Light Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        mAuth = FirebaseAuth.getInstance();

        Button btn_get_started = findViewById(R.id.btn_get_started);
        btn_get_started.setOnClickListener(v -> {
            Intent intent = new Intent(GetStarted.this, CreateAccount.class);
            startActivity(intent);
        });

        TextView tv_account_already = findViewById(R.id.tv_account_already);
        tv_account_already.setOnClickListener(v -> {
            Intent intent = new Intent(GetStarted.this, Login.class);
            startActivity(intent);
        });
    }
}