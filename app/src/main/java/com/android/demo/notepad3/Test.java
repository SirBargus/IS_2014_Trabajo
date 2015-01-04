package com.android.demo.notepad3;

import android.database.sqlite.SQLiteConstraintException;

import java.sql.SQLException;

/**
 * Created by agustin on 3/1/15.
 */
public class Test {

    public static void go_test(NotesDbAdapter mdB, CategoriesDbAdapter mdB2){
        pruebas_unitarias(mdB, mdB2);
        pruebas_volumen();
        pruebas_sobrecarga();
    }

    //Private methods

    /**
     * Realiza las pruebas unitarias al programa
     */
    private static void pruebas_unitarias(NotesDbAdapter mdB, CategoriesDbAdapter mdB2){
        try {
            create_nota(mdB);
            borrar_nota(mdB);
            modificar_nota(mdB);
            crear_categoria(mdB2);
            borrar_categoria(mdB2);
            modificar_categoria(mdB2);
        }catch(SQLiteConstraintException e){};
    }

    /**
     * Realiza las pruebas de volumen
     */
    private static void pruebas_volumen(){

    }

    /**
     * Realiza las pruebas de sobrecarga
     */
    private static void pruebas_sobrecarga(){

    }

    /**
     * Pruebas unitarias de crear notas
     */
    private static void create_nota(NotesDbAdapter mdB) throws SQLiteConstraintException{
        //clases de equivalencia validas
        long id = mdB.createNote("Test", "Test", CategoriesDbAdapter.getDefault_category());
        android.util.Log.d("Test - crear nota - OK: ", "" + id);

        //clases de equivalencia no validas
        try {
            android.util.Log.d("Test - crear nota - FAIL 2: ", "" + mdB.createNote(null, "Test",
                    CategoriesDbAdapter.getDefault_category()));
        }catch(Exception e){
            android.util.Log.d("Test - crear nota - FAIL 2: ", "-1");
        }
        android.util.Log.d("Test - crear nota - FAIL 3: ", "" + mdB.createNote("", "Test",
                CategoriesDbAdapter.getDefault_category()));
        try {
            android.util.Log.d("Test - crear nota - FAIL 4: ", "" + mdB.createNote("Test", null,
                    CategoriesDbAdapter.getDefault_category()));
        }catch(Exception e){
            android.util.Log.d("Test - crear nota - FAIL 4: ", "-1");
        }
        android.util.Log.d("Test - crear nota - FAIL 5: ", "" + mdB.createNote("Test", "Test", -1));
        android.util.Log.d("Test - crear nota - FAIL 6: ", "" + mdB.createNote("Test", "Test",
                1000000000));

        //borramos la nota creada
        mdB.deleteNote(id);
    }

    /**
     * Pruebas unitarias de borrar notas
     */
    private static void borrar_nota(NotesDbAdapter mdB) throws SQLiteConstraintException{
        //Creamos una nota para asegurarnos de que existe
        long id = mdB.createNote("Test_a", "Test_a", CategoriesDbAdapter.getDefault_category());

        //clases de equivalencia validas
        android.util.Log.d("Test - borrar nota - OK: ", "" + mdB.deleteNote(id));

        //clases de equivalencia no validas
        android.util.Log.d("Test - borrar nota - FAIL 2: ", "" + mdB.deleteNote(0));
        android.util.Log.d("Test - borrar nota - FAIL 3: ", "" + mdB.deleteNote(id));
    }

