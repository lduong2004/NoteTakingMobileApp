package com.example.notetakingmobileapp.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton btnBrush, btnEraser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etNote = findViewById(R.id.etNote);
        drawingView = findViewById(R.id.drawingView);
        btnBrush = findViewById(R.id.btnBrush);
        btnEraser = findViewById(R.id.btnEraser);

        if (getIntent().hasExtra("NOTE_ID")) {
            noteId = getIntent().getStringExtra("NOTE_ID");
            loadNoteData(noteId);
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveNoteToFirestore());
        
        if (findViewById(R.id.imv_back) != null) {
            findViewById(R.id.imv_back).setOnClickListener(v -> finish());
        }

        // Thiết lập bộ công cụ vẽ
        btnBrush.setOnClickListener(v -> {
            drawingView.setEraser(false);
            Toast.makeText(this, "Đã chọn Bút vẽ", Toast.LENGTH_SHORT).show();
            btnBrush.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            btnEraser.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        });

        btnEraser.setOnClickListener(v -> {
            drawingView.setEraser(true);
            Toast.makeText(this, "Đã chọn Cục tẩy", Toast.LENGTH_SHORT).show();
            btnEraser.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            btnBrush.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        });

        // Mặc định chọn bút vẽ
        btnBrush.performClick();
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

        String drawingData = "";
        try {
            Bitmap bitmap = drawingView.getDrawingBitmap();
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
        note.put("timestamp", FieldValue.serverTimestamp());

        if (noteId == null) {
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