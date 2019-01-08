package com.bryanville.noteskeeper;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewNoteActivity extends AppCompatActivity {
    private Spinner courseSpinner;
    private NoteInfo mNoteInfo;
    private EditText mDescription;
    private EditText mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        courseSpinner = findViewById(R.id.new_course_spinner);
        List<CourseInfo> courseInfoList = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> courseInfoArrayAdapter = new ArrayAdapter<>(this, android.R
                .layout.simple_list_item_1, courseInfoList);
        courseInfoArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);

        mTitle = findViewById(R.id.new_note_title);
        mDescription = findViewById(R.id.new_note_description);
        courseSpinner.setAdapter(courseInfoArrayAdapter);
    }

    public void saveNewNote(View view) {
        String note_title = mTitle.getText().toString();
        String note_description = mDescription.getText().toString();
        CourseInfo course_title = (CourseInfo) courseSpinner.getSelectedItem();

        if (!note_title.isEmpty() && !note_description.isEmpty()){
//            List<NoteInfo> notes = new ArrayList<>();
            mNoteInfo = new NoteInfo(course_title,note_title,note_description);
//            notes.add(mNoteInfo);
            mNoteInfo.setCourse(course_title);
            mNoteInfo.setTitle(note_title);
            mNoteInfo.setText(note_description);
            Toast.makeText(this,mNoteInfo.getTitle()+" saved successfully",Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(this,"Note info must be filled.",Toast.LENGTH_SHORT).show();
        }
    }
}
