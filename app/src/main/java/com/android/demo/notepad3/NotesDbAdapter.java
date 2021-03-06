/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.demo.notepad3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CATEGORY = "category";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
   private static final String DATABASE_CREATE_CATEGORY =
        "CREATE TABLE category(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "name TEXT NOT NULL);";
    private static final String DATABASE_CREATE_NOTES =
        "CREATE TABLE notes (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
        + "title TEXT NOT NULL, body TEXT NOT NULL, category INTEGER NOT NULL,"
        + "FOREIGN KEY(category) REFERENCES category(_id));";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CATEGORY = "category";


    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_CATEGORY);
            db.execSQL(DATABASE_CREATE_NOTES);

            /**
             * Crea una categoria por defecto
             */
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_NAME, "default");

            try{
                db.insertOrThrow(DATABASE_CATEGORY,null,initialValues);
            }catch(Exception e){}
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createNote(String title, String body, long category){
        long res;
        if(title == null || title.length() == 0 || category <= 0 ||
                !this.categoryExist(category)){
            return -1;
        }
        try{
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_TITLE, title);
            initialValues.put(KEY_BODY, body);
            initialValues.put(KEY_CATEGORY, category);
            res = mDb.insertOrThrow(DATABASE_TABLE, null, initialValues);
        }catch(SQLiteConstraintException e){res = -1;}
        return res;
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @param n : 0 -> sort by title, 1 -> sort by category
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes(int n, long category) {
        if(category == -1) {
            switch (n) {
                case 0:
                    return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE,
                            KEY_BODY, KEY_CATEGORY}, null, null, null, null, KEY_TITLE);
                case 1:
                    return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE,
                            KEY_BODY, KEY_CATEGORY}, null, null, null, null, KEY_CATEGORY);
                default:
                    return null;
            }
        }else{
            android.util.Log.d("Test", "Entro");
            return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE,
                    KEY_BODY, KEY_CATEGORY}, KEY_CATEGORY + "='" + category + "'", null, null, null, KEY_TITLE);

        }
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_BODY, KEY_CATEGORY}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String body, long category) {
        if(title.length() == 0 || category <= 0 || !this.categoryExist(category)){
            return false;
        }

        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_CATEGORY, category);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "='" + rowId + "' ", null) > 0;
    }


    public boolean updateCategories(String old, String neww){
        ContentValues args = new ContentValues();
        args.put(KEY_CATEGORY   , neww);
        return mDb.update(DATABASE_TABLE, args, KEY_CATEGORY + "='" + old + "'", null) > 0;
    }

    private boolean categoryExist(long rowId){
        try {
            Cursor i = mDb.query(true, DATABASE_CATEGORY, new String[]{KEY_ROWID,
                            KEY_NAME}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
            i.moveToFirst();
            if(rowId == i.getLong(i.getColumnIndex(CategoriesDbAdapter.KEY_ROWID)))
                return true;
            else
                return false;
        }catch(Exception e){
            return false;
        }
    }
}
