package com.bryanville.noteskeeper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import static com.bryanville.noteskeeper.provider.NoteKeeperProviderContract.Notes;

/**
 * Created by Sirikye Brian on 9/3/2019.
 * bryanmuloni@gmail.com
 */
public class NoteUploader {
    private static final String TAG = "NoteUploader";

    private final Context mContext;
    private boolean mCanceled;


    public NoteUploader(Context context) {
        mContext = context;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public boolean cancel() {
        return mCanceled = true;
    }

    public void doUpload(Uri uri) {
        String[] columns = {
                Notes.COLUMN_COURSE_ID,
                Notes.COLUMN_NOTE_TITLE,
                Notes.COLUMN_NOTE_TEXT,
        };
        Cursor cursor = mContext.getContentResolver().query(uri, columns, null, null, null);
        int courseIdPos = cursor.getColumnIndex(Notes.COLUMN_COURSE_ID);
        int noteTitlePos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TITLE);
        int noteTextPos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TEXT);

        Log.i(TAG, ">>>*** UPLOAD START - " + uri + "***<<<");
        mCanceled = false;
        while (!mCanceled && cursor.moveToNext()) {
            String courseId = cursor.getString(courseIdPos);
            String noteTitle = cursor.getString(noteTitlePos);
            String noteText = cursor.getString(noteTextPos);

            if (!noteTitle.equals("")) {
                Log.i(TAG, ">>> UPLOADING NOTE<<<" + courseId + "|" + noteTitle + "|" + noteText);
                simulateLongRunningWork();
            }
        }
    }

    private void simulateLongRunningWork() {
        try {
            Thread.sleep(2000);
        } catch (Exception ex) {
        }
    }
}
