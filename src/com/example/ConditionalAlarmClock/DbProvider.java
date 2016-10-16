package com.example.ConditionalAlarmClock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.sql.SQLException;

/**
 * Created by Ian on 2/16/15.
 */
public class DbProvider
{

    public static final String DATABASE_NAME = "data";
    public static final String ALARMS_TABLE = "alarms";
    public static final String DATABASE_VERSION = "1";

    public static final String KEY_NAME = "name";
    public static final String KEY_HOUR = "hour";
    public static final String KEY_MINUTE = "minute";
    public static final String KEY_DAYS_OF_WEEK = "days_of_week";
    public static final String KEY_RAIN_OFFSET = "rain_offset";
    public static final String KEY_SNOW_OFFSET = "snow_offset";
    public static final String KEY_ID = "_id";
    public static final String ROW_ID = "ROWID";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public static final String DATABASE_CREATE =
            "create table " + ALARMS_TABLE + " ("
                    + KEY_NAME + " text primary key not null, "
                    + KEY_HOUR + " integer not null, "
                    + KEY_MINUTE + " integer not null, "
                    + KEY_DAYS_OF_WEEK + " integer not null, "
                    + KEY_RAIN_OFFSET + " integer not null, "
                    + KEY_SNOW_OFFSET + " integer not null);";

    private final Context mCtx;

