package com.bryanville.noteskeeper.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bryanville.noteskeeper.NoteActivity;
import com.bryanville.noteskeeper.R;
import com.bryanville.noteskeeper.database.NoteKeeperDatabaseContract;

import static com.bryanville.noteskeeper.database.NoteKeeperDatabaseContract.*;
import static com.bryanville.noteskeeper.database.NoteKeeperDatabaseContract.NoteInfoEntry;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NotesViewHolder> {
    Context mContext;
    LayoutInflater mLayoutInflater;
    Cursor mCursor;
    int mCoursePos;
    int mNoteTitlePos;
    int mIdPos;

    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if (mCursor == null) return;//            Get column indexes from mCursor
            mCoursePos = mCursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE);
            mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
            mIdPos = mCursor.getColumnIndex(NoteInfoEntry._ID);
        }



    public void changeCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
            mCursor = cursor;
            populateColumnPositions();
            notifyDataSetChanged();

    }

    @NonNull
    @Override
    public NoteRecyclerAdapter.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                                  int ViewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list, viewGroup, false);
        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteRecyclerAdapter.NotesViewHolder notesViewHolder,
                                 int position) {
        mCursor.moveToPosition(position);

//        int mCoursePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
//        int mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
//        int mIdPos = mCursor.getColumnIndex(NoteInfoEntry._ID);

        String course = mCursor.getString(mCoursePos);
        String noteTitle = mCursor.getString(mNoteTitlePos);
        int id = mCursor.getInt(mIdPos);

        notesViewHolder.textCourse.setText(course);
        notesViewHolder.textTitle.setText(noteTitle);
        notesViewHolder.mId = id;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        private TextView textCourse;
        private TextView textTitle;
        private int mId;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = itemView.findViewById(R.id.text_course);
            textTitle = itemView.findViewById(R.id.text_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NoteActivity.class);
                    intent.putExtra(NoteActivity.NOTE_ID, mId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
