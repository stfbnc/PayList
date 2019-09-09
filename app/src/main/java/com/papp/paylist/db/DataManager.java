package com.papp.paylist.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataManager {

    private SQLiteDatabase db;
    //db paytab columns
    public static final String PAYTAB_ID = "URNO";
    public static final String PAYTAB_NAME = "NAME";
    public static final String PAYTAB_EURO = "EURO";
    //db info
    private static final String DB_NAME = "PAYLIST_DB";
    private static final int DB_VERSION = 1;
    private static final String PAYTAB = "PAYTAB";

    public DataManager(Context context){
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public void dbInsert(String name, double euro){
        String query = "INSERT INTO "+PAYTAB+" ("+PAYTAB_NAME+", "+PAYTAB_EURO+") VALUES ("+"'"+name+"', "+euro+");";
        Log.i("insert() = ", query);
        db.execSQL(query);
    }

    public void dbDelete(String name){
        String query = "DELETE FROM "+PAYTAB+" WHERE "+PAYTAB_NAME+" = '"+name+"';";
        Log.i("delete() = ", query);
        db.execSQL(query);
    }

    public void dbDeleteAll(){
        String query = "DELETE FROM "+PAYTAB+";";
        Log.i("deleteAll() = ", query);
        db.execSQL(query);
    }

    public Cursor dbSelectByUrno(Integer urno){
        Cursor c = db.rawQuery("SELECT "+PAYTAB_NAME+", "+PAYTAB_EURO+" FROM "+PAYTAB+" WHERE URNO = "+urno+";", null);
        return c;
    }

    public Cursor dbSelectAll(){
        Cursor c = db.rawQuery("SELECT * FROM "+PAYTAB+" ORDER BY "+PAYTAB_NAME+";", null);
        return c;
    }

    public int dbRows(){
        Cursor c = db.rawQuery("SELECT * FROM "+PAYTAB+";", null);
        return c.getCount();
    }

    public Cursor dbSearchNames(String str){
        String query = "SELECT "+PAYTAB_ID+" FROM "+PAYTAB+" WHERE "+PAYTAB_NAME+" LIKE '%"+str+"%';";
        Log.i("search() = ", query);
        Cursor c = db.rawQuery(query, null);
        return c;
    }

    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {

        public CustomSQLiteOpenHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            String newTable = "CREATE TABLE "+PAYTAB+" ("+PAYTAB_ID+" INTEGER PRIMARY KEY NOT NULL, "+
                    PAYTAB_NAME+" TEXT NOT NULL, "+PAYTAB_EURO+" REAL NOT NULL);";
            db.execSQL(newTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}

    }

}
