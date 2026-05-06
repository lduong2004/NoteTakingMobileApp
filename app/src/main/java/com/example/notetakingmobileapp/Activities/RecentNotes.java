package com.example.notetakingmobileapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetakingmobileapp.Adapter.NoteAdapter;
import com.example.notetakingmobileapp.Database.FirebaseNote;
import com.example.notetakingmobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class RecentNotes extends AppCompatActivity {

    private RecyclerView rvNotes;
    private NoteAdapter adapter;
    private List<FirebaseNote> noteList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListenerRegistration noteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_notes);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvNotes = findViewById(R.id.rvNotes);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        
        noteList = new ArrayList<>();
        // Khởi tạo adapter với listener cho việc xóa note
        adapter = new NoteAdapter(noteList, new NoteAdapter.OnNoteListener() {
            @Override
            public void onDeleteClick(FirebaseNote note) {
                showDeleteDialog(note);
            }
        });
        rvNotes.setAdapter(adapter);

        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            startActivity(new Intent(this, EditNote.class));
        });

        findViewById(R.id.imv_menu).setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
        });
    }

    private void showDeleteDialog(FirebaseNote note) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> deleteNote(note))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteNote(FirebaseNote note) {
        if (note.getId() == null) return;

        db.collection("notes").document(note.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(RecentNotes.this, "Note deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(RecentNotes.this, "Error deleting note", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        startListeningNotes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteListener != null) {
            noteListener.remove();
        }
    }

    private void startListeningNotes() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, GetStarted.class));
            finish();
            return;
        }

        noteListener = db.collection("notes")
                .whereEqualTo("ownerId", user.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        noteList.clear();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                            FirebaseNote note = doc.toObject(FirebaseNote.class);
                            if (note != null) {
                                note.setId(doc.getId());
                                noteList.add(note);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}