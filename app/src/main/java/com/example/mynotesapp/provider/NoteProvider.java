package com.example.mynotesapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.example.mynotesapp.db.DatabaseContract;
import com.example.mynotesapp.db.NoteHelper;

import androidx.annotation.NonNull;

public class NoteProvider extends ContentProvider {

    private static final int NOTE = 1;
    private static final int NOTE_ID = 2;
    private NoteHelper noteHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // content://com.example.mynotesapp/note
        sUriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.NoteColumns.TABLE_NAME, NOTE);

        // content://com.example.mynotesapp/note/id
        sUriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.NoteColumns.TABLE_NAME + "/#",
                NOTE_ID);
    }

    public NoteProvider() {

    }

    @Override
    public boolean onCreate() {
        noteHelper = NoteHelper.getInstance(getContext());
        noteHelper.open();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] strings, String s, String[] strings1, String s1) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case NOTE:
                cursor = noteHelper.queryAll();
                break;
            case NOTE_ID:
                cursor = noteHelper.queryById(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        long added;
        switch (sUriMatcher.match(uri)) {
            case NOTE:
                added = noteHelper.insert(contentValues);
                break;
            default:
                added = 0;
                break;
        }

        getContext().getContentResolver().notifyChange(DatabaseContract.NoteColumns.CONTENT_URI, null);

        return Uri.parse(DatabaseContract.NoteColumns.CONTENT_URI + "/" + added);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int updated;
        switch (sUriMatcher.match(uri)){
            case NOTE_ID:
                updated = noteHelper.update(uri.getLastPathSegment(), contentValues);
                break;
            default:
                updated = 0;
                break;
        }

        getContext().getContentResolver().notifyChange(DatabaseContract.NoteColumns.CONTENT_URI, null);
        return updated;
    }

    @Override
    public int delete(@NonNull  Uri uri, String s, String[] strings) {
        int deleted;
        switch (sUriMatcher.match(uri)) {
            case NOTE_ID:
                deleted = noteHelper.deleteById(uri.getLastPathSegment());
                break;
            default:
                deleted = 0;
                break;
        }

        getContext().getContentResolver().notifyChange(DatabaseContract.NoteColumns.CONTENT_URI, null);

        return deleted;
    }
}