    public DbProvider(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Opens the database for writing/reading if it is not open already
     * @return
     * @throws SQLException
     */
    public DbProvider open() throws SQLException
    {
    	if (mDbHelper != null) return this;
    	
        mDbHelper = DatabaseHelper.getInstance(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Closes the database.  Should only be used in the MainActivity.onDestroy() method.  This will allow the
     * application to manage this database without developer input
     */
    public void close()
    {
        mDbHelper.close();
    }

    /**
     * Method to create a new alarm; Alarm will be added to the database based on parameters passed in
     * @param name Name of alarm.  Will be added to NAME column
     * @param hour Hour that the alarm will go off.  Will be added to the HOUR column
     * @param minute Minute that the alarm will go off.  Will be added to the MINUTE column
     * @param daysOfWeek Mask representing the days of week.  See Mask class.  Note that AM/PM bit is also stored here.  Will be added to DAYS_OF_WEEK column
     * @param rainOffset Number of minutes later the alarm will go off in the event of rain.  Will be added to the RAIN_OFFSET column
     * @param snowOffset Number of minutes later the alarm will go off in the event of snow.  Will be added to the SNOW_OFFSET column
     * @return the position of the new alarm.  This will become the unique ID of the alarm in the _id column
     */
    public long createAlarm(String name, int hour, int minute, int daysOfWeek, int rainOffset, int snowOffset)
    {
    	checkIfDbIsOpen();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_HOUR, hour);
        values.put(KEY_MINUTE, minute);
        values.put(KEY_DAYS_OF_WEEK, daysOfWeek);
        values.put(KEY_RAIN_OFFSET, rainOffset);
        values.put(KEY_SNOW_OFFSET, snowOffset);

        long insertResult = mDb.insert(ALARMS_TABLE, null, values);
        
        if (insertResult == -1)
        	System.out.println("Insert statement failed.");
        
        return insertResult;
    }
    
    /**
     * Method to create a new alarm; Alarm will be added to the database based on parameters passed in
     * @param alarm Instance of Alarm class.  This will act as an adapter
     * @return
     */
    public long createAlarm(Alarm alarm) {
    	checkIfDbIsOpen();
    	ContentValues values = new ContentValues();
    	values.put(KEY_NAME, alarm.getName());
    	values.put(KEY_HOUR, alarm.getMainAlarmHour());
        values.put(KEY_MINUTE, alarm.getMainAlarmMinute());
        values.put(KEY_DAYS_OF_WEEK, alarm.getDaysOfWeek());
        values.put(KEY_RAIN_OFFSET, alarm.getRainOffset());
        values.put(KEY_SNOW_OFFSET, alarm.getSnowOffset());

        long insertResult = mDb.insert(ALARMS_TABLE, null, values);
        
        if (insertResult == -1)
        	System.out.println("Insert statement failed.");
        
        return insertResult;
    }

    /**
     * Removes an alarm from the database based on NAME
     * @param name A string value representing the NAME column in the database
     * @return boolean representing whether or not the operation was a success
     */
    public boolean deleteAlarm(String name)
    {
    	checkIfDbIsOpen();
        return mDb.delete(ALARMS_TABLE, KEY_NAME + "=" + "'" + name + "'", null) > 0;
    }
    
    /**
     * Removes an alarm from the database based on ROWID
     * @param id ROWID column from the database
     * @return boolean representing whether or not the operation was a success
     */
    public boolean deleteAlarm(long id) {
    	checkIfDbIsOpen();
    	return mDb.delete(ALARMS_TABLE, ROW_ID + "=" + "'" + String.valueOf(id) + "'", null) > 0;
    }

    /**
     * Fetches every alarm that is currently in the database
     * @return database cursor
     */
    public Cursor fetchAllAlarms()
    {
    	checkIfDbIsOpen();
    	Cursor cursor = mDb.query(ALARMS_TABLE, new String[]
                {
                		"ROWID as " + KEY_ID,
                        KEY_NAME,
                        KEY_HOUR,
                        KEY_MINUTE,
                        KEY_DAYS_OF_WEEK,
                        KEY_RAIN_OFFSET,
                        KEY_SNOW_OFFSET,
                }, null, null, null, null, null);
    	
    	if (cursor != null)
    		cursor.moveToFirst();
    	
        return cursor;
    }

    /**
     * Method to fetch a single alarm based on the NAME column in the database
     * @param name a String instance representing the NAME that you wish to retrieve
     * @return database cursor
     */
    public Cursor fetchAlarm(String name)
    {
    	checkIfDbIsOpen();
    	try {
    		Cursor cursor = mDb.query(true, ALARMS_TABLE, new String []
    		        {
    		        		"ROWID as " + KEY_ID,
    		                KEY_NAME,
    		                KEY_HOUR,
    		                KEY_MINUTE,
    		                KEY_DAYS_OF_WEEK,
    		                KEY_RAIN_OFFSET,
    		                KEY_SNOW_OFFSET,
    		        }, KEY_NAME + "=" + "'" + name + "'", null, null, null, null, null);

    		        if (cursor != null)
    		            cursor.moveToFirst();

    		        return cursor;
    	}
    	catch (SQLiteConstraintException e)
    	{
    		System.out.println("Could not find row.");
    		System.out.println(e);
    	}
    	
    	return null;
    }
    
    /**
     * Method to fetch a single alarm based on the _id column in the database
     * @param position a numeric representing the _id that you wish to retrieve
     * @return database cursor
     */
    public Cursor fetchAlarm(long position) {
    	checkIfDbIsOpen();
    	try {
    		Cursor cursor = mDb.query(true, ALARMS_TABLE, new String []
    				{
    						"ROWID as " + KEY_ID,
    						KEY_NAME,
    						KEY_HOUR,
    						KEY_MINUTE,
    						KEY_DAYS_OF_WEEK,
    						KEY_RAIN_OFFSET,
    						KEY_SNOW_OFFSET,
    				}, KEY_ID + "=" + "'" + String.valueOf(position) + "'", null, null, null, null, null);

		        	if (cursor != null)
		        		cursor.moveToFirst();

		        	return cursor;
    	}
    	catch (SQLiteConstraintException e)
    	{
    		System.out.println("Could not find row.");
    		System.out.println(e);
    	}
	
    	return null;
    }

    public boolean updateAlarm(String name, int hour, int minute, int daysOfWeek, int rainOffset, int snowOffset)
    {
    	checkIfDbIsOpen();
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_HOUR, String.valueOf(hour));
        args.put(KEY_MINUTE, String.valueOf(minute));
        args.put(KEY_DAYS_OF_WEEK, String.valueOf(daysOfWeek));
        args.put(KEY_RAIN_OFFSET, String.valueOf(rainOffset));
        args.put(KEY_SNOW_OFFSET, String.valueOf(snowOffset));

        return mDb.update(ALARMS_TABLE, args, KEY_NAME + "=" + name, null) > 0;
    }
    
    private void checkIfDbIsOpen() {
    	if (mDb == null) throw new IllegalStateException("Backing database object is null.  You must open a connection before attempting any database operations.");
    }
}
