package com.example.ConditionalAlarmClock;

import java.sql.SQLException;
import java.util.Calendar;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

public class ReminderService extends WakeReminderIntentService {

	private DbProvider db;
	
	public ReminderService() {
		super("ReminderService");
	}

	@Override
	void doReminderWork(Intent intent) {
		Long rowId = intent.getExtras().getLong(DbProvider.KEY_ID);
		Boolean isMainAlarm = intent.getExtras().getBoolean("IsMainAlarm");

		//check to see if alarm should go off today
		db = new DbProvider(this);
		try {
			db.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//todo - do api calls here to decide if the alarm should actually proceed and sound
		
		if (!shouldAlarmRing(rowId))
		{
			resetAlarm(intent, rowId, isMainAlarm);
			return;
		}
			
		
		Intent i = new Intent(AlarmActivatedActivity.instance, AlarmActivatedActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("AlarmId", rowId);
		i.setClassName("com.example.ConditionalAlarmClock",
	               "com.example.ConditionalAlarmClock.AlarmActivatedActivity");
		startActivity(i);
	}
	
	private boolean shouldAlarmRing(long rowId)
	{
		Cursor cursor = db.fetchAlarm(rowId);
		Alarm alarm = Alarm.alarmFactory(cursor);
		
		int daysOfWeek = alarm.getDaysOfWeek();
		
		if (Mask.isPresent(getCurrentDayOfWeek(), daysOfWeek))
			return true;
		
		return false;
	}
	
	private int getCurrentDayOfWeek()
	{
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		
		int dayMapped = mapDayOfWeek(day);
		
		return dayMapped;
	}
	
	private int mapDayOfWeek(int javaDayOfWeek)
	{
		int n = 0;
		
		switch (javaDayOfWeek)
		{
		case Calendar.SUNDAY:
			n = Mask.SUNDAY;
			break;
		case Calendar.MONDAY:
			n = Mask.MONDAY;
			break;
		case Calendar.TUESDAY:
			n = Mask.TUESDAY;
			break;
		case Calendar.WEDNESDAY:
			n = Mask.WEDNESDAY;
			break;
		case Calendar.THURSDAY:
			n = Mask.THURSDAY;
			break;
		case Calendar.FRIDAY:
			n = Mask.FRIDAY;
			break;
		case Calendar.SATURDAY:
			n = Mask.SATURDAY;
			break;
		}
		
		return n;
	}
	
	private void resetAlarm(Intent intent, long rowId, boolean isMainAlarm)
	{
		DbProvider db = new DbProvider(this);
		try {
			db.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Cursor cursor = db.fetchAlarm(rowId);
		Alarm alarm = Alarm.alarmFactory(cursor);
		
		//reset alarm for 1 day in the future
		ReminderManager manager = new ReminderManager(ReminderService.this);
		Calendar time = alarm.getMainAlarmTime();
		time.add(Calendar.DATE, 1);
		manager.setReminder(intent.getExtras().getLong(DbProvider.KEY_ID), time.getTimeInMillis(), isMainAlarm);
	}

}

