package com.papp.paylist.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import com.papp.paylist.R;
import com.papp.paylist.base.BaseActivity;

import java.util.ArrayList;

public class DataManager {

    private SQLiteDatabase db;
    private Context context;
    //db paytab columns
    public static final String PAYTAB_ID = "URNO";//id paytab
    public static final String PAYTAB_DATE = "DATE";//date of the record
    public static final String PAYTAB_LSTU = "LSTU";//last time updated
    public static final String PAYTAB_DSCR = "DSCR";//description
    public static final String PAYTAB_EURO = "EURO";//amount of money
    public static final String PAYTAB_TYPE = "TYPE";//type of thr record
    public static final String PAYTAB_IORO = "IORO";//income (0) or outflow (1)
    public static final int PAYTAB_ID_IDX = 0;
    public static final int PAYTAB_DATE_IDX = 1;
    public static final int PAYTAB_LSTU_IDX = 2;
    public static final int PAYTAB_DSCR_IDX = 3;
    public static final int PAYTAB_EURO_IDX = 4;
    public static final int PAYTAB_TYPE_IDX = 5;
    public static final int PAYTAB_IORO_IDX = 6;
    public static final String TYPTAB_ID = "URNO";//id typtab
    public static final String TYPTAB_TYPE = "TYPE";//type of thr record
    public static final int TYPTAB_ID_IDX = 0;
    public static final int TYPTAB_TYPE_IDX = 1;
    public static final String UPDTAB_ID = "URNO";//id updtab
    public static final String UPDTAB_CDAT = "CDAT";//creation date
    public static final String UPDTAB_DATE = "DATE";//date of the record
    public static final String UPDTAB_DSCR = "DSCR";//description
    public static final String UPDTAB_EURO = "EURO";//amount of money
    public static final String UPDTAB_TYPE = "TYPE";//id of typtab
    public static final String UPDTAB_IORO = "IORO";//income (0) or outflow (1)
    public static final String UPDTAB_UPAY = "UPAY";//id of paytab
    public static final int UPDTAB_ID_IDX = 0;
    public static final int UPDTAB_CDAT_IDX = 1;
    public static final int UPDTAB_DATE_IDX = 2;
    public static final int UPDTAB_DSCR_IDX = 3;
    public static final int UPDTAB_EURO_IDX = 4;
    public static final int UPDTAB_TYPE_IDX = 5;
    public static final int UPDTAB_IORO_IDX = 6;
    public static final int UPDTAB_UPAY_IDX = 7;
    //db info
    private static final String DB_NAME = "PAYLIST_DB";
    private static final int DB_VERSION = 1;
    private static final String PAYTAB = "PAYTAB";//table with up to date data
    private static final String TYPTAB = "TYPTAB";//table with types
    private static final String UPDTAB = "UPDTAB";//table with history of updates

