/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.demo.notepad3;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Spinner;

import com.android.demo.mail.JavaMail;
import com.android.demo.mail.JavaMailActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Notepadv3 extends ListActivity {
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int ACTIVITY_SEND = 2;
    private static final int ACTIVITY_CATEGORY = 3;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int SEND_EMAIL = Menu.FIRST + 2;
    private static final int SORT_TITLE = Menu.FIRST + 3;
    private static final int SORT_CATEGORY = Menu.FIRST + 4;
    private static final int SHOW_CATEGORY = Menu.FIRST + 5;
    private static final int SHOW_ALL_NOTES = Menu.FIRST + 6;
    private static final int TEST = Menu.FIRST + 7;

    static long categoryShow = -1;

    private NotesDbAdapter mDbHelper;

    private static int show = 0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        CategoriesDbAdapter cat = new CategoriesDbAdapter(this, mDbHelper);
        cat.open();
        fillData();
        registerForContextMenu(getListView());
    }

    private void fillData() {
        Cursor notesCursor = mDbHelper.fetchAllNotes(show, categoryShow);
        startManagingCursor(notesCursor);

        // Create an array to specify the fieldswe want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.KEY_TITLE};
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
            new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        setListAdapter(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        menu.add(2, SORT_TITLE, 2, "Sort by title");
        menu.add(3, SORT_CATEGORY, 3, "Sort by category");
        menu.add(4, SHOW_CATEGORY, 4, "Show category");
        menu.add(5, SHOW_ALL_NOTES, 5, "Show all notes");
        menu.add(6, TEST, 6, "Test");
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
            case SORT_TITLE:
                show = 0;
                fillData();
                return true;
            case SORT_CATEGORY:
                show = 1;
                fillData();
                return true;
            case SHOW_CATEGORY:
                showCategory();
                return true;
            case SHOW_ALL_NOTES:
                categoryShow = -1;
                fillData();
                return true;
            case TEST:
                CategoriesDbAdapter dummy_mDb = new CategoriesDbAdapter(this, mDbHelper);
                dummy_mDb.open();
                Test.go_test(mDbHelper, dummy_mDb);
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        menu.add(0, SEND_EMAIL, 0, "Send email");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
            case SEND_EMAIL:
                AdapterContextMenuInfo info2 = (AdapterContextMenuInfo) item.getMenuInfo();
                Intent r = new Intent(this, JavaMailActivity.class);
                Cursor dummy = mDbHelper.fetchNote(info2.id);
                r.putExtra("SUBJECT", dummy.getString(dummy.getColumnIndex("title")));
                r.putExtra("BODY", dummy.getString(dummy.getColumnIndex("body")));
                startActivityForResult(r, ACTIVITY_SEND);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void showCategory(){
        Intent i = new Intent(this, CategoryList.class);
        startActivityForResult(i, ACTIVITY_CATEGORY);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
