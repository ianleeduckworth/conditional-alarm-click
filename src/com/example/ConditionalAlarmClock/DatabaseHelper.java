package com.example.ConditionalAlarmClock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static DatabaseHelper mInstance;
	
    private DatabaseHelper(Context context)
    {
        super(context, DbProvider.DATABASE_NAME, null, Integer.parseInt(DbProvider.DATABASE_VERSION));
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DbProvider.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
    
    public static DatabaseHelper getInstance(Context ctx) {
    	if (mInstance == null)
    		mInstance = new DatabaseHelper(ctx.getApplicationContext());
    	
		return mInstance;
    }
}