    public DataManager(Context context){
        this.context = context;
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public void paytabInsert(Bundle bndl){
        String query = "INSERT INTO "+PAYTAB+
                " ("+PAYTAB_DATE+", "+PAYTAB_LSTU+", "+PAYTAB_DSCR+", "+PAYTAB_EURO+", "+PAYTAB_TYPE+", "+PAYTAB_IORO+") "+
                "VALUES ("+"'"+bndl.getString(PAYTAB_DATE)+"', '"+bndl.getString(PAYTAB_LSTU)+"', '"+bndl.getString(PAYTAB_DSCR)+"', "+
                bndl.getDouble(PAYTAB_EURO)+", '"+bndl.getString(PAYTAB_TYPE)+"', "+bndl.getInt(PAYTAB_IORO)+");";
        Log.i("paytabInsert() = ", query);
        db.execSQL(query);
    }

    public void paytabUpdate(Integer urno, Bundle bndl){
        String query = "UPDATE "+PAYTAB+
                " SET "+PAYTAB_DATE+" = '"+bndl.getString(PAYTAB_DATE)+"', "+
                PAYTAB_LSTU+" = '"+bndl.getString(PAYTAB_LSTU)+"', "+
                PAYTAB_DSCR+" = '"+bndl.getString(PAYTAB_DSCR)+"', "+
                PAYTAB_EURO+" = "+bndl.getDouble(PAYTAB_EURO)+", "+
                PAYTAB_TYPE+" = '"+bndl.getString(PAYTAB_TYPE)+"', "+
                PAYTAB_IORO+" = "+bndl.getInt(PAYTAB_IORO)+
                " WHERE "+bndl.getInt(PAYTAB_ID)+" = "+urno+";";
        Log.i("paytabUpdate() = ", query);
        db.execSQL(query);
    }

    public void paytabDelete(Integer urno){
        String query = "DELETE FROM "+PAYTAB+" WHERE "+PAYTAB_ID+" = '"+urno+"';";
        Log.i("paytabDelete() = ", query);
        db.execSQL(query);
    }

    public Cursor paytabSelectAll(){
        Cursor c = db.rawQuery("SELECT * FROM "+PAYTAB+" ORDER BY "+PAYTAB_DATE+" DESC;", null);
        return c;
    }

    public Cursor paytabSelect(Bundle bundle){
        String query = "SELECT * FROM "+PAYTAB;
        ArrayList<String> arr = new ArrayList<>();
        String date1 = bundle.getString(BaseActivity.START_DATE);
        String date2 = bundle.getString(BaseActivity.END_DATE);
        String fltr_type = bundle.getString(BaseActivity.FILTER_TYPE);
        String fltr_dscr = bundle.getString(BaseActivity.FILTER_DSCR);
        int fltr_ioro = bundle.getInt(BaseActivity.FILTER_IORO);
        if(!fltr_type.equals(context.getResources().getString(R.string.empty_type)))
            arr.add(PAYTAB_TYPE+" = '"+fltr_type+"'");
        if(!fltr_dscr.isEmpty())
            arr.add(PAYTAB_DSCR+" LIKE '%"+fltr_dscr+"%'");
        if(fltr_ioro != BaseActivity.BOTH)
            arr.add(PAYTAB_IORO+" = "+fltr_ioro);
        if(!date1.isEmpty()){
            if(date2.isEmpty())
                arr.add(PAYTAB_DATE+" = '"+date1+"'");
            else
                arr.add(PAYTAB_DATE+" BETWEEN '"+date1+"' AND '"+date2+"'");
        }
        if(arr.size() > 0) {
            query += " WHERE ";
            for (int i = 0; i < arr.size(); i++) {
                if (i == arr.size() - 1)
                    query += arr.get(i);
                else
                    query += arr.get(i) + " AND ";
            }
        }
        query += " ORDER BY " + PAYTAB_DATE + " DESC;";
        Log.i("paytabSelect() = ", query);
        Cursor c = db.rawQuery(query, null);
        return c;
    }

    public Cursor paytabSelectById(Integer urno){
        Cursor c = db.rawQuery("SELECT * FROM "+PAYTAB+" WHERE "+PAYTAB_ID+" = "+urno+";", null);
        return c;
    }

    public void typtabInsert(String type){
        String query = "INSERT INTO "+TYPTAB+
                " ("+TYPTAB_TYPE+") VALUES ('"+type+"');";
        Log.i("typtabInsert() = ", query);
        db.execSQL(query);
    }

    public Cursor typtabSelectTypes(){
        Cursor c = db.rawQuery("SELECT "+TYPTAB_TYPE+" FROM "+TYPTAB+";", null);
        return c;
    }

    public void updtabInsert(Bundle bndl){
        String query = "INSERT INTO "+UPDTAB+
                " ("+UPDTAB_CDAT+", "+UPDTAB_DATE+", "+UPDTAB_DSCR+", "+UPDTAB_EURO+", "+UPDTAB_TYPE+", "+UPDTAB_IORO+", "+UPDTAB_UPAY+") "+
                "VALUES ("+"'"+bndl.getString(UPDTAB_CDAT)+"', '"+bndl.getString(UPDTAB_DATE)+"', '"+bndl.getString(UPDTAB_DSCR)+"', "+
                bndl.getDouble(UPDTAB_EURO)+", '"+bndl.getString(UPDTAB_TYPE)+"', "+bndl.getInt(UPDTAB_IORO)+", "+bndl.getInt(UPDTAB_UPAY)+");";
        Log.i("updtabInsert() = ", query);
        db.execSQL(query);
    }

    public int countRows(String table){
        Cursor c = db.rawQuery("SELECT * FROM "+table+";", null);
        return c.getCount();
    }

    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {

        public CustomSQLiteOpenHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            String payTable = "CREATE TABLE "+PAYTAB+" ("+PAYTAB_ID+" INTEGER PRIMARY KEY NOT NULL, "+
                    PAYTAB_DATE+" TEXT NOT NULL, "+
                    PAYTAB_LSTU+" TEXT NOT NULL, "+
                    PAYTAB_DSCR+" TEXT NOT NULL, "+
                    PAYTAB_EURO+" REAL NOT NULL, "+
                    PAYTAB_TYPE+" TEXT NOT NULL, "+
                    PAYTAB_IORO+" INTEGER NOT NULL);";
            db.execSQL(payTable);
            String typTable = "CREATE TABLE "+TYPTAB+" ("+TYPTAB_ID+" INTEGER PRIMARY KEY NOT NULL, "+
                    TYPTAB_TYPE+" TEXT NOT NULL);";
            db.execSQL(typTable);
            String updTable = "CREATE TABLE "+UPDTAB+" ("+UPDTAB_ID+" INTEGER PRIMARY KEY NOT NULL, "+
                    UPDTAB_CDAT+" TEXT NOT NULL, "+
                    UPDTAB_DATE+" TEXT NOT NULL, "+
                    UPDTAB_DSCR+" TEXT NOT NULL, "+
                    UPDTAB_EURO+" REAL NOT NULL, "+
                    UPDTAB_TYPE+" TEXT NOT NULL, "+
                    UPDTAB_IORO+" INTEGER NOT NULL, "+
                    UPDTAB_UPAY+" INTEGER NOT NULL);";
            db.execSQL(updTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}

    }

}
