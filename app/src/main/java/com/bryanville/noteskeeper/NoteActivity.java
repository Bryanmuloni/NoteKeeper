package com.bryanville.noteskeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final int POSITION_NOT_SET = -1;
    private static final String LOG_TAG = NoteActivity.class.getSimpleName();
    private Spinner courseSpinner;

    //    public static final String NOTE_POSITION = "com.bryanville.noteskeeper.NOTE_POSITION";
    public static final String NOTE_POSITION = "com.bryanville.noteskeeper.NOTE_POSITION";
    private NoteInfo mNoteInfo;
    private EditText mDescription;
    private EditText mTitle;
    private boolean mIsNewNote;
    private int mNotePosition;
    private boolean isCancelling;
    private String mOriginalNoteCourseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        courseSpinner = findViewById(R.id.courseSpinner);
        List<CourseInfo> courseInfoList = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> courseInfoArrayAdapter = new ArrayAdapter<>(this, android.R
                .layout.simple_list_item_1, courseInfoList);
        courseInfoArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);


        readDisplayStateValues();
        saveOriginalNoteValues();

        mTitle = findViewById(R.id.note_title);
        mDescription = findViewById(R.id.note_description);

        if (!mIsNewNote) displayNote(courseSpinner, mTitle, mDescription);
        courseSpinner.setAdapter(courseInfoArrayAdapter);


    }

    private void saveOriginalNoteValues() {
        if (mIsNewNote) return;
        mNoteInfo = DataManager.getInstance().getNotes().get(mNotePosition);
        mOriginalNoteCourseId = mNoteInfo.getCourse().getCourseId();
        mOriginalNoteCourseId = mNoteInfo.getTitle();
        mOriginalNoteCourseId = mNoteInfo.getText();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(mNotePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void displayNote(Spinner myCourseSpinner, EditText noteTitle,
                             EditText noteDescription) {
        mNoteInfo = DataManager.getInstance().getNotes().get(mNotePosition);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNoteInfo.getCourse());
        myCourseSpinner.setSelection(courseIndex);
        noteTitle.setText(mNoteInfo.getTitle());
        noteDescription.setText(mNoteInfo.getText());
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(NOTE_POSITION)) {
//           mNoteInfo = intent.getParcelableExtra(NOTE_POSITION);
            int note_position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
            mIsNewNote = note_position == POSITION_NOT_SET;
            if (mIsNewNote) {
                createNewNote();
            } else {
                mNoteInfo = DataManager.getInstance().getNotes().get(note_position);
            }
        } else {
            Log.d(LOG_TAG, "This intent has no extra data my boss");
        }
    }

    private void createNewNote() {
        DataManager dataManager = DataManager.getInstance();
        mNotePosition = dataManager.createNewNote();
        mNoteInfo = dataManager.getNotes().get(mNotePosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCancelling) {
            Log.i(LOG_TAG,"Note Position is at position"+mNotePosition);
            if (mIsNewNote) {
                DataManager.getInstance().removeNote(mNotePosition);
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }
        Log.d(LOG_TAG,"onPause");
    }

    private void storePreviousNoteValues() {
        mNoteInfo = DataManager.getInstance().getNotes().get(mNotePosition);
        mOriginalNoteCourseId = mNoteInfo.getCourse().getCourseId();
        CourseInfo courseInfo = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNoteInfo.setCourse(courseInfo);
        mNoteInfo.setTitle(mOriginalNoteCourseId);
        mNoteInfo.setText(mOriginalNoteCourseId);
    }

    private void saveNote() {
        mNoteInfo = DataManager.getInstance().getNotes().get(mNotePosition);
        mNoteInfo.setCourse((CourseInfo) courseSpinner.getSelectedItem());
        mNoteInfo.setTitle(mTitle.getText().toString());
        mNoteInfo.setText(mDescription.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes_keeper, menu);
        return true;
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
            return true;
        } else if (id == R.id.action_cancel) {
            isCancelling = true;
            finish();
        } else if (id == R.id.action_next) {
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    private void moveNext() {
        saveNote();

        ++mNotePosition;
        mNoteInfo = DataManager.getInstance().getNotes().get(mNotePosition);

        saveOriginalNoteValues();
        displayNote(courseSpinner, mTitle, mDescription);
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        CourseInfo courseInfo = (CourseInfo) courseSpinner.getSelectedItem();
        String subject = mTitle.getText().toString();
        String text = "Checkout what I learned in the Pluralsight course \"" + courseInfo
                .getTitle() + "\"\n" + mDescription.getText().toString();
        String mime_type = "message/rfc2822";
        Intent sendMailIntent = new Intent(Intent.ACTION_SEND);
        sendMailIntent.setType(mime_type);
        sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendMailIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(sendMailIntent);


    }
}
