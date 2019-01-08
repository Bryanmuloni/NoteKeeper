package com.bryanville.noteskeeper.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bryanville.noteskeeper.NoteActivity;
import com.bryanville.noteskeeper.NoteInfo;
import com.bryanville.noteskeeper.R;

import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NotesViewHolder> {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<NoteInfo> mNotes;

    public NotesListAdapter(Context context, List<NoteInfo> notes) {
        mContext = context;
        mNotes = notes;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public NotesListAdapter.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                               int ViewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list,viewGroup,false);
        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesListAdapter.NotesViewHolder notesViewHolder, int
            position) {
        NoteInfo noteInfo = mNotes.get(position);
        notesViewHolder.textCourse.setText(noteInfo.getCourse().getTitle());
        notesViewHolder.textTitle.setText(noteInfo.getTitle());
        notesViewHolder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        private TextView textCourse;
        private TextView textTitle;
        private int mCurrentPosition;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = itemView.findViewById(R.id.text_course);
            textTitle = itemView.findViewById(R.id.text_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,NoteActivity.class);
                    intent.putExtra(NoteActivity.NOTE_POSITION,mCurrentPosition);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
