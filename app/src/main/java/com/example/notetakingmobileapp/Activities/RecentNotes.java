package com.example.notetakingmobileapp.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetakingmobileapp.Adapter.NoteAdapter;
import com.example.notetakingmobileapp.Database.AppDatabase;
import com.example.notetakingmobileapp.R;

public class RecentNotes extends AppCompatActivity {
    RecyclerView rvNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recent_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvNotes = findViewById(R.id.rvNotes);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.fabAdd).setOnClickListener(v -> startActivity(new Intent(this, EditNote.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        rvNotes.setAdapter(new NoteAdapter(AppDatabase.getInstance(this).noteDao().getAllNotes()));
    }

}