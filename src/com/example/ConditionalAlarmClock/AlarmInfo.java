package com.example.ConditionalAlarmClock;

import android.app.Application;

/**
 * This is an abstract class that acts as the view model for the various aspects of the UI.
 * Setting its values will effectively control what data is actually shown to the user.
 * The method adapt(Alarm) should be called to transfer an instance of the Alarm class which is instantiated
 * from the database to persist it to the view
 */

public abstract class AlarmInfo extends Application
{
	//constructor exists to defeat instantiation
    private AlarmInfo() {}
    
    private static boolean mMon;
    private static boolean mTues;
    private static boolean mWeds;
    private static boolean mThurs;
    private static boolean mFri;
    private static boolean mSat;
    private static boolean mSun;

    private static boolean mIsSnow;
    private static boolean mIsRain;

    private static int mRainOffset;
    private static int mSnowOffset;

    private static int mMainHour;
    private static int mMainMinute;
    
    private static boolean mAm;

    private static String mAlarmName;
    
    public static final long UNSELECTED = -1;
    private static long mSelectedPos = UNSELECTED;
    
    //sets all fields on the class based on an instance of Alarm retrieved from the database
    public static void adaptAlarm(Alarm alarm) {
    	mSun = alarm.getSun();
    	mMon = alarm.getMon();
    	mTues = alarm.getTues();
    	mWeds = alarm.getWeds();
    	mThurs = alarm.getThurs();
    	mFri = alarm.getFri();
    	mSat = alarm.getSat();
    	
    	int snowOffset = alarm.getSnowOffset();
    	if (snowOffset == 0) {
    		mIsSnow = false;
    		mSnowOffset = 0;
    	}
    	else {
    		mIsSnow = true;
    		mSnowOffset = snowOffset;
    	}
    	
    	int rainOffset = alarm.getRainOffset();
    	if (rainOffset == 0) {
    		mIsRain = false;
    		mRainOffset = 0;
    	}
    	else {
    		mIsRain = true;
    		mRainOffset = rainOffset;
    	}
    	
    	mMainMinute = alarm.getMainAlarmMinute();
    	mMainHour = alarm.getMainAlarmHour();
    	
    	mAm = alarm.isAm();
    	
    	mAlarmName = alarm.getName();
    	
    	mSelectedPos = alarm.getId();
    }
    
    public static void reset() {
    	setSun(false);
    	setMon(false);
    	setTues(false);
    	setWeds(false);
    	setThurs(false);
    	setFri(false);
    	setSat(false);
    	
    	setSnowSelected(false);
    	setRainSelected(false);
    	
    	setRainOffset(0);
    	setSnowOffset(0);
    	
    	setAlarmName(StringHelper.EMPTY);
    	
    	setMainAlarmHour(-1);
    	setMainAlarmMinute(-1);
    	
    	setSelectedPos(-1);
    	
    	setAm(false);
    }
    
    //getters and setters for each day of the week
    public static boolean getMon() { return mMon; }
    public static void setMon(boolean value) { mMon = value; }
    
    public static boolean getTues() { return mTues; }
    public static void setTues(boolean value) { mTues = value; }
    
    public static boolean getWeds() { return mWeds; }
    public static void setWeds(boolean value) { mWeds = value; }
    
    public static boolean getThurs() { return mThurs; }
    public static void setThurs(boolean value) { mThurs = value; }
    
    public static boolean getFri() { return mFri; }
    public static void setFri(boolean value) { mFri = value; }
    
    public static boolean getSat() {return mSat; }
    public static void setSat(boolean value) { mSat = value; }
    
    public static boolean getSun() { return mSun; }
    public static void setSun(boolean value) { mSun = value; }

    //getters and setters for each weather offset
    public static boolean getSnowSelected() { return mIsSnow; }
    public static void setSnowSelected(boolean value) { mIsSnow = value; }
    
    public static boolean getRainSelected() { return mIsRain; }
    public static void setRainSelected(boolean value) { mIsSnow = value; }
    
    public static int getRainOffset() { return mRainOffset; }
    public static void setRainOffset(int value) { mRainOffset = value; }
    
    public static int getSnowOffset() { return mSnowOffset; }
    public static void setSnowOffset(int value) {mSnowOffset = value; }

    //getters and setters for main alarm time
    public static int getMainAlarmHour() { return mMainHour; }
    public static void setMainAlarmHour(int value) { mMainHour = value; }
    
    public static int getMainAlarmMinute() { return mMainMinute; }
    public static void setMainAlarmMinute(int value) { mMainMinute = value; }

    public static void setAm(boolean value) {mAm = value;}
    public static boolean getAm() {return mAm;}

    //getter and for alarm name
    public static String getAlarmName() { return mAlarmName; }
    public static void setAlarmName(String value) { mAlarmName = value; }
    
    //getter and setter for selectedPos
    public static long getSelectedPos() { return mSelectedPos; }
    public static void setSelectedPos(long value) { mSelectedPos = value; }
}

