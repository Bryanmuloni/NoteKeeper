package com.bryanville.noteskeeper;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {

    public static DataManager sDataManager;

    @BeforeClass
    public static void setUp() throws Exception{
        sDataManager = DataManager.getInstance();
    }
    @Rule
    public ActivityTestRule<MainActivity> mNoteListActivityRule = new ActivityTestRule<>
            (MainActivity.class);

    @Test
    public void createNewNote() {
        final CourseInfo courseInfo = sDataManager.getCourse("java_lang");
        final String noteTitle = "This is the title of the note";
        final String noteText = "This is the body of the note";
//       ViewInteraction fabNewNote =  onView(withId(R.id.fab));
//       fabNewNote.perform(click());
        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.new_course_spinner)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class),equalTo(courseInfo))).perform(click());
        onView(withId(R.id.new_course_spinner)).check(matches(withSpinnerText(containsString
                (courseInfo.getTitle()))));


        onView(withId(R.id.new_note_title)).perform(typeText(noteTitle));
        onView(withId(R.id.new_note_title)).check(matches(withText
                (containsString(noteTitle))));

        onView(withId(R.id.new_note_description)).perform(typeText(noteText),
                closeSoftKeyboard());
        onView(withId(R.id.new_note_description)).check(matches(withText(containsString(noteText))));

        pressBack();

//        int noteIndex = sDataManager.getNotes().size() - 1;
//        NoteInfo noteInfo = sDataManager.getNotes().get(noteIndex);
//        assertEquals(courseInfo, noteInfo.getCourse());
//        assertEquals(noteTitle, noteInfo.getTitle());
//        assertEquals(noteText, noteInfo.getText());
    }

}