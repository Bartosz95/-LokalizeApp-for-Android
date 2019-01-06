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

    private static final String TABLE2_NAME = "phoneNumbers";
    private static final String TABLE2_COLUMN1 = "ID";
    private static final String TABLE2_COLUMN2 = "name";
    private static final String TABLE2_COLUMN3 = "number";


    public Database(Activity activity) {
        this.activity = activity;
        openDatabase();
        //dropTable(TABLE1_NAME);
        //dropTable(TABLE2_NAME);
        //createTable(TABLE1_NAME);
        //createTable(TABLE2_NAME);
        //insertDefaultStatement();
        //insertPhoneNumber("Dad", "616920045");
        //showTable(TABLE1_NAME);
        //showTable(TABLE2_NAME);
        //editPhoneNumber(1,"Mom", "445284982");
        //showTable(TABLE1_NAME);
        showTable(TABLE2_NAME);
        //insertData();
        //editData("Hi");
        //getStatement();
        finish();
    }

    private void openDatabase() {
        File storagePath = activity.getApplication().getFilesDir();
        String myDbPath = storagePath + "/" + "myDatabase";
        try {
            db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        } catch (SQLiteException e) {
            toastMessage("Error openDatabase: " + e.getMessage());
            finish();
        }
    }

    private void createTable(String tableName){
        switch (tableName){
            case TABLE1_NAME: {
                String query = String.format("CREATE TABLE %s ( %s TEXT);", TABLE1_NAME, TABLE1_COLUMN1);
                try {
                    db.beginTransaction();
                    db.execSQL(query);
                    db.setTransactionSuccessful();
                    toastMessage(String.format("Table '%s' was created",TABLE1_NAME));
                } catch (SQLiteException e) {
                    toastMessage("Error insertData: " + e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            }

            case TABLE2_NAME: {
                String query = String.format("CREATE TABLE %s ( %s integer PRIMARY KEY autoincrement, %s TEXT, %s TEXT);", TABLE2_NAME, TABLE2_COLUMN1, TABLE2_COLUMN2, TABLE2_COLUMN3);
                try {
                    db.execSQL(query);
                    db.beginTransaction();
                    db.setTransactionSuccessful();
                    toastMessage(String.format("Table '%s' was created",TABLE2_NAME));
                } catch (SQLiteException e) {
                    toastMessage("Error insertData: " + e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            }

        }
    }

    private void dropTable(String tableName) {
        switch (tableName){
            case TABLE1_NAME: {
                try {
                    db.execSQL(String.format("DROP TABLE '%s'; ", TABLE1_NAME));
                    toastMessage(String.format("'%s'- dropped!!", TABLE1_NAME));
                } catch (Exception e) {
                    toastMessage("Error dropTable:\n" + e.getMessage());
                }
                break;
            }
            case TABLE2_NAME: {
                try {
                    db.execSQL(String.format("DROP TABLE '%s'; ", TABLE2_NAME));
                    toastMessage(String.format("'%s'- dropped!!",TABLE2_NAME));
                } catch (Exception e) {
                    toastMessage("Error dropTable:\n" + e.getMessage());
                }
            }
            break;
            }
    }

    private void insertDefaultStatement() {
        String statementText = "Please came ASAP. I need you because I am in danger !!!";
        String query = String.format("INSERT INTO %s(%s) values ('%s');", TABLE1_NAME, TABLE1_COLUMN1, statementText);
        db.beginTransaction();
        try {
            db.execSQL(query);
            db.setTransactionSuccessful();
            toastMessage("insert new statement");
        } catch (SQLiteException e) {
            toastMessage("Error insertData: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void insertPhoneNumber(String name, String nubmer) {
        String query = String.format("insert into %s(%s,%s) values ('%s','%s');", TABLE2_NAME, TABLE2_COLUMN2, TABLE2_COLUMN3, name, nubmer);
        db.beginTransaction();
        try {
            db.execSQL(query);
            db.setTransactionSuccessful();
            toastMessage("insert new statement");
        } catch (SQLiteException e) {
            toastMessage("Error insertData: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private void editPhoneNumber(int id, String name, String number) {
        String query = String.format("UPDATE %s SET %s = '%s', %s = '%s' WHERE %s = '%s'", TABLE2_NAME, TABLE2_COLUMN2, name, TABLE2_COLUMN3, number, TABLE2_COLUMN1, id);
        try {
            db.beginTransaction();
            db.execSQL(query);
            db.setTransactionSuccessful();
            toastMessage("insert new statement");
        } catch (SQLiteException e) {
            toastMessage("Error insertData: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /*public String[][] getPhoneNumbersList(){
        String[] line = new String[3];
            }*/

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
                    cursorRow.append("\n");
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

    private void showTable(String tableName) {
        try {
            String sql = "select * from " + tableName ;
            Cursor c = db.rawQuery(sql, null);
            toastMessage(String.format("Table %s:\n", tableName) + showCursor(c) );
        } catch (Exception e) {
            toastMessage("Error showTable: " + e.getMessage());
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


