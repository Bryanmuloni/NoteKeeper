package com.bryanville.noteskeeper.database;

import android.provider.BaseColumns;

public final class NoteKeeperDatabaseContract {
    //    Required Constructor
    private NoteKeeperDatabaseContract() {
    }

    //    CourseInfo entry class
    public static final class CourseInfoEntry implements BaseColumns {
        public static final String COURSE_TABLE = "course_info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

        public static final String getQName(String columnName) {
            return COURSE_TABLE + "." + columnName;
        }

//        Create index course_info_index1 on course_info (course_title)
        public static final String INDEX1 = COURSE_TABLE + "index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX "+ INDEX1 + " ON " + COURSE_TABLE + "(" + COLUMN_COURSE_TITLE +")";

        //        CREATE TABLE course_info(course_id,course_title)
        public static final String SQL_CREATE_TABLE_COURSE = "CREATE TABLE "
                + COURSE_TABLE
                + "("
                + _ID
                + " INTEGER PRIMARY KEY, "
                + COLUMN_COURSE_ID
                + " TEXT UNIQUE NOT NULL, "
                + COLUMN_COURSE_TITLE
                + " TEXT NOT NULL)";
    }

    public static final class NoteInfoEntry implements BaseColumns {
        public static final String NOTE_TABLE = "note_info";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";

        public static final String getQName(String columnName) {
            return NOTE_TABLE + "." + columnName;
        }

        //        Create index course_info_index2 on course_info (course_title)
        public static final String INDEX2 = NOTE_TABLE + "index2";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX "+ INDEX2 + " ON " + NOTE_TABLE + "(" + COLUMN_NOTE_TITLE +")";


        //    CREATE TABLE note_info(course_id,note_title,note_text)
        public static final String SQL_CREATE_TABLE_NOTE = "CREATE TABLE "
                + NOTE_TABLE
                + "("
                + _ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NOTE_TITLE + " TEXT NOT NULL, "
                + COLUMN_NOTE_TEXT + " TEXT, "
                + COLUMN_COURSE_ID + " TEXT NOT NULL)";
    }
}
