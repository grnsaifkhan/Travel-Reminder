package com.example.travelreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "travelreminder.db";

    private static final String TABLE = "travel_table";

    private static final String ID = "ID";

    private static final String TRAVEL_NAME= "TRAVEL_NAME";

    private static final String DESTINATION = "DESTINATION";

    private static final String TRAVEL_DATE= "TRAVEL_DATE";

    private static final String TRAVEL_TIME = "TRAVEL_TIME";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG,"create : database: "+sqLiteDatabase);
        String createTable = "CREATE TABLE "+TABLE+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,TRAVEL_NAME TEXT,DESTINATION TEXT,TRAVEL_DATE TEXT,TRAVEL_TIME TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG,"upgrade : database: "+sqLiteDatabase+" old version: "+oldVersion+" new version: "+newVersion);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String travelName,String destination,String travelDate,String travelTime){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRAVEL_NAME,travelName);
        contentValues.put(DESTINATION, destination);
        contentValues.put(TRAVEL_DATE,travelDate);
        contentValues.put(TRAVEL_TIME,travelTime);

        Log.d(TAG,"insertData : Adding "+travelName+" "+destination+" "+travelDate+" "+travelTime+"to "+TABLE);

        long result = sqLiteDatabase.insert(TABLE,null,contentValues);

        if (result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE;
        Cursor data = sqLiteDatabase.rawQuery(query, null);
        return data;
    }
}
