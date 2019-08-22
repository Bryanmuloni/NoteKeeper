package com.bryanville.noteskeeper.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sirikye Brian on 6/24/2019.
 * bryanmuloni@gmail.com
 */
public final class NoteKeeperProviderContract {
    public static final String CONTENT_AUTHORITY = "com.bryanville.noteskeeper.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private NoteKeeperProviderContract() {
    }

    protected interface CourseIdColumn{
        String COLUMN_COURSE_ID = "course_id";
    }
    protected interface CourseColumns{
        String COLUMN_COURSE_TITLE = "course_title";
    }

    protected interface NoteColumns{
        String COLUMN_NOTE_TITLE = "note_title";
        String COLUMN_NOTE_TEXT = "note_text";
    }

    public static final class Courses implements BaseColumns,CourseColumns,CourseIdColumn {
        public static final String COURSES_PATH = "courses";
        public static final Uri COURSES_CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI,
                COURSES_PATH);
    }

    public static final class Notes implements BaseColumns,NoteColumns,CourseIdColumn{
        public static final String NOTES_PATH = "notes";
        public static final Uri NOTES_CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI,
                NOTES_PATH);

        public static final String PATH_EXPANDED  = "notes_expanded";
        public static final Uri NOTES_CONTENT_EXPANDED_URI =
                Uri.withAppendedPath(CONTENT_AUTHORITY_URI,PATH_EXPANDED);
    }


}