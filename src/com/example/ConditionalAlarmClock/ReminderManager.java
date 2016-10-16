package com.example.ConditionalAlarmClock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.example.BroadcastReceiver.OnAlarmReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Responsible for setting up reminders using the AlarmManager API
 * 
 * @author Ian
 *
 */
public class ReminderManager {
	private Context mContext;
	private AlarmManager mAlarmManager;

	public ReminderManager(Context context) {
		mContext = context;
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void setReminder(long timeInMilis, boolean isMainAlarm) {
		setReminder(-1, timeInMilis, isMainAlarm);
	}

	public void setReminder(Long taskId, Calendar when, boolean isMainAlarm) {
		long timeInMilis = when.getTimeInMillis();
		setReminder(taskId, timeInMilis, isMainAlarm);
		System.out.println("Alarm set to go off at: " + getAlarmDateTime(timeInMilis));
	}
	
	private String getAlarmDateTime(long when) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", Locale.US);
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
		calendar.setTimeInMillis(when);
		return sdf.format(calendar.getTime());
	}

	/**
	 * Method for if you already have an instance of Alarm. Will set alarm based
	 * on Alarm's id and oldest alarm between the rain and the snow alarms.
	 * 
	 * @param alarm
	 *            Instance of Alarm upon which the reminder is to be set
	 */
	public void setReminder(long id, Alarm alarm, boolean isMainAlarm) {
		setReminder(id, alarm.getOldestAlarm(), isMainAlarm);
	}

	public void setReminder(long taskId, long timeInMilis, boolean isMainAlarm) {
		Intent i = new Intent(mContext, OnAlarmReceiver.class);
		
		if (taskId != -1)
			i.putExtra(DbProvider.KEY_ID, (long) taskId);
		
		i.setClass(mContext, OnAlarmReceiver.class);
		i.putExtra("IsMainAlarm", isMainAlarm);

		PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i, PendingIntent.FLAG_ONE_SHOT);
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, timeInMilis, pi);
	}
	
	public void deleteReminder(long id)
	{
		Intent intent = new Intent();
		intent.putExtra("AlarmId", id);
		
		//no argument for ID as a long.  Necessary to cast to int and handle the precision loss; shouldn't affect this
		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
		        (int)id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		mAlarmManager.cancel(pendingIntent);
	}
}
