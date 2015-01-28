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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.android.demo.mail.JavaMailActivity;

import java.util.ArrayList;
import java.util.List;

public class CategoryList extends ListActivity {
    private static final int ACTIVITY_NOTES= 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int CATEGORY_CREATE = 3;

    private static final int INSERT_CATEGORY = Menu.FIRST;
    private static final int DELETE_CATEGORY = Menu.FIRST + 1;
    private static final int SHOW_NOTES = Menu.FIRST + 2;
    private static final int SHOW_NOTES_THIS = Menu.FIRST + 3;

    private static int posUltCategory = 0;

    private CategoriesDbAdapter mDbHelper;
    private NotesDbAdapter mDbNotes;

    private static int show = 0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
        setTitle(R.string.category);

        mDbNotes = new NotesDbAdapter(this);
        mDbNotes.open();
        mDbHelper = new CategoriesDbAdapter(this, mDbNotes);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }

    /**
     * Muestra por pantalla las categorias
     */
    private void fillData() {
        Cursor categoriesCursor = mDbHelper.fetchAllCategories();
        startManagingCursor(categoriesCursor);

        // Create an array to specify the fieldswe want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.KEY_NAME};
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter categories =
            new SimpleCursorAdapter(this, R.layout.notes_row, categoriesCursor, from, to);
        setListAdapter(categories);
        this.setSelection(posUltCategory);
    }

    /**
     * Opciones del menu creado
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_CATEGORY, 0, "Add Category");
        menu.add(1, SHOW_NOTES, 1, "Show Notes");
        return true;
    }

    /**
     * Opciones al seleccionar un item del menu
     * @param featureId
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_CATEGORY:
                createCategory();
                return true;
            case SHOW_NOTES:
                showNotes();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * Crea las opciones del menu contextual
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_CATEGORY, 0, "Delete category");
        menu.add(0, SHOW_NOTES_THIS, 0, "Show notes");
    }

    /**
     * Operaciones del menu seleccionado
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_CATEGORY:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                if(info.id != CategoriesDbAdapter.getDefault_category()) {
                    mDbHelper.deleteCategory(info.id);
                }
                fillData();
                return true;
            case SHOW_NOTES_THIS:
                AdapterContextMenuInfo info2 = (AdapterContextMenuInfo) item.getMenuInfo();
                Cursor c = mDbHelper.fetchCategory(info2.id);
                long aux = c.getLong(c.getColumnIndex(CategoriesDbAdapter.KEY_ROWID));
                Notepadv3.categoryShow = aux;
                Intent i = new Intent(this, Notepadv3.class);
                startActivityForResult(i, ACTIVITY_NOTES);
                finish();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Crea una categoria
     */
    private void createCategory() {
        Intent i = new Intent(this, CategoryEdit.class);
        startActivityForResult(i, CATEGORY_CREATE);
        posUltCategory = this.getListView().getCount();
    }

    /**
     * Muestra las notas
     */
    private void showNotes(){
        Intent i = new Intent(this, Notepadv3.class);
        Notepadv3.categoryShow = -1;
        startActivityForResult(i, ACTIVITY_NOTES);
        Notepadv3.setPosUltimaNota(0);
        finish();
    }

    /**
     * Edita una nota
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, CategoryEdit.class);
        i.putExtra(CategoriesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    /**
     * Actualiza el valor de posUltCategory
     * @param nPosUltCategory = posUltCategory
     */
    public static void setPosUltCategory(int nPosUltCategory){
        posUltCategory = nPosUltCategory;
    }
}
