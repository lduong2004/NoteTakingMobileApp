package com.example.notetakingmobileapp.Adapter;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetakingmobileapp.Activities.EditNote;
import com.example.notetakingmobileapp.Database.Note;
import com.example.notetakingmobileapp.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private List<Note> notes;
    public NoteAdapter(List<Note> notes) { this.notes = notes; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.tvContent.setText(note.content);
        if (note.imagePath != null) {
            holder.imgPreview.setImageBitmap(BitmapFactory.decodeFile(note.imagePath));
        }

        // BẮT SỰ KIỆN CLICK VÀO 1 GHI CHÚ
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditNote.class);
            // Truyền ID của ghi chú sang màn hình Editor để nó biết là đang sửa chứ không phải tạo mới
            intent.putExtra("NOTE_ID", note.id);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return notes.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPreview; TextView tvContent;
        public ViewHolder(View v) {
            super(v);
            imgPreview = v.findViewById(R.id.imgPreview);
            tvContent = v.findViewById(R.id.tvContent);
        }
    }
}