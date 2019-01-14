package com.bryanville.noteskeeper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {
    public static DataManager sDataManagerInstance;

    @BeforeClass
    public static void classSetUp() throws Exception{
       sDataManagerInstance = DataManager.getInstance();
    }
    @Before
    public void setUp() throws Exception{
        sDataManagerInstance.getNotes().clear();
        sDataManagerInstance.initializeExampleNotes();
    }

    @Test
    public void createNewNote() {

//        Testing values for new note
        final CourseInfo course = sDataManagerInstance.getCourse("android_async");
        final  String note_title = "Course title";
        final  String note_text = "Course body text";

        int note_index =  sDataManagerInstance.createNewNote();
        NoteInfo newNote = sDataManagerInstance.getNotes().get(note_index);
        newNote.setCourse(course);
        newNote.setTitle(note_title);
        newNote.setText(note_text);

//        Comparing if notes are equally the same
        NoteInfo compareNote = sDataManagerInstance.getNotes().get(note_index);
        assertEquals(course , compareNote.getCourse());
        assertEquals(note_title,compareNote.getTitle());
        assertEquals(note_text, compareNote.getText());
    }

    @Test
    public void findSimilarNotes() throws Exception{

        final CourseInfo course = sDataManagerInstance.getCourse("android_async");
        final  String note_title = "Course title";
        final  String note_text_one = "Course body text";
        final  String note_text_two = "Course body text two";


        int note_index_one =  sDataManagerInstance.createNewNote();
        NoteInfo newNoteOne = sDataManagerInstance.getNotes().get(note_index_one);
        newNoteOne.setCourse(course);
        newNoteOne.setTitle(note_title);
        newNoteOne.setText(note_text_one);

        int note_index_two =  sDataManagerInstance.createNewNote();
        NoteInfo newNoteTwo = sDataManagerInstance.getNotes().get(note_index_two);
        newNoteTwo.setCourse(course);
        newNoteTwo.setTitle(note_title);
        newNoteTwo.setText(note_text_two);

        int found_index_one = sDataManagerInstance.findNote(newNoteOne);
        assertEquals(note_index_one,found_index_one);

        int found_index_two = sDataManagerInstance.findNote(newNoteTwo);
        assertEquals(note_index_two,found_index_two);

    }

    @Test
    public void createNewNoteOneStepCreation(){
        final CourseInfo course = sDataManagerInstance.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText = "This is the body of my test note";

        int noteIndex = sDataManagerInstance.createNewNote(course,noteTitle,noteText);
        NoteInfo compareNote = sDataManagerInstance.getNotes().get(noteIndex);
        assertEquals(course,compareNote.getCourse());
        assertEquals(noteTitle,compareNote.getTitle());
        assertEquals(noteText,compareNote.getText());
    }
}