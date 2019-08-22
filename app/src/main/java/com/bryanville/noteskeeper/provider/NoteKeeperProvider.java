package com.bryanville.noteskeeper.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.bryanville.noteskeeper.database.NoteKeeperOpenHelper;

import static com.bryanville.noteskeeper.database.NoteKeeperDatabaseContract.CourseInfoEntry;
import static com.bryanville.noteskeeper.database.NoteKeeperDatabaseContract.NoteInfoEntry;
import static com.bryanville.noteskeeper.provider.NoteKeeperProviderContract.CONTENT_AUTHORITY;
import static com.bryanville.noteskeeper.provider.NoteKeeperProviderContract.CourseIdColumn;
import static com.bryanville.noteskeeper.provider.NoteKeeperProviderContract.Courses;
import static com.bryanville.noteskeeper.provider.NoteKeeperProviderContract.Notes;

public class NoteKeeperProvider extends ContentProvider {

    public static final String MIME_VENDOR_TYPE = "vnd." + CONTENT_AUTHORITY + ".";
    NoteKeeperOpenHelper mDbOpenHelper;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int COURSES = 0;

    public static final int NOTES = 1;

    public static final int NOTES_EXPANDED = 2;

    private static final int NOTES_ROW = 3;

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY,
                Courses.COURSES_PATH, COURSES);
        sUriMatcher.addURI(CONTENT_AUTHORITY, Notes.NOTES_PATH, NOTES);
        sUriMatcher.addURI(CONTENT_AUTHORITY, Notes.PATH_EXPANDED, NOTES_EXPANDED);
        sUriMatcher.addURI(CONTENT_AUTHORITY, Notes.NOTES_PATH + "/#", NOTES_ROW);
    }

    public NoteKeeperProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        String mimeType = null;
        int uriMatcher = sUriMatcher.match(uri);
        switch (uriMatcher) {
            case COURSES:
                mimeType =
                        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Courses.COURSES_PATH;
                break;
            case NOTES:
                mimeType =
                        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.NOTES_PATH;
                break;
            case NOTES_EXPANDED:
                mimeType =
                        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.PATH_EXPANDED;
                break;

            case NOTES_ROW:
                mimeType =
                        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.NOTES_PATH;
                break;
        }
        return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        long rowId = -1;
        Uri rowUri = null;
        int uriMatch = sUriMatcher.match(uri);

        switch (uriMatch) {
            case NOTES:
                rowId = db.insert(NoteInfoEntry.NOTE_TABLE, null, values);
                rowUri = ContentUris.withAppendedId(Notes.NOTES_CONTENT_URI, rowId);
                break;
            case COURSES:
                rowId = db.insert(CourseInfoEntry.COURSE_TABLE, null, values);
                rowUri = ContentUris.withAppendedId(Courses.COURSES_CONTENT_URI, rowId);
                break;
            case NOTES_EXPANDED:
                break;
        }
        return rowUri;
    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new NoteKeeperOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        int uriMatcher = sUriMatcher.match(uri);
        switch (uriMatcher) {
            case COURSES:
                cursor = db.query(CourseInfoEntry.COURSE_TABLE, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case NOTES:
                cursor = db.query(NoteInfoEntry.NOTE_TABLE, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case NOTES_EXPANDED:
                cursor = notesExpandedQuery(db, projection, selection, selectionArgs, sortOrder);
                break;
            case NOTES_ROW:
                long rowId = ContentUris.parseId(uri);
                String rowSelection = NoteInfoEntry._ID + " = ?";
                String[] rowSelectionArgs = new String[]{Long.toString(rowId)};
                cursor = db.query(NoteInfoEntry.NOTE_TABLE, projection, rowSelection,
                        rowSelectionArgs, null, null, null);
                break;
        }
        return cursor;
    }

    private Cursor notesExpandedQuery(SQLiteDatabase db, String[] projection, String selection,
                                      String[] selectionArgs, String sortOrder) {
        String[] columns = new String[projection.length];
        for (int idx = 0; idx < projection.length; idx++) {
            columns[idx] =
                    projection[idx].equals(BaseColumns._ID) ||
                            projection[idx].equals(CourseIdColumn.COLUMN_COURSE_ID) ?
                            NoteInfoEntry.getQName(projection[idx]) : projection[idx];
        }
        String tablesWithJoin = NoteInfoEntry.NOTE_TABLE + " JOIN " +
                CourseInfoEntry.COURSE_TABLE + " ON " +
                NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
                CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

        return db.query(tablesWithJoin, columns, selection, selectionArgs, null, null, sortOrder);

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
