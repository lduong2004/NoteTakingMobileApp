package com.example.notetakingmobileapp.Adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetakingmobileapp.Activities.EditNote;
import com.example.notetakingmobileapp.Database.FirebaseNote;
import com.example.notetakingmobileapp.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private List<FirebaseNote> notes;
    private OnNoteListener onNoteListener;

    public interface OnNoteListener {
        void onDeleteClick(FirebaseNote note);
    }

    public NoteAdapter(List<FirebaseNote> notes, OnNoteListener onNoteListener) {
        this.notes = notes;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseNote note = notes.get(position);
        holder.tvContent.setText(note.getContent());

        if (note.getDrawingData() != null && !note.getDrawingData().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(note.getDrawingData(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imgPreview.setImageBitmap(decodedByte);
            } catch (Exception e) {
                holder.imgPreview.setImageResource(R.drawable.ic_get_started);
            }
        } else {
            holder.imgPreview.setImageResource(R.drawable.ic_get_started);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditNote.class);
            intent.putExtra("NOTE_ID", note.getId());
            v.getContext().startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (onNoteListener != null) {
                onNoteListener.onDeleteClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes != null ? notes.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPreview, btnDelete;
        TextView tvContent;

        public ViewHolder(View v) {
            super(v);
            imgPreview = v.findViewById(R.id.imgPreview);
            tvContent = v.findViewById(R.id.tvContent);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}