package com.bryanville.noteskeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bryanville.noteskeeper.adapters.NotesListAdapter;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private NotesListAdapter mNotesListAdapter;
    private List<NoteInfo> mNoteInfoList;

//    private ArrayAdapter<NoteInfo> noteInfoArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });
        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        noteInfoArrayAdapter.notifyDataSetChanged();
        mNotesListAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
//        final ListView listNotes = findViewById(R.id.list_note);
//        List<NoteInfo> noteInfoList = DataManager.getInstance().getNotes();
//        noteInfoArrayAdapter = new ArrayAdapter<>(this, android.R.layout
//                .simple_list_item_1, noteInfoList);
//        listNotes.setAdapter(noteInfoArrayAdapter);
//
//        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
////                NoteInfo noteInfo = (NoteInfo) listNotes.getItemAtPosition(position);
////                intent.putExtra(NoteActivity.NOTE_POSITION, noteInfo);
//                intent.putExtra(NoteActivity.NOTE_POSITION, position);
//                startActivity(intent);
//            }
//        });
        final RecyclerView listNotes = findViewById(R.id.list_note);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listNotes.setLayoutManager(linearLayoutManager);
        mNoteInfoList = DataManager.getInstance().getNotes();
        mNotesListAdapter = new NotesListAdapter(this, mNoteInfoList);
        listNotes.setAdapter(mNotesListAdapter);
    }


}
