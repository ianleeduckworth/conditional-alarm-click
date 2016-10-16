package com.example.ConditionalAlarmClock;

import java.util.Calendar;

import android.database.Cursor;

/**
 * This class acts as a data model to be used in conjunction with the abstract class AlarmInfo.  This class
 * is instantiated based on data from the database that can be used to populate the view model.  Note that to
 * create an instance of this class, call the AlarmFactory(Cursor) method and pass in your database cursor.
 * This will convert that data into an instance of Alarm automatically.
 */

public class Alarm {
	
	private long mId;
	
	private String mName;
	private int mDaysOfWeek;
	private int mSnowOffset;
	private int mRainOffset;
	
	private boolean mAm;
	private int mMainAlarmHour;
	private int mMainAlarmMinute;
	private Calendar mMainAlarmTime;
	private Calendar mRainAlarmTime;
	private Calendar mSnowAlarmTime;
	
	private boolean mMon;
	private boolean mTues;
	private boolean mWeds;
	private boolean mThurs;
	private boolean mFri;
	private boolean mSat;
	private boolean mSun;
	
	public Alarm(String name, int daysOfWeek, int snowOffset, int rainOffset, int mainHour, int mainMinute, long id) {
		this (name, daysOfWeek, snowOffset, rainOffset, mainHour, mainMinute);
		this.mId = id;
	}
	
	//TODO: make this private.  Should only be instantiated with a db cursor based on alarmFactory() method
	public Alarm(String name, int daysOfWeek, int snowOffset, int rainOffset, int mainHour, int mainMinute) {
		
		mId = Integer.MIN_VALUE;
		
		//set private fields directly based on constructor.
		mName = name;
		mDaysOfWeek = daysOfWeek;
		mSnowOffset = snowOffset;
		mRainOffset = rainOffset;
		mMainAlarmHour = mainHour;
		mMainAlarmMinute = mainMinute;
		mAm = Mask.isPresent(daysOfWeek, Mask.AM);
		
		//Select main alarm time, snow alarm time and rain alarm time.
		Calendar mainCalendar = Calendar.getInstance();
    	mainCalendar.set(Calendar.HOUR_OF_DAY, mainHour);
    	mainCalendar.set(Calendar.MINUTE, mainMinute);
    	
    	
    	//if (mAm)
    	//	mainCalendar.set(Calendar.AM_PM, Calendar.AM);
    	//else
    	//	mainCalendar.set(Calendar.AM_PM, Calendar.PM);
    	
    	Calendar rainCalendar = (Calendar) mainCalendar.clone();
    	rainCalendar.set(Calendar.MINUTE, mainMinute - rainOffset);
    	
    	Calendar snowCalendar = (Calendar) mainCalendar.clone();
    	snowCalendar.set(Calendar.MINUTE, mainMinute - snowOffset);
		
    	mMainAlarmTime = mainCalendar;
    	mRainAlarmTime = rainCalendar;
    	mSnowAlarmTime = snowCalendar;
    	
    	//set all day of week booleans based on daysOfWeek mask.
    	convertToDaysOfWeekBooleans(daysOfWeek);
    	
	}
	
    private void convertToDaysOfWeekBooleans(int mask) {
    	if (Mask.isPresent(mask, Mask.SUNDAY))
    		this.mSun = true;
    	else
    		this.mSun = false;
    	
    	if (Mask.isPresent(mask, Mask.MONDAY))
    		this.mMon = true;
    	else
    		this.mMon = false;
    	
    	if (Mask.isPresent(mask, Mask.TUESDAY))
    		this.mTues = true;
    	else
    		this.mTues = false;
    	
    	if (Mask.isPresent(mask, Mask.WEDNESDAY))
    		this.mWeds = true;
    	else
    		this.mWeds = false;
    	
    	if (Mask.isPresent(mask, Mask.THURSDAY))
    		this.mThurs = true;
    	else
    		this.mThurs = false;
    	
    	if (Mask.isPresent(mask, Mask.FRIDAY))
    		this.mFri = true;
    	else
    		this.mFri = false;
    	
    	if (Mask.isPresent(mask, Mask.SATURDAY))
    		this.mSat = true;
    	else this.mSat = false;
    }
    
    /**
     * Method to create an instance of Alarm class.  Acts as an adapter between the information in the
     * database and the Alarm class
     * @param Cursor cursor must be generated based on a call to fetchAlarm() from DbProvider instance
     * @return An instance of Alarm based on the cursor passed in
     */
    //must be a cursor from the Alarm_Info database.
    public static Alarm alarmFactory(Cursor cursor) {  
    	return new Alarm(
    			cursor.getString(1),
    			cursor.getInt(4),
    			cursor.getInt(6),
    			cursor.getInt(5),
    			cursor.getInt(2),
    			cursor.getInt(3),
    			cursor.getLong(0)
    			);
    }
    
	/**
	 * Determines if the snow or rain alarm is set to go off first
	 * @param alarm
	 * @return Calendar oldest alarm
	 */
	public Calendar getOldestAlarm() {
		Calendar s = this.getSnowtime();
		Calendar r = this.getRainTime();
		if (s.before(r)) 
			return s;
		
		return r;
	}
    
    //Getters.  There are no setters on this class; info is passed in entirely through the constructor
    public long getId() {return mId; }
    
    public String getName() { return mName; }
    public int getDaysOfWeek() { return mDaysOfWeek; }
    public int getSnowOffset() { return mSnowOffset; }
    public int getRainOffset() { return mRainOffset; }
    public boolean isAm() { return mAm; }
    public int getMainAlarmHour() {return mMainAlarmHour; }
    public int getMainAlarmMinute() { return mMainAlarmMinute; }
    public Calendar getMainAlarmTime() { return mMainAlarmTime; }
    public Calendar getRainTime() { return mRainAlarmTime; }
    public Calendar getSnowtime() { return mSnowAlarmTime; }
    
    public boolean getMon() { return mMon; }
    public boolean getTues() { return mTues; }
    public boolean getWeds() { return mWeds; }
    public boolean getThurs() { return mThurs; }
    public boolean getFri() { return mFri; }
    public boolean getSat() { return mSat; }
    public boolean getSun() { return mSun; }
	
	
}
