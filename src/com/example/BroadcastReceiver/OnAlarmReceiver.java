package com.example.BroadcastReceiver;

import com.example.ConditionalAlarmClock.AlarmActivatedActivity;
import com.example.ConditionalAlarmClock.DbProvider;
import com.example.ConditionalAlarmClock.MainActivity;
import com.example.ConditionalAlarmClock.R;
import com.example.ConditionalAlarmClock.ReminderService;
import com.example.ConditionalAlarmClock.WakeReminderIntentService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Responsible for handling event that occurs whenever an alarm is received.
 * @author Ian
 *
 */
public class OnAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("Alarm went off.");
		long rowId = intent.getExtras().getLong(DbProvider.KEY_ID);
		boolean isMainAlarm = intent.getExtras().getBoolean("IsMainAlarm");
		
		WakeReminderIntentService.acquireStaticLock(context);
		
		Intent i = new Intent(context, ReminderService.class);
		i.putExtra(DbProvider.KEY_ID, rowId);
		i.putExtra("IsMainAlarm", isMainAlarm);
		context.startService(i);
	}
}
