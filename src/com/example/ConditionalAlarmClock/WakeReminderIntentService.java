package com.example.ConditionalAlarmClock;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Responsible for acquiring and releasing the wake lock.
 * @author Ian
 *
 */
public abstract class WakeReminderIntentService extends IntentService {

	public WakeReminderIntentService(String name) {
		super(name);
		
	}

	abstract void doReminderWork(Intent intent);
	
	public static final String LOCK_NAME_STATIC = "com.example.ConditionalAlarmClock.Static";
	private static PowerManager.WakeLock lockStatic = null;
	
	/**
	 * Acquires a static lock.  Should be used when attempting to wake device as alarm is going off
	 * @param context
	 */
	public static void acquireStaticLock(Context context) {
		getLock(context).acquire();
	}
	
	/**
	 * Releases static lock that has been acquired.  
	 * @param context
	 */
	public static void releaseStaticLock(Context context) {
		getLock(context).release();
	}
	
	synchronized private static PowerManager.WakeLock getLock(Context context) {
		if (lockStatic == null) {
			PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			
			lockStatic = mgr.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, LOCK_NAME_STATIC); //TODO: figure out what to use instead of FULL_WAKE_LOCK
			
			lockStatic.setReferenceCounted(true);
		}
		
		return (lockStatic);
	}
	
	@Override
	final protected void onHandleIntent(Intent intent) {
		try {
			doReminderWork(intent);
		}
		finally {
			getLock(this).release();
		}
	}
	
}
