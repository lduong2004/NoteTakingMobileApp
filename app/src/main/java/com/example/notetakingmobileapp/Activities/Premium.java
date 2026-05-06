package com.example.notetakingmobileapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notetakingmobileapp.R;

public class Premium extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_premium);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ 2 thẻ
        LinearLayout cardAnnual = findViewById(R.id.card_annual);
        LinearLayout cardMonthly = findViewById(R.id.card_monthly);

        // Thiết lập mặc định khi vừa vào màn hình là chọn thẻ Annual
        cardAnnual.setSelected(true);
        cardMonthly.setSelected(false);

        // Xử lý khi bấm thẻ Annual
        cardAnnual.setOnClickListener(v -> {
            cardAnnual.setSelected(true);   // Bật viền Annual
            cardMonthly.setSelected(false); // Tắt viền Monthly
        });

        // Xử lý khi bấm thẻ Monthly
        cardMonthly.setOnClickListener(v -> {
            cardMonthly.setSelected(true);  // Bật viền Monthly
            cardAnnual.setSelected(false);  // Tắt viền Annual
        });

        Button btn_get_started = findViewById(R.id.btn_subscribe);
        btn_get_started.setOnClickListener(v -> {
            Intent intent = new Intent(Premium.this, RecentNotes.class);
            startActivity(intent);
        });
    }
}