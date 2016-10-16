package com.example.ConditionalAlarmClock;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("OnBootReceiver", "Boot event received.");
		ReminderManager reminderMgr = new ReminderManager(context);
		
		DbProvider provider = new DbProvider(context);
		Cursor cursor = provider.fetchAllAlarms();
		
		if (cursor != null) {
			
			System.out.println("Adding alarms from boot.");
			while (cursor.isAfterLast() == false) {
				Alarm alarm = Alarm.alarmFactory(cursor);
				Calendar cal = alarm.getMainAlarmTime();
				
				//todo right now only main alarm is being set on boot
				reminderMgr.setReminder(alarm.getId(), cal, true);
				
				cursor.moveToNext();
				
				System.out.println("Row Id - " + alarm.getId() + " .  Name: '" + alarm.getName() + "'.");
			}
			
			cursor.close();
		}
		provider.close();
	}

}
