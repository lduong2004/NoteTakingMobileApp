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
                .setTitle("Xóa ghi chú")
                .setMessage("Bạn có chắc chắn muốn xóa ghi chú này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteNote(note))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteNote(FirebaseNote note) {
        if (note.getId() == null) return;

        // Optimistic UI: Xóa khỏi list ngay lập tức để UI cập nhật tức thì
        int position = noteList.indexOf(note);
        if (position != -1) {
            noteList.remove(position);
            adapter.notifyItemRemoved(position);
        }

        db.collection("notes").document(note.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(RecentNotes.this, "Đã xóa ghi chú", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    // Nếu lỗi thì lắng nghe lại để phục hồi dữ liệu
                    startListeningNotes();
                    Toast.makeText(RecentNotes.this, "Lỗi khi xóa ghi chú", Toast.LENGTH_SHORT).show();
                });
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

        if (noteListener != null) noteListener.remove();

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
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    }
                });
    }
}