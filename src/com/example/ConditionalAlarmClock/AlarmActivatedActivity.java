package com.example.ConditionalAlarmClock;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AlarmActivatedActivity extends Activity {

	private SeekBar turnOffAlarm;
	private SeekBar snoozeAlarm;
	private Ringtone ringtone;
	private AudioManager audioManager;
	private int ringerVolume;
	private long rowId;
	private DbProvider db;
	private Alarm alarm;
	
	private final int snoozeMilis = 5 * 6000;

	public static AlarmActivatedActivity instance = new AlarmActivatedActivity();

	public AlarmActivatedActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getApplicationContext();
		
		Intent i = getIntent();
		rowId = i.getLongExtra("AlarmId", 0);
		
		db = new DbProvider(this);
		try {
			db.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Cursor cursor = db.fetchAlarm(rowId);
		alarm = Alarm.alarmFactory(cursor);
		
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		//ringerVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM); //todo does not work if ringer is off
		ringerVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
		unlockScreen();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_activated);
		registerSeekBars();
		setRingtoneVolumeMax();
		getRingtone().play();
	}

	private void unlockScreen() {	
		Window win = getWindow();
	    win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	    win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	private Ringtone getRingtone() {
		if (this.ringtone != null)
			return this.ringtone;

		Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alarmUri == null) {
			alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		}

		return RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
	}

	private void registerSeekBars() {
		turnOffAlarm = (SeekBar) findViewById(R.id.turnOffAlarm);
		snoozeAlarm = (SeekBar) findViewById(R.id.snoozeAlarm);

		turnOffAlarm.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();

				if (progress <= 35)
					seekBar.setProgress(0);
				else {
					seekBar.setProgress(100);
					getRingtone().stop();
					turnOff();
					resetRingtoneVolume();
					finish();
					//WakeReminderIntentService.releaseStaticLock(getApplicationContext());
					resetAlarm(alarm);
					System.exit(0);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// do nothing here
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// do nothing here
			}
		});

		snoozeAlarm.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();

				if (progress <= 35)
					seekBar.setProgress(0);
				else {
					seekBar.setProgress(100);
					getRingtone().stop();
					snooze();
					resetRingtoneVolume();
					finish();
					//WakeReminderIntentService.releaseStaticLock(getApplicationContext());
					System.exit(0);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// do nothing here
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// do nothing here
			}
		});
	}

	private void turnOff() {
		// TODO do something here
		// maybe do nothing?
	}

	private void snooze() {
		long milis = System.currentTimeMillis() + (snoozeMilis);

		ReminderManager reminderManager = new ReminderManager(getApplicationContext());
		reminderManager.setReminder(rowId, milis, true);
	}
	
	/**
	 * Sets the alarm to go off again the same time the next day.
	 * @param alarm
	 */
	private void resetAlarm(Alarm alarm) {
		// set the alarm using AlarmManager API
		ReminderManager reminderManager = new ReminderManager(this);
		reminderManager.setReminder(rowId, alarm.getMainAlarmTime(), true);
	}

	private void resetRingtoneVolume() {
		//audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ringerVolume, 0);
		audioManager.setStreamVolume(AudioManager.STREAM_RING, ringerVolume, 0);
	}

	private void setRingtoneVolumeMax() {
		//int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		//audioManager.setStreamVolume(AudioManager.STREAM_ALARM, max, 0);
		
		int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
		audioManager.setStreamVolume(AudioManager.STREAM_RING, max, 0);
	}

}
