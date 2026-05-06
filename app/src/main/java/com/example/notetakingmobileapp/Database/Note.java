package com.example.notetakingmobileapp.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String content;
    public String imagePath; // Lưu đường dẫn file ảnh vẽ
}