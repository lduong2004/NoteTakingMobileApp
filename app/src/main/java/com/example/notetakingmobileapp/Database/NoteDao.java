package com.example.notetakingmobileapp.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Note> getAllNotes();

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    Note getNoteById(int noteId); // Thêm hàm lấy 1 ghi chú theo ID

    @Insert
    void insert(Note note);

    @Update
    void update(Note note); // Thêm hàm cập nhật
}