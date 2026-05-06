package com.example.notetakingmobileapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetakingmobileapp.Adapter.NoteAdapter;
import com.example.notetakingmobileapp.Database.FirebaseNote;
import com.example.notetakingmobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class RecentNotes extends AppCompatActivity {
    RecyclerView rvNotes;
    ImageView imvMenu;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recent_notes);
        
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imvMenu = findViewById(R.id.imv_menu);
        rvNotes = findViewById(R.id.rvNotes);
        
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        
        findViewById(R.id.fabAdd).setOnClickListener(v -> startActivity(new Intent(this, EditNote.class)));
        
        imvMenu.setOnClickListener(v -> {
            Intent intent = new Intent(RecentNotes.this, Profile.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotesFromFirestore();
    }

    private void loadNotesFromFirestore() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, GetStarted.class));
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("notes")
                .whereEqualTo("ownerId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading notes", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        List<FirebaseNote> notes = value.toObjects(FirebaseNote.class);
                        rvNotes.setAdapter(new NoteAdapter(notes));
                    }
                });
    }
}