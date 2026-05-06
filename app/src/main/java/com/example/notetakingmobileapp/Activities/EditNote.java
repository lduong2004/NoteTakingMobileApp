package com.example.notetakingmobileapp.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notetakingmobileapp.Function.DrawingView;
import com.example.notetakingmobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditNote extends AppCompatActivity {

    private String noteId = null;
    private EditText etNote;
    private DrawingView drawingView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etNote = findViewById(R.id.etNote);
        drawingView = findViewById(R.id.drawingView);

        if (getIntent().hasExtra("NOTE_ID")) {
            noteId = getIntent().getStringExtra("NOTE_ID");
            loadNoteData(noteId);
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveNoteToFirestore());
        
        // Đảm bảo nút back hoạt động (với ID imv_back trong layout)
        if (findViewById(R.id.imv_back) != null) {
            findViewById(R.id.imv_back).setOnClickListener(v -> finish());
        }
    }

    private void loadNoteData(String id) {
        db.collection("notes").document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                etNote.setText(documentSnapshot.getString("content"));
                String drawingBase64 = documentSnapshot.getString("drawingData");
                if (drawingBase64 != null && !drawingBase64.isEmpty()) {
                    try {
                        byte[] decodedString = Base64.decode(drawingBase64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        if (decodedByte != null) {
                            drawingView.loadExistingImageFromBitmap(decodedByte);
                        }
                    } catch (Exception e) {
                        Log.e("EditNote", "Error decoding drawing data", e);
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveNoteToFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = etNote.getText().toString().trim();
        String userId = currentUser.getUid();

        // Chuyển bản vẽ thành chuỗi Base64
        String drawingData = "";
        try {
            Bitmap bitmap = drawingView.getDrawingBitmap();
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // Nén ảnh để tránh vượt quá giới hạn 1MB của Firestore document
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); 
                drawingData = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            }
        } catch (Exception e) {
            Log.e("EditNote", "Error processing bitmap", e);
        }

        Map<String, Object> note = new HashMap<>();
        note.put("content", content);
        note.put("drawingData", drawingData);
        note.put("ownerId", userId);
        // Sử dụng ServerTimestamp để đồng bộ thời gian và khớp với Rules
        note.put("timestamp", FieldValue.serverTimestamp());

        if (noteId == null) {
            // Tạo mới ghi chú
            db.collection("notes").add(note)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Note saved successfully!", Toast.LENGTH_SHORT).show();
                        finish(); 
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Error adding document", e);
                        Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            // Cập nhật ghi chú hiện tại
            db.collection("notes").document(noteId).set(note)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Note updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Error updating document", e);
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}