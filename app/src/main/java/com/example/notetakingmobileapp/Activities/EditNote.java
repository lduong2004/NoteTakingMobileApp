package com.example.notetakingmobileapp.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notetakingmobileapp.Function.DrawingView;
import com.example.notetakingmobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
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
    }

    private void loadNoteData(String id) {
        db.collection("notes").document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                etNote.setText(documentSnapshot.getString("content"));
                String drawingBase64 = documentSnapshot.getString("drawingData");
                if (drawingBase64 != null) {
                    byte[] decodedString = Base64.decode(drawingBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    drawingView.loadExistingImageFromBitmap(decodedByte);
                }
            }
        });
    }

    private void saveNoteToFirestore() {
        String content = etNote.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();

        // Chuyển bản vẽ thành chuỗi Base64 để lưu vào Firestore
        Bitmap bitmap = drawingView.getDrawingBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String drawingData = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        Map<String, Object> note = new HashMap<>();
        note.put("content", content);
        note.put("drawingData", drawingData);
        note.put("ownerId", userId);
        note.put("timestamp", System.currentTimeMillis());

        if (noteId == null) {
            // Tạo mới
            db.collection("notes").add(note)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            // Cập nhật
            db.collection("notes").document(noteId).set(note)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Note updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}