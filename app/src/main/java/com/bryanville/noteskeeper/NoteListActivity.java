package com.bryanville.noteskeeper;

//public class NoteListActivity extends AppCompatActivity {
//    private NoteRecyclerAdapter mNoteCursorAdapter;
//    private List<NoteInfo> mNoteInfoList;
//
////    private ArrayAdapter<NoteInfo> noteInfoArrayAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_note_list);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab =  findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
//            }
//        });
//        initializeDisplayContent();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        noteInfoArrayAdapter.notifyDataSetChanged();
//        mNoteCursorAdapter.notifyDataSetChanged();
//    }
//
//    private void initializeDisplayContent() {
////        final ListView listNotes = findViewById(R.id.list_note);
////        List<NoteInfo> noteInfoList = DataManager.getInstance().getNotes();
////        noteInfoArrayAdapter = new ArrayAdapter<>(this, android.R.layout
////                .simple_list_item_1, noteInfoList);
////        listNotes.setAdapter(noteInfoArrayAdapter);
////
////        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
//////                NoteInfo noteInfo = (NoteInfo) listNotes.getItemAtPosition(position);
//////                intent.putExtra(NoteActivity.NOTE_ID, noteInfo);
////                intent.putExtra(NoteActivity.NOTE_ID, position);
////                startActivity(intent);
////            }
////        });
//        final RecyclerView listNotes = findViewById(R.id.list_note);
//        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        listNotes.setLayoutManager(linearLayoutManager);
//        mNoteInfoList = DataManager.getInstance().getNotes();
//        mNoteCursorAdapter = new NoteRecyclerAdapter(this, mNoteInfoList);
//        listNotes.setAdapter(mNoteCursorAdapter);
//    }
//
//
//}
