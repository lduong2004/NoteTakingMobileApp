package com.example.notetakingmobileapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notetakingmobileapp.R;

public class GetStarted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn_get_started = findViewById(R.id.btn_get_started);
        btn_get_started.setOnClickListener(v -> {
            Intent intent = new Intent(GetStarted.this, Premium.class);
            startActivity(intent);
        });

        TextView tv_account_already = findViewById(R.id.tv_account_already);
        tv_account_already.setOnClickListener(v -> {
            Intent intent = new Intent(GetStarted.this, CreateAccount.class);
            startActivity(intent);
        });
    }
}