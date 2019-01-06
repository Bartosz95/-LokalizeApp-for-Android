package com.example.home.projekt;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

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
        //dropTable(TABLE1_NAME);
        //dropTable(TABLE2_NAME);
        //createTable(TABLE1_NAME);
        //createTable(TABLE2_NAME);
    }

    public String getStatement(){
        String ret = "";
        try {
            openDatabase();
            db.beginTransaction();
            String query = String.format("SELECT %s FROM %s",TABLE1_COLUMN1 , TABLE1_NAME);
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToPosition(0);
            ret = cursor.getString(0);
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

    public void insertPhoneNumber(String name, String nubmer) {
        String sql = String.format("select * from %s WHERE %s='%s'", TABLE2_NAME, TABLE2_COLUMN3, nubmer);
        try {
            openDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToPosition(-1);
            if(!cursor.moveToNext()) {
                try {
                    String query = String.format("insert into %s(%s,%s) values ('%s','%s');", TABLE2_NAME, TABLE2_COLUMN2, TABLE2_COLUMN3, name, nubmer);
                    db.execSQL(query);
                    toastMessage("insert new contact");
                } catch (SQLiteException e) {
                    toastMessage("Error insertData: " + e.getMessage());
                }
            } else {
                toastMessage(String.format("Contact with number: %s exists", nubmer));
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            toastMessage("Error insertData: " + e.getMessage());
        }  finally {
            db.endTransaction();
            finish();
        }
    }

    public ArrayList<ArrayList<String>> getPhoneNumbersList(){
        String sql = "select * from " + TABLE2_NAME ;
        ArrayList<ArrayList<String>> Data = new ArrayList<ArrayList<String>>();
        try {
            openDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                ArrayList<String> Row = new ArrayList<String>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Row.add(cursor.getString(i));
                }
                Data.add(Row);
            }
            return Data;
        } catch (Exception e){
            toastMessage("Error get phone numbers: " + e.getMessage());
        } finally {
            finish();
            return Data;
        }
    }

    public void editPhoneNumber(int id, String name, String number) {
        String query = String.format("UPDATE %s SET %s = '%s', %s = '%s' WHERE %s = '%s'", TABLE2_NAME, TABLE2_COLUMN2, name, TABLE2_COLUMN3, number, TABLE2_COLUMN1, id);
        try {
            openDatabase();
            db.beginTransaction();
            db.execSQL(query);
            db.setTransactionSuccessful();
            toastMessage("edit phone number: " + id);
        } catch (SQLiteException e) {
            toastMessage("Error insertData: " + e.getMessage());
        } finally {
            db.endTransaction();
            finish();
        }
    }

    public void deletePhoneNumber(int id){
        String query = String.format("DELETE %s WHERE %s = '%s'", TABLE2_NAME, TABLE2_COLUMN1, id);
        try {
            openDatabase();
            db.beginTransaction();
            db.execSQL(query);
            db.setTransactionSuccessful();
            toastMessage("delete phone number: " + id);
        } catch (SQLiteException e) {
            toastMessage("Error insertData: " + e.getMessage());
        } finally {
            db.endTransaction();
            finish();
        }
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
                    openDatabase();
                    db.beginTransaction();
                    db.execSQL(query);
                    String statementText = "Please come ASAP. I need you because I am in danger !!!";
                    query = String.format("INSERT INTO %s(%s) values ('%s');", TABLE1_NAME, TABLE1_COLUMN1, statementText);
                    db.execSQL(query);
                    //insertDefaultStatement();
                    db.setTransactionSuccessful();
                    toastMessage(String.format("Table '%s' was created",TABLE1_NAME));
                } catch (SQLiteException e) {
                    toastMessage("Error insertData: " + e.getMessage());
                } finally {
                    db.endTransaction();
                    finish();
                }
                break;
            }

            case TABLE2_NAME: {
                String query = String.format("CREATE TABLE %s ( %s integer PRIMARY KEY autoincrement, %s TEXT, %s TEXT);", TABLE2_NAME, TABLE2_COLUMN1, TABLE2_COLUMN2, TABLE2_COLUMN3);
                try {
                    openDatabase();
                    db.beginTransaction();
                    db.execSQL(query);
                    db.setTransactionSuccessful();
                    toastMessage(String.format("Table '%s' was created",TABLE2_NAME));
                } catch (SQLiteException e) {
                    toastMessage("Error insertData: " + e.getMessage());
                } finally {
                    db.endTransaction();
                    finish();
                }
                break;
            }
        }
    }

    private void dropTable(String tableName) {
        switch (tableName){
            case TABLE1_NAME: {
                try {
                    openDatabase();
                    db.execSQL(String.format("DROP TABLE '%s'; ", TABLE1_NAME));
                    toastMessage(String.format("'%s'- dropped!!", TABLE1_NAME));
                } catch (Exception e) {
                    toastMessage("Error dropTable:\n" + e.getMessage());
                } finally {
                    finish();
                }
                break;
            }
            case TABLE2_NAME: {
                try {
                    openDatabase();
                    db.execSQL(String.format("DROP TABLE '%s'; ", TABLE2_NAME));
                    toastMessage(String.format("'%s'- dropped!!",TABLE2_NAME));
                } catch (Exception e) {
                    toastMessage("Error dropTable:\n" + e.getMessage());
                } finally {
                    finish();
                }
            }
            break;
            }
    }

    private void insertDefaultStatement() {
        String statementText = "Please came ASAP. I need you because I am in danger !!!";
        String query = String.format("INSERT INTO %s(%s) values ('%s');", TABLE1_NAME, TABLE1_COLUMN1, statementText);
        try {
            openDatabase();
            db.beginTransaction();
            db.execSQL(query);
            db.setTransactionSuccessful();
            toastMessage("insert new statement");
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


