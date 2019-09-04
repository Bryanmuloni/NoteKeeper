package com.bryanville.noteskeeper;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bryanville.noteskeeper.database.NoteKeeperOpenHelper;

import static com.bryanville.noteskeeper.database.NoteKeeperDatabaseContract.CourseInfoEntry;
import static com.bryanville.noteskeeper.database.NoteKeeperDatabaseContract.NoteInfoEntry;
import static com.bryanville.noteskeeper.provider.NoteKeeperProviderContract.Courses;
import static com.bryanville.noteskeeper.provider.NoteKeeperProviderContract.Notes;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = NoteActivity.class.getSimpleName();
    public static final String NOTE_ID = "com.bryanville.noteskeeper.NOTE_ID";
    private static final String ORIGINAL_NOTE_cID = "com.bryanville.noteskeeper.ORIGINAL_NOTE_cID";
    private static final String ORIGINAL_NOTE_TITLE = "com.bryanville.noteskeeper " +
            ".ORIGINAL_NOTE_TITLE";
    private static final String ORIGINAL_NOTE_TEXT = "com.bryanville.noteskeeper.ORIGINAL_NOTE_TEXT";
    public static final int ID_NOT_SET = -1;
    private static final int LOADER_NOTES = 0;
    public static final int LOADER_COURSES = 1;
    private NoteInfo mNote = new NoteInfo(DataManager.getInstance().getCourses().get(0), "", "");
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText textNoteTitle;
    private EditText textNoteText;

    private boolean mIsCancelling;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private NoteKeeperOpenHelper mDbOpenHelper;
    private Cursor noteCursor;
    private int courseIdPos;
    private int noteTitlePos;
    private int noteTextPos;
    private int mNoteId;
    private android.widget.SimpleCursorAdapter mAdapterCourses;
    private boolean coursesQueryFinished;
    private boolean notesQueryFinished;
    private Uri mNoteUri;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_cID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDbOpenHelper = new NoteKeeperOpenHelper(this);

        mSpinnerCourses = findViewById(R.id.courseSpinner);
        mAdapterCourses = new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item,null,
                new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},
                new int[]{android.R.id.text1},0);
        mAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(mAdapterCourses);


        getLoaderManager().initLoader(LOADER_COURSES,null,this);

        readDisplayStateValues();
        if(savedInstanceState == null)
            saveOriginalStateValues();
        else {
            restoreOriginalNoteValues(savedInstanceState);
        }
        textNoteTitle = findViewById(R.id.note_title);
        textNoteText = findViewById(R.id.note_description);

        if(!mIsNewNote) {
            getLoaderManager().initLoader(LOADER_NOTES,null, this);
        }

    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_cID);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
    }

    private void saveOriginalStateValues() {
        if(mIsNewNote){
            return;
        }
        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteTitle = mNote.getTitle();
        mOriginalNoteText = mNote.getText();
    }

    private void displayNote() {
        String courseId = noteCursor.getString(courseIdPos);
        String noteTitle = noteCursor.getString(noteTitlePos);
        String noteText = noteCursor.getString(noteTextPos);

        int courseIndex = getIndexOfCourseId(courseId);

        mSpinnerCourses.setSelection(courseIndex);
        textNoteText.setText(noteText);
        textNoteTitle.setText(noteTitle);

        CourseEventBroadcastHelper.sendEventBroadcast(this, courseId, "Editing Note");
    }

    private int getIndexOfCourseId(String courseId) {
        Cursor cursor = mAdapterCourses.getCursor();
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        int courseRowIndex = 0;

        boolean more = cursor.moveToFirst();
        while(more){
            String cursorCourseId = cursor.getString(courseIdPos);
            if(courseId.equals(cursorCourseId))
                break;

            courseRowIndex ++;
            more = cursor.moveToNext();
        }
        return courseRowIndex;
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);
        mIsNewNote = mNoteId == ID_NOT_SET;
        if(mIsNewNote){
            createNewNote();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes_keeper, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling) {
            if(mIsNewNote) {
                deleteNoteFromDatabase();
            }
            else {
                storePreviousNoteValues();
            }
        }
        else {
            saveNote();
        }
    }

    private void deleteNoteFromDatabase() {
//        final String selection = NoteInfoEntry._ID + " = ?";
//        final String[] selectionArgs = {Integer.toString(mNoteId)};

        @SuppressLint("StaticFieldLeak") AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
//                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
//                db.delete(NoteInfoEntry.NOTE_TABLE,selection,selectionArgs);
                getContentResolver().delete(mNoteUri, null, null);
                return null;
            }
        };
        task.execute();
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setText(mOriginalNoteText);
        mNote.setTitle(mOriginalNoteTitle);
    }

    private void saveNote() {
        String courseId = selectedCourseId();
        String noteTitle = textNoteTitle.getText().toString();
        String noteText = textNoteText.getText().toString();

        if (!noteTitle.isEmpty() && !noteText.isEmpty()){
            saveNoteToDatabase(courseId,noteTitle,noteText);
        }else {
            Toast.makeText(this, "Please fill all fields to create note.", Toast.LENGTH_SHORT).show();
        }
    }

    private String selectedCourseId() {
        int selectedPosition = mSpinnerCourses.getSelectedItemPosition();
        Cursor cursor = mAdapterCourses.getCursor();
        cursor.moveToPosition(selectedPosition);
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        String courseId = cursor.getString(courseIdPos);

        return courseId;
    }

    private void saveNoteToDatabase(String courseId, String noteTitle, String noteText){
        String selection = NoteInfoEntry._ID + " = ?";
        String[] selectionArgs = {Integer.toString(mNoteId)};

        ContentValues values = new ContentValues();
        values.put(NoteInfoEntry.COLUMN_COURSE_ID,courseId);
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE,noteTitle);
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT,noteText);

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.update(NoteInfoEntry.NOTE_TABLE, values,selection,selectionArgs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
        }
        else if (id == R.id.action_next){
            moveNext();
        } else if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();
        } else if (id == R.id.action_set_reminder) {
            showReminderNotification();
            Toast.makeText(this, "Wow", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showReminderNotification() {
        Log.d(LOG_TAG, "showReminderNotification: calling notify method from notification helper " +
                "class");
        String noteTitle = textNoteTitle.getText().toString();
        String noteText = textNoteText.getText().toString();

        int noteId = (int) ContentUris.parseId(mNoteUri);
        NoteReminderNotification.notify(this, noteTitle, noteText, noteId);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNodeIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(mNoteId < lastNodeIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();
        ++mNoteId;
        mNote = DataManager.getInstance().getNotes().get(mNoteId);

        saveOriginalStateValues();
        displayNote();

        invalidateOptionsMenu();
    }

    private void sendEmail() {
//        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
//        String subject = textNoteTitle.getText().toString();
//        String text = "Checkout what I learnt in the PluralSight course " +
//                course.getTitle() + "\n" + textNoteText.getText().toString();
//
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("message/rfc2822");
//        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//        intent.putExtra(Intent.EXTRA_TEXT, text);
//
//        startActivity(intent);
    }


    private void createNewNote() {

        @SuppressLint("StaticFieldLeak") AsyncTask<ContentValues, Integer, Uri> task =
                new AsyncTask<ContentValues, Integer, Uri>() {
                    private ProgressBar mProgressBar;

                    @Override
                    protected void onPreExecute() {
                        mProgressBar = findViewById(R.id.progressBar);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mProgressBar.setProgress(1);
                    }

                    @Override
                    protected Uri doInBackground(ContentValues... contentValues) {
                        Log.d(LOG_TAG, "doInBackground: thread: " + Thread.currentThread().getId());
                        ContentValues insertValues = contentValues[0];
                        Uri rowUri = getContentResolver().insert(Notes.NOTES_CONTENT_URI, insertValues);

                        simulateLongRunningWork();
                        publishProgress(2);


                        simulateLongRunningWork();
                        publishProgress(3);
                        return rowUri;
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        int progressValue = values[0];
                        mProgressBar.setProgress(progressValue);
                    }

                    @Override
                    protected void onPostExecute(Uri uri) {
                        mNoteUri = uri;
                        Log.d(LOG_TAG, "onPostExecute: thread: " + Thread.currentThread().getId());
                        displaySnackBar(mNoteUri.toString());
                        mProgressBar.setVisibility(View.GONE);
                    }
                };
        ContentValues values = new ContentValues();
        values.put(Notes.COLUMN_COURSE_ID, "");
        values.put(Notes.COLUMN_NOTE_TITLE, "");
        values.put(Notes.COLUMN_NOTE_TEXT, "");
        Log.d(LOG_TAG, "createNewNote: call to execute - thread: " + Thread.currentThread().getId());
        task.execute(values);

    }

    private void simulateLongRunningWork() {
        try {
            Thread.currentThread();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void displaySnackBar(String noteUri) {
        View view = findViewById(R.id.mainConstraint);
        Snackbar.make(view, noteUri, Snackbar.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        CursorLoader loader = null;
        if(i == LOADER_NOTES)
            loader = createLoaderNotes();
        else if (i == LOADER_COURSES) {
            loader = createLoaderCourses();
        }
        return loader;
    }

    private CursorLoader createLoaderCourses() {
        coursesQueryFinished = false;
        Uri uri = Courses.COURSES_CONTENT_URI;
        String[] courseColumns = {
                Courses.COLUMN_COURSE_TITLE,
                Courses.COLUMN_COURSE_ID,
                Courses._ID
        };
        return new CursorLoader(this, uri, courseColumns, null, null,
                Courses.COLUMN_COURSE_TITLE);
    }

    @SuppressLint("StaticFieldLeak")
    private CursorLoader createLoaderNotes() {
        notesQueryFinished = false;

        String[] noteColumns = {
                Notes.COLUMN_COURSE_ID,
                Notes.COLUMN_NOTE_TITLE,
                Notes.COLUMN_NOTE_TEXT
        };
        mNoteUri = ContentUris.withAppendedId(Notes.NOTES_CONTENT_URI, mNoteId);
        return new CursorLoader(this, mNoteUri, noteColumns, null, null, null);
//        return new CursorLoader(this){
//            @Override
//            public Cursor loadInBackground() {
//
//                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
//
//                String selection = NoteInfoEntry._ID + " = ?";
//                String[] selectionArgs = {Integer.toString(mNoteId)};
//
//                String[] noteColumns = {
//                        NoteInfoEntry.COLUMN_COURSE_ID,
//                        NoteInfoEntry.COLUMN_NOTE_TITLE,
//                        NoteInfoEntry.COLUMN_NOTE_TEXT
//                };
//                return db.query(NoteInfoEntry.NOTE_TABLE, noteColumns,selection,
//                        selectionArgs,null, null, null);
//            }
//        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == LOADER_NOTES)
            loadFinishedNotes(cursor);

        else if(loader.getId() == LOADER_COURSES) {
            mAdapterCourses.changeCursor(cursor);
            coursesQueryFinished = true;
            displayNoteWhenQueriesFinished();
        }
    }

    private void loadFinishedNotes(Cursor cursor) {
        noteCursor = cursor;
        courseIdPos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        noteTitlePos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        noteTextPos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        noteCursor.moveToNext();
        notesQueryFinished = true;
        displayNoteWhenQueriesFinished();
    }

    private void displayNoteWhenQueriesFinished() {
        if(notesQueryFinished && coursesQueryFinished)
            displayNote();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTES){
            if(noteCursor != null)
                noteCursor.close();
            else if (loader.getId() == LOADER_COURSES){
                mAdapterCourses.changeCursor(null);
            }
        }
    }
}
