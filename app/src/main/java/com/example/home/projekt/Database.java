package com.example.home.projekt;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import java.io.File;

public class Database {

    private Activity activity;
    private SQLiteDatabase db;
    private static final String TABLE1_NAME = "statement";
    private static final String TABLE1_COLUMN1 = "statementText";

    public Database(Activity activity) {
        this.activity = activity;
        //openDatabase();
        //dropTable();
        //createTable();
        //insertData();
        //editData("Hi");
        //useRawQueryShowAll();
        //getStatement();
        //finish();
    }

    private void openDatabase() {
        File storagePath = activity.getApplication().getFilesDir();
        String myDbPath = storagePath + "/" + "myDatabase";
        try {
            db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            //toastMessage("-openDatabase - DB was opened");
        } catch (SQLiteException e) {
            toastMessage("Error openDatabase: " + e.getMessage());
            finish();
        }
    }

    private void dropTable() {
        try {
            String query = String.format("DROP TABLE %s", TABLE1_NAME);
            db.execSQL(query);
            toastMessage("\n-dropTable - dropped!!");
        } catch (Exception e) {
            toastMessage("\nError dropTable: " + e.getMessage());
            finish();
        }
    }

    private void createTable(){
        db.beginTransaction();
        try {
            String createTable = String.format("CREATE TABLE %s ( %s TEXT);", TABLE1_NAME, TABLE1_COLUMN1);
            db.execSQL(createTable);
            db.setTransactionSuccessful();
            toastMessage("-insertData - Table was created");
        } catch (SQLiteException e) {
            toastMessage("Error insertData: " + e.getMessage());
            finish();
        } finally {
            db.endTransaction();
        }
    }

    private void insertData() {
        db.beginTransaction();
        try {
            String statementText = "Help Help Help !!!";
            String query = String.format("INSERT INTO %s(%s) values ('%s');", TABLE1_NAME, TABLE1_COLUMN1, statementText);
            db.execSQL(query);
            db.setTransactionSuccessful();
            toastMessage("insert new statement");
        } catch (SQLiteException e) {
            toastMessage("Error insertData: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private void editData(String text) {
        db.beginTransaction();
        try {
            String query = String.format("UPDATE %s SET %s='%s';", TABLE1_NAME, TABLE1_COLUMN1, text);
            db.execSQL(query);
            db.setTransactionSuccessful();
            toastMessage("insert new statement");
        } catch (SQLiteException e) {
            toastMessage("Error insertData: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private String showCursor(Cursor cursor) {
        cursor.moveToPosition(-1);
        StringBuilder cursorData = new StringBuilder();
        /*cursorData.append("Cursor: [");
        try {

            String[] colName = cursor.getColumnNames();
            for(int i=0; i<colName.length; i++){
                String dataType = getColumnType(cursor, i);
                cursorData.append(colName[i]).append(dataType);
                if(i<colName.length-1){
                    cursorData.append(", ");
                }
            }
        } catch (Exception e){
            toastMessage("<<SCHEMA>>" + e.getMessage());
        }
        cursorData.append("]");*/
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            StringBuilder cursorRow = new StringBuilder();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                cursorRow.append(cursor.getString(i));
                if (i<cursor.getColumnCount()-1)
                    cursorRow.append(", ");
            }
            cursorData.append(cursorRow);
        }
        return cursorData.toString();
    }

    private String getColumnType(Cursor cursor, int i) {
        try {
            cursor.moveToFirst();
            int result = cursor.getType(i);
            String[] types = {"(NULL)", "(INT)", "(FLOAT)", "(STR)", "(BLOB)", "(UNK)" };
            cursor.moveToPosition(-1);
            return types[result];
        } catch (Exception e) {
            return " ";
        }
    }

    private void useRawQueryShowAll() {
        try {
            String query = String.format("SELECT * FROM %s", TABLE1_NAME);
            Cursor cursor = db.rawQuery(query, null);
            toastMessage("useRawQueryShowAll" + showCursor(cursor));
        } catch (Exception e ){
            toastMessage("Error useRawQuery: " + e.getMessage());
        }
    }

   public String getStatement(){
        String ret = "";

        try {
            openDatabase();
            db.beginTransaction();
            String query = String.format("SELECT * FROM %s", TABLE1_NAME);
            Cursor cursor = db.rawQuery(query, null);
            ret = showCursor(cursor);
        } catch (Exception e ){
            toastMessage("Error useRawQuery: " + e.getMessage());
        }finally {
            db.endTransaction();
            finish();
            return ret;
        }
    }

   public void editStatement(String text) {
        try {
            openDatabase();
            db.beginTransaction();
            String query = String.format("UPDATE %s SET %s='%s';", TABLE1_NAME, TABLE1_COLUMN1, text);
            db.execSQL(query);
            db.setTransactionSuccessful();
            toastMessage("Statement update succesfully");
        } catch (SQLiteException e) {
            toastMessage("Error insertData: " + e.getMessage());
        } finally {
            db.endTransaction();
            finish();
        }
   }

    private void finish() {
        db.close();
        //toastMessage("Database was closed");
    }

    private void toastMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
}