    /**
     * Pruebas unitarias de modificar notas
     */
    private static void modificar_nota(NotesDbAdapter mdB) throws SQLiteConstraintException{
        //creamos una nota para asegurarnos de que existe
        long id = mdB.createNote("Test", "Test", CategoriesDbAdapter.getDefault_category());

        //clases de equivalencia validas
        android.util.Log.d("Test - modificar nota - OK: ", "" + mdB.updateNote(id, "Test", "Test",
                CategoriesDbAdapter.getDefault_category()));

        //clases de equivalencia no validas
        android.util.Log.d("Test - modificar nota - FAIL 2: ", "" + mdB.updateNote(0, "Test", "Test",
                CategoriesDbAdapter.getDefault_category()));
        try {
            android.util.Log.d("Test - modificar nota - FAIL 4: ", "" + mdB.updateNote(1, null, "Test",
                    CategoriesDbAdapter.getDefault_category()));
        }catch(Exception e){
            android.util.Log.d("Test - modificar nota - FAIL 4: ", "false");
        }
        android.util.Log.d("Test - modificar nota - FAIL 5: ", "" + mdB.updateNote(1, "", "Test",
                CategoriesDbAdapter.getDefault_category()));
        try {
            android.util.Log.d("Test - modificar nota - FAIL 6: ", "" + mdB.updateNote(1, "Test", null,
                    CategoriesDbAdapter.getDefault_category()));
        }catch(Exception e){
            android.util.Log.d("Test - modificar nota - FAIL 6: ", "false");
        }
        android.util.Log.d("Test - modificar nota - FAIL 7: ", "" + mdB.updateNote(1, "Test", "Test",
                0));
        android.util.Log.d("Test - modificar nota - FAIL 8: ", "" + mdB.updateNote(1, "Test", "Test",
                10000000));
        //borramos la categoria creada
        mdB.deleteNote(id);
        android.util.Log.d("Test - modificar nota - FAIL 3: ", "" + mdB.updateNote(id, "Test",
                "Test", CategoriesDbAdapter.getDefault_category()));
    }

    /**
     * Pruebas unitarias para crear categoria
     */
    private static void crear_categoria(CategoriesDbAdapter mdB){
        //clases de equivalencia validas
        long id = mdB.createCategory("Test");
        android.util.Log.d("Test - Crear categoria - OK: ", "" + id);

        //clases de equivalencia no validas
        try {
            android.util.Log.d("Test - Crear categoria - FAIL 2: ", "" + mdB.createCategory(null));
        }catch(Exception e){
            android.util.Log.d("Test - Crear categoria - FAIL 2: ", "-1");
        }
        android.util.Log.d("Test - Crear categoria - FAIL 3: ", "" + mdB.createCategory(""));

        mdB.deleteCategory(id);
    }

    /**
     * Realiza las pruebas unitarias de borrar categoria
     */
    private static void borrar_categoria(CategoriesDbAdapter mdB){
        //Creamos la categoria para asegurarnos de que existe
        long id = mdB.createCategory("Test_2");

        //clases de equivalencia validas
        android.util.Log.d("Test - Borrar categoria - OK: ", "" + mdB.deleteCategory(id));

        //clases de equivalencia no validas
        try {
            android.util.Log.d("Test - Borrar categoria - FAIL 2: ", "" + mdB.deleteCategory(0));
        }catch(Exception e){
            android.util.Log.d("Test - Borrar categoria - FAIL 2: ", "false");
        }
        try {
            android.util.Log.d("Test - Borrar categoria - FAIL 3: ", "" + mdB.deleteCategory(id));
        }catch(Exception e){
            android.util.Log.d("Test - Borrar categoria - FAIL 3: ", "false");
        }
    }

    /**
     * Realiza las pruebas unitarias de modificar categoria
     */
    private static void modificar_categoria(CategoriesDbAdapter mdB){
        //Creamos la categoria para aseguranos de que existe
        long id = mdB.createCategory("Test_3");

        //clases de equivalencia validas
        android.util.Log.d("Test - Modificar categoria - OK: ", "" + mdB.updateCategory(id, "Test2"));

        //clases de equivalencia no validas
        try {
            android.util.Log.d("Test - Modificar categoria - FAIL 2: ", "" + mdB.updateCategory(0,
                    "Test2"));
        }catch(Exception e){
            android.util.Log.d("Test - Modificar categoria - FAIL 2: ", "false");
        }
        try {
            android.util.Log.d("Test - Modificar categoria - FAIL 4: ", "" + mdB.updateCategory(id, null));
        }catch(Exception e){
            android.util.Log.d("Test - Modificar categoria - FAIL 4: ", "false");
        }
        try {
            android.util.Log.d("Test - Modificar categoria - FAIL 5: ", "" + mdB.updateCategory(id, ""));
        }catch(Exception e){
            android.util.Log.d("Test - Modificar categoria - FAIL 5: ", "false");
        }
        //borramos la categoria creada
        mdB.deleteCategory(id);
        try {
            android.util.Log.d("Test - Modificar categoria - FAIL 3: ", "" + mdB.updateCategory(id,
                    "Test2"));
        }catch(Exception e){
            android.util.Log.d("Test - Modificar categoria - FAIL 3: ", "false");
        }
    }
}
