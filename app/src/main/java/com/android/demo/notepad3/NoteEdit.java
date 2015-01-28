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

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoteEdit extends Activity {

    private EditText mTitleText;
    private EditText mBodyText;
    private EditText mIdText;
    private String categoryNote;
    private Long mRowId;
    private NotesDbAdapter mDbHelper;
    private CategoriesDbAdapter mDb;
    private HashMap<String, Long> category_name_id;

    /**
     * Crea la actividad
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mDb = new CategoriesDbAdapter(this, mDbHelper);
        mDb.open();
        categoryNote = "default";
        category_name_id = new HashMap<>();

        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mIdText = (EditText) findViewById(R.id.noID);

        Button confirmButton = (Button) findViewById(R.id.confirm);
        Button delete = (Button) findViewById(R.id.delete_category);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
									: null;
		}

		populateFields();

        fillSpinner();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                categoryNote = "default";
                fillSpinner();
            }
        });
    }

    /**
     * Rellena los campos
     */
    private void populateFields() {
        long id;
        Cursor c;

        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            id = note.getLong(note.getColumnIndexOrThrow((NotesDbAdapter.KEY_CATEGORY)));
            c = mDb.fetchCategory(id);
            categoryNote = c.getString(c.getColumnIndex(NotesDbAdapter.KEY_NAME));
            mIdText.setHint(mRowId.toString());
        }else{
            mIdText.setHint("***");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        Spinner spinner = (Spinner)findViewById(R.id.editSpinner);
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        String category = spinner.getSelectedItem().toString();
        long idCategory = category_name_id.get(category);
        long id;

        if (mRowId == null) {
            try {
                id = mDbHelper.createNote(title, body, idCategory);if (id > 0) {
                    mRowId = id;
                }
            }catch(SQLiteConstraintException e){}
        } else {
            mDbHelper.updateNote(mRowId, title, body, idCategory);
        }
    }

    /**
     * Rellena el contenido del spinner
     */
    private void fillSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.editSpinner);
        List<String> list = new ArrayList<String>();
        Cursor c = mDb.fetchAllCategories();

        list.add(categoryNote);
        while(c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(CategoriesDbAdapter.KEY_NAME));
            Long id = c.getLong(c.getColumnIndex(CategoriesDbAdapter.KEY_ROWID));
            category_name_id.put(name, id);
            if(!name.equals(categoryNote))
            {
                list.add(name);
            }
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }
}
