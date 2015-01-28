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
public class CategoriesDbAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
   private static final String DATABASE_CREATE_CATEGORY =
        "CREATE TABLE category(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "name TEXT NOT NULL UNIQUE);";

    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CATEGORY = "category";

    private final Context mCtx;
    private NotesDbAdapter notesDB;

    private static long default_category = 1;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_CATEGORY);

            /**
             * Crea una categoria por defecto
             */
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_NAME, "default");

            default_category = db.insert(DATABASE_CATEGORY, null, initialValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            db.execSQL("DROP TABLE IF EXISTS category");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public CategoriesDbAdapter(Context ctx, NotesDbAdapter notes) {
        this.mCtx = ctx;
        notesDB = notes;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws android.database.SQLException if the database could be neither opened or created
     */
    public CategoriesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new category using the name. If the category is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param name the name of the category
     * @return rowId or -1 if failed
     */
    public long createCategory(String name){
        if(name.length() == 0){
            return -1;
        }
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);

        try{
            return mDb.insertOrThrow(DATABASE_CATEGORY,null,initialValues);
        }catch(Exception e){return -1;}
    }

    /**
     * Delete the category with the given rowId
     *
     * @param rowId id of category to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteCategory(long rowId) {
        //Actualizar balores de las notas
        if(rowId == CategoriesDbAdapter.default_category){
            return false;
        }
        Cursor c = fetchCategory(rowId);
        String name = c.getString(c.getColumnIndex("name"));
        notesDB.updateCategories(name, "default");

        return mDb.delete(DATABASE_CATEGORY, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Update the category using the details provided. The category to be updated is
     * specified using the rowId, and it is altered to use the name
     * values passed in
     *
     * @param rowId id of note to update
     * @param name value to set note title to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateCategory(long rowId, String name) {
        if(name.length() == 0){
            return false;
        }
        Cursor c = this.fetchCategory(rowId);
        String old = c.getString(c.getColumnIndex(KEY_NAME));
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        //Actualizar valor en las notas
        notesDB.updateCategories(old, name);

        return mDb.update(DATABASE_CATEGORY, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all categories in the database
     *
     * @return Cursor over all categories
     */
    public Cursor fetchAllCategories() {

        //Ultimo valor, ordena los resultados -> null no ordena
        return mDb.query(DATABASE_CATEGORY, new String[] {KEY_ROWID, KEY_NAME}, null, null, null,
                null, KEY_NAME);
    }

    /**
     * Return a Cursor positioned at the category that matches the given rowId
     *
     * @param rowId id of category to retrieve
     * @return Cursor positioned to matching category, if found
     * @throws android.database.SQLException if note could not be found/retrieved
     */
    public Cursor fetchCategory(long rowId) throws SQLException {

        Cursor mCursor =
                mDb.query(true, DATABASE_CATEGORY, new String[] {KEY_ROWID,
                                KEY_NAME}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * @return : id of the default category
     */
    public static long getDefault_category(){
        return default_category;
    }
}
