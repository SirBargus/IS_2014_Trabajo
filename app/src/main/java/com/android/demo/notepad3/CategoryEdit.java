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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CategoryEdit extends Activity {

    private EditText mName;
    private EditText mIdText;
    private String oldName;
    private Long mRowId;
    private CategoriesDbAdapter mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotesDbAdapter notesDB= new NotesDbAdapter(this);
        notesDB.open();
        mDb = new CategoriesDbAdapter(this, notesDB);
        mDb.open();

        setContentView(R.layout.category_edit);
        setTitle("Edit category");

        mName = (EditText) findViewById(R.id.name);
        mIdText = (EditText) findViewById(R.id.noID);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
									: null;
		}

		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDb.fetchCategory(mRowId);
            startManagingCursor(note);
            mName.setText(note.getString(
                    note.getColumnIndexOrThrow(CategoriesDbAdapter.KEY_NAME)));
            mIdText.setText(note.getString(
                    note.getColumnIndexOrThrow(CategoriesDbAdapter.KEY_ROWID)));
            oldName = mName.getText().toString();
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
        String name = mName.getText().toString();

        if (mRowId == null) {
            long id = mDb.createCategory(name);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDb.updateCategory(mRowId, name);
        }
    }

}
