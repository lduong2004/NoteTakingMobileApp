package com.example.notetakingmobileapp.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;

import com.example.notetakingmobileapp.Database.AppDatabase;
import com.example.notetakingmobileapp.Database.Note;
import com.example.notetakingmobileapp.Function.DrawingView;
import com.example.notetakingmobileapp.R;

public class EditNote extends AppCompatActivity {

    private int noteId = -1; // -1 nghĩa là đang tạo mới
    private String existingImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        EditText etNote = findViewById(R.id.etNote);
        DrawingView drawingView = findViewById(R.id.drawingView);

        // KIỂM TRA CHẾ ĐỘ SỬA HAY TẠO MỚI
        if (getIntent().hasExtra("NOTE_ID")) {
            noteId = getIntent().getIntExtra("NOTE_ID", -1);

            // Lấy dữ liệu ghi chú cũ từ DB
            Note oldNote = AppDatabase.getInstance(this).noteDao().getNoteById(noteId);
            if (oldNote != null) {
                // Điền chữ cũ vào EditText
                etNote.setText(oldNote.content);
                // Load hình cũ vào khu vực vẽ
                existingImagePath = oldNote.imagePath;
                drawingView.loadExistingImage(existingImagePath);
            }
        }

        // XỬ LÝ NÚT LƯU
        findViewById(R.id.btnSave).setOnClickListener(v -> {
            // Lưu hình ảnh vẽ trên màn hình thành file
            String newImagePath = saveBitmap(drawingView.getDrawingBitmap());

            Note note = new Note();
            note.content = etNote.getText().toString();
            note.imagePath = newImagePath != null ? newImagePath : existingImagePath;

            if (noteId == -1) {
                // Chế độ Tạo mới: Insert
                AppDatabase.getInstance(this).noteDao().insert(note);
                Toast.makeText(this, "Đã thêm mới!", Toast.LENGTH_SHORT).show();
            } else {
                // Chế độ Sửa: Update
                note.id = noteId; // Gán đúng ID cũ để nó đè lên dòng cũ
                AppDatabase.getInstance(this).noteDao().update(note);
                Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }

    private String saveBitmap(Bitmap bmp) {
        // Có thể tái sử dụng file cũ nếu đang ở chế độ sửa, hoặc tạo file mới
        File file;
        if (existingImagePath != null && noteId != -1) {
            file = new File(existingImagePath); // Ghi đè file ảnh cũ
        } else {
            file = new File(getFilesDir(), "img_" + System.currentTimeMillis() + ".png"); // Tạo file mới
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            return file.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }
}