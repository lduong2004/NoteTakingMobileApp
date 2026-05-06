package com.example.notetakingmobileapp.Database;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FirebaseNote {
    @DocumentId
    private String id;
    private String content;
    private String drawingData; // Base64 string
    private String ownerId;
    private Object timestamp; // Dùng Object để hỗ trợ cả Long và Timestamp

    public FirebaseNote() {
        // Required for Firestore
    }

    public FirebaseNote(String content, String drawingData, String ownerId, Object timestamp) {
        this.content = content;
        this.drawingData = drawingData;
        this.ownerId = ownerId;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDrawingData() { return drawingData; }
    public void setDrawingData(String drawingData) { this.drawingData = drawingData; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public Object getTimestamp() { return timestamp; }
    public void setTimestamp(Object timestamp) { this.timestamp = timestamp; }
}