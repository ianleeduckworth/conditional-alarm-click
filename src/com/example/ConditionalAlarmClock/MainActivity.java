package com.example.ConditionalAlarmClock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import com.example.BroadcastReceiver.OnAlarmReceiver;

/**
 * Created by Ian on 11/11/14.
 */
public class MainActivity extends Activity implements OnItemClickListener {

	private DbProvider db;

	public static final String SQL_OUTPUT_FORMAT = "%-12s%-12s%-12s%-12s%-12s%-12s%-12s\n";
	public static final String[] COLUMNS = { "ID", "NAME", "HOUR", "MINUTE", "DAYS", "RAIN_DELAY", "SNOW_DELAY" };
	//public static String BROADCAST_RECEIVER_PACKAGE = "com.example.BroadcastReceiver.OnAlarmReceiver";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainwindow);

		db = new DbProvider(this);
		try {
			db.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		populateAllAlarmsListView();
	}

	/**
	 * Called when the Create button is clicked
	 * 
	 * @param view
	 */
	public void CreateNew_OnClick(View view) {
		try {
			printAllAlarms(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception when trying to retrieve all alarms: " + e);
		}
		setContentView(R.layout.createalarm);
		AlarmInfo.reset();
		//populateCreateAlarmScreen();
		populateCreateAlarmScreen();
	}

	/**
	 * Called when the Edit button is clicked
	 * 
	 * @param view
	 */
	public void EditBtn_OnClick(View view) {
		long pos = AlarmInfo.getSelectedPos();

		// check if we even have an item selected
		if (AlarmInfo.getSelectedPos() == AlarmInfo.UNSELECTED) {
			Toast toast = Toast.makeText(this, "You must select an item to edit.", Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		Cursor cursor = db.fetchAlarm(pos);
		Alarm alarm = Alarm.alarmFactory(cursor);
		AlarmInfo.adaptAlarm(alarm);

		// now that all alarminfo is retrieved advance screens to the
		// create_alarm xml
		setContentView(R.layout.createalarm);

		populateCreateAlarmScreen();
	}

	/**
	 * Called when the Conditions button is clicked
	 * 
	 * @param view
	 */
	public void ConditionsBtn_OnClick(View view) {
		// before proceeding, verify that alarm has a name
		AlertDialog.Builder errorAlert = new AlertDialog.Builder(this);
		EditText alarmName = (EditText) findViewById(R.id.alarmNameTxt);

		if (alarmName.getText().toString().trim().length() == 0) {
			errorAlert.setMessage("You must give your alarm a name.");
			errorAlert.show();
			return;
		}

		AlarmInfo.setAlarmName(alarmName.getText().toString());

		// create objects for all dayOfWeek buttons
		ToggleButton sunBtn = (ToggleButton) findViewById(R.id.sundayToggleButton);
		ToggleButton monBtn = (ToggleButton) findViewById(R.id.mondayToggleButton);
		ToggleButton tuesBtn = (ToggleButton) findViewById(R.id.tuesdayToggleButton);
		ToggleButton wedsBtn = (ToggleButton) findViewById(R.id.wednesdayToggleButton);
		ToggleButton thursBtn = (ToggleButton) findViewById(R.id.thursdayToggleButton);
		ToggleButton friBtn = (ToggleButton) findViewById(R.id.fridayToggleButton);
		ToggleButton satBtn = (ToggleButton) findViewById(R.id.saturdayToggleButton);
		TimePicker timePicker = (TimePicker) findViewById(R.id.mainTimePicker);

		// verify that at least one day of the week is set. If it isn't do not
		// allow user to continue
		if (!monBtn.isChecked() && !tuesBtn.isChecked() && !wedsBtn.isChecked() && !thursBtn.isChecked()
				&& !friBtn.isChecked() && !satBtn.isChecked() && !sunBtn.isChecked()) {
			errorAlert.setMessage("You must have at least one day selected for your alarm.");
			errorAlert.show();
			return;
		}

		// set all applicable properties in AlarmInfo now that we know that
		// alarm settings are valid
		AlarmInfo.setMainAlarmHour(timePicker.getCurrentHour());
		AlarmInfo.setMainAlarmMinute(timePicker.getCurrentMinute());

		if (sunBtn.isChecked())
			AlarmInfo.setSun(true);
		else
			AlarmInfo.setSun(false);

		if (monBtn.isChecked())
			AlarmInfo.setMon(true);
		else
			AlarmInfo.setMon(false);

		if (tuesBtn.isChecked())
			AlarmInfo.setTues(true);
		else
			AlarmInfo.setTues(false);

		if (wedsBtn.isChecked())
			AlarmInfo.setWeds(true);
		else
			AlarmInfo.setWeds(false);

		if (thursBtn.isChecked())
			AlarmInfo.setThurs(true);
		else
			AlarmInfo.setThurs(false);

		if (friBtn.isChecked())
			AlarmInfo.setFri(true);
		else
			AlarmInfo.setFri(false);

		if (satBtn.isChecked())
			AlarmInfo.setSat(true);
		else
			AlarmInfo.setSat(false);

		// now that AlarmInfo is set, load conditions view based on what is in
		// AlarmInfo
		setContentView(R.layout.conditions);
		populateConditionsScreen();
	}

	// Checks static AlarmInfo class and converts it into a dayOfWeek bitmask
	//TODO: THIS METHOD IS CURRENTLY DUPLICATED.  SHOULD FIX
	private int convertDaysOfWeek() {
		int daysOfWeek = 0;

		if (AlarmInfo.getSun())
			daysOfWeek = Mask.addToMask(daysOfWeek, Mask.SUNDAY);
		if (AlarmInfo.getMon())
			daysOfWeek = Mask.addToMask(daysOfWeek, Mask.MONDAY);
		if (AlarmInfo.getTues())
			daysOfWeek = Mask.addToMask(daysOfWeek, Mask.TUESDAY);
		if (AlarmInfo.getWeds())
			daysOfWeek = Mask.addToMask(daysOfWeek, Mask.WEDNESDAY);
		if (AlarmInfo.getThurs())
			daysOfWeek = Mask.addToMask(daysOfWeek, Mask.THURSDAY);
		if (AlarmInfo.getFri())
			daysOfWeek = Mask.addToMask(daysOfWeek, Mask.FRIDAY);
		if (AlarmInfo.getSat())
			daysOfWeek = Mask.addToMask(daysOfWeek, Mask.SATURDAY);
		if (AlarmInfo.getAm())
			daysOfWeek = Mask.addToMask(daysOfWeek, Mask.AM);

		return daysOfWeek;
	}

	// test method only; prints output to syso stating all the alarms currently
	// in data file
	@Deprecated
	public void printAllAlarms(DbProvider db) throws SQLException {
		db.open();
		Cursor cursor = db.fetchAllAlarms();

		if (cursor == null)
			return;

		System.out.println("Query returned " + cursor.getCount() + " rows.");

		System.out.printf(SQL_OUTPUT_FORMAT, "ID", "NAME", "HOUR", "MINUTE", "DAYS", "RAIN_OFF", "SNOW_OFF");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			System.out.printf(SQL_OUTPUT_FORMAT, cursor.getString(0), cursor.getString(1), cursor.getString(2),
					cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));

			cursor.moveToNext();
		}
	}
	
	private void populateCreateAlarmScreen(){
		Calendar cal = Calendar.getInstance();

		TimePicker timePicker = (TimePicker) findViewById(R.id.mainTimePicker);

		int alarmHour = AlarmInfo.getMainAlarmHour();
		if (alarmHour != -1)
			timePicker.setCurrentHour(AlarmInfo.getMainAlarmHour());
		else
			timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		
		int alarmMinute = AlarmInfo.getMainAlarmMinute();
		if (alarmMinute != -1)
			timePicker.setCurrentMinute(AlarmInfo.getMainAlarmMinute());
		else
			timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));

		ToggleButton sun = (ToggleButton) findViewById(R.id.sundayToggleButton);
		sun.setChecked(AlarmInfo.getSun());

		ToggleButton mon = (ToggleButton) findViewById(R.id.mondayToggleButton);
		mon.setChecked(AlarmInfo.getMon());

		ToggleButton tues = (ToggleButton) findViewById(R.id.tuesdayToggleButton);
		tues.setChecked(AlarmInfo.getTues());

		ToggleButton weds = (ToggleButton) findViewById(R.id.wednesdayToggleButton);
		weds.setChecked(AlarmInfo.getMon());

		ToggleButton thurs = (ToggleButton) findViewById(R.id.thursdayToggleButton);
		thurs.setChecked(AlarmInfo.getThurs());

		ToggleButton fri = (ToggleButton) findViewById(R.id.fridayToggleButton);
		fri.setChecked(AlarmInfo.getFri());

		ToggleButton sat = (ToggleButton) findViewById(R.id.saturdayToggleButton);
		sat.setChecked(AlarmInfo.getSat());

		EditText alarmName = (EditText) findViewById(R.id.alarmNameTxt);
		if (AlarmInfo.getAlarmName() != null)
			alarmName.setText(AlarmInfo.getAlarmName());
		else
			alarmName.setText(StringHelper.EMPTY);
	}
	
	private void populateConditionsScreen() {
		// rain
		//Switch rainSwitch = (Switch) findViewById(R.id.rainSwitch);
		CheckBox rainCheckBox = (CheckBox) findViewById(R.id.rainCheckbox);
		EditText rainText = (EditText) findViewById(R.id.rainOffset);

		rainCheckBox.setChecked(AlarmInfo.getRainSelected());
		//rainSwitch.setChecked(AlarmInfo.getRainSelected());

		if (AlarmInfo.getRainOffset() == 0) {
			rainText.setEnabled(false);
			rainText.setText(StringHelper.EMPTY);
		} else {
			rainText.setEnabled(true);
			rainText.setText(String.valueOf(AlarmInfo.getRainOffset()));
		}

		// snow
		//Switch snowSwitch = (Switch) findViewById(R.id.snowSwitch);
		CheckBox snowCheckBox = (CheckBox) findViewById(R.id.snowCheckbox);
		EditText snowText = (EditText) findViewById(R.id.snowOffset);

		//snowSwitch.setChecked(AlarmInfo.getSnowSelected());
		snowCheckBox.setChecked(AlarmInfo.getSnowSelected());

		if (AlarmInfo.getSnowOffset() == 0) {
			snowText.setEnabled(false);
			snowText.setText(StringHelper.EMPTY);
		} else {
			snowText.setEnabled(true);
			snowText.setText(String.valueOf(AlarmInfo.getSnowOffset()));
		}
	}

	/**
	 * Called with the Delete button is clicked
	 * 
	 * @param view
	 */
	// Handles the delete button being clicked. Will delete the alarm at the
	// selected position.
	// TODO: test this. I don't think it works as of 12/21/15
	public void DeleteBtn_OnClick(View view) {
		if (AlarmInfo.getSelectedPos() == AlarmInfo.UNSELECTED) {
			Toast toast = Toast.makeText(MainActivity.this, "You must select an alarm to be deleted.",
					Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Delete Alarm");
		alert.setMessage("Are you sure you want to delete alarm?");
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				return;
			}
		});
		alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				long rowId = AlarmInfo.getSelectedPos();
				
				ListView allAlarms = (ListView) findViewById(R.id.allAlarms);
				DbProvider db = new DbProvider(getApplicationContext());
				try {
					db.open();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Cursor cursor = db.fetchAlarm(rowId);
				Alarm alarm = Alarm.alarmFactory(cursor);
				db.deleteAlarm(rowId);
				AlarmListCursorAdapter adapter = (AlarmListCursorAdapter) allAlarms.getAdapter();
				adapter.notifyDataSetChanged();
				dialog.dismiss();
				
				ReminderManager remidnerManager = new ReminderManager(getApplicationContext());
				remidnerManager.deleteReminder(alarm.getId());
				
				populateAllAlarmsListView();
			}
		});

		alert.show();
	}

	public void FinishBtn_OnClick(View view) throws SQLException {
		// declare variables to interact with rain and snow controls
		
		EditText rainEditText = (EditText) findViewById(R.id.rainOffset);
		EditText snowEditText = (EditText) findViewById(R.id.snowOffset);
		CheckBox rainCheckBox = (CheckBox) findViewById(R.id.rainCheckbox);
		CheckBox snowCheckBox = (CheckBox) findViewById(R.id.snowCheckbox);

		// check rain controls and store offset accordingly
		AlertDialog.Builder errorAlert = new AlertDialog.Builder(this);

		if (rainCheckBox.isChecked()) {
			if (rainEditText.getText().toString().trim().length() == 0) {
				errorAlert.setMessage("You must enter a value in Rain Edit Text box.");
				errorAlert.show();
				return;
			}

			AlarmInfo.setRainSelected(true);
			AlarmInfo.setRainOffset(Integer.parseInt(rainEditText.getText().toString()));
		} else {
			AlarmInfo.setRainSelected(false);
			AlarmInfo.setRainOffset(0);
		}

		// check snow controls and store offset accordingly
		if (snowCheckBox.isChecked()) {
			if (snowEditText.getText().toString().trim().length() == 0) {
				errorAlert.setMessage("You must enter a value in Snow Edit Text box.");
				errorAlert.show();
				return;
			}

			AlarmInfo.setSnowSelected(true);
			AlarmInfo.setSnowOffset(Integer.parseInt(snowEditText.getText().toString()));
		} else {
			AlarmInfo.setSnowSelected(false);
			AlarmInfo.setSnowOffset(0);
		}

		final DbProvider db = new DbProvider(this);
		db.open();

		final Cursor alarmCursor = db.fetchAlarm(AlarmInfo.getSelectedPos());

		if (alarmCursor.getCount() != 0) {
			final Alarm alarm = Alarm.alarmFactory(alarmCursor);

			if (!checkIfUpdated(alarm)) {
				Toast toast = Toast.makeText(this, "Alarm '" + alarm.getName() + "' unchanged,  No action taken.",
						Toast.LENGTH_LONG);
				toast.show();

				AlarmInfo.reset();
				setContentView(R.layout.mainwindow);
				populateAllAlarmsListView();
				return;
			}

			// TODO: Check if this if statement is necessary. Already checking
			// if it's not 0 and it can't be negative.
			if (alarmCursor.getCount() >= 1) {
				final AlertDialog.Builder alert = new AlertDialog.Builder(this);

				alert.setTitle("Overwrite Alarm");
				alert.setMessage("Alarm '" + AlarmInfo.getAlarmName() + "' already exists.  Overwrite?");
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						return;
					}
				});

				alert.setPositiveButton("Overwrite", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						db.deleteAlarm(alarm.getName());
						System.out.println("Alarm '" + alarm.getName() + "' deleted.");

						long result = createAlarm();

						if (result == -1) {
							Toast toast = Toast.makeText(MainActivity.this, "Insert failed.", Toast.LENGTH_LONG);
							toast.show();
						}

						//TODO: This probably does not work.  Test it.
						AlarmInfo.reset();

						setContentView(R.layout.mainwindow);
						populateAllAlarmsListView();

						setCurrentAlarm();
						
						// set the alarm using AlarmManager API
						//ReminderManager reminderManager = new ReminderManager(MainActivity.this);
						//reminderManager.setReminder(alarm.getId(), alarm.getMainAlarmTime(), true);
					}
				});

				alert.show();
			}
		} else {
			long result = createAlarm();
			AlarmInfo.setSelectedPos(result);

			if (result == -1) {
				Toast toast = Toast.makeText(this, "Insert failed.", Toast.LENGTH_LONG);
				toast.show();
			}
			
			setCurrentAlarm();
			
			AlarmInfo.reset();

			setContentView(R.layout.mainwindow);
			populateAllAlarmsListView();
		}

	}

	/**
	 * Checks an instance of Alarm class against AlarmInfo to see if there are
	 * any differences
	 * 
	 * @param alarm
	 *            Instance of Alarm class to be checked against
	 * @return true if an update has occurred, false if an update has not
	 *         occurred
	 */
	private boolean checkIfUpdated(Alarm alarm) {
		if (alarm.getSun() != AlarmInfo.getSun())
			return true;
		if (alarm.getMon() != AlarmInfo.getMon())
			return true;
		if (alarm.getTues() != AlarmInfo.getTues())
			return true;
		if (alarm.getWeds() != AlarmInfo.getWeds())
			return true;
		if (alarm.getThurs() != AlarmInfo.getThurs())
			return true;
		if (alarm.getFri() != AlarmInfo.getFri())
			return true;
		if (alarm.getSat() != AlarmInfo.getSat())
			return true;

		if (alarm.getMainAlarmHour() != AlarmInfo.getMainAlarmHour())
			return true;
		if (alarm.getMainAlarmMinute() != AlarmInfo.getMainAlarmMinute())
			return true;

		if (!alarm.getName().equals(AlarmInfo.getAlarmName()))
			return true;

		if (alarm.getRainOffset() != AlarmInfo.getRainOffset())
			return true;
		if (alarm.getSnowOffset() != AlarmInfo.getSnowOffset())
			return true;

		return false;
	}

	/**
	 * Creates a new alarm based on the information currently stored in
	 * AlarmInfo
	 */
	private long createAlarm() {
		int daysOfWeek = convertDaysOfWeek();

		return db.createAlarm(AlarmInfo.getAlarmName(), AlarmInfo.getMainAlarmHour(), AlarmInfo.getMainAlarmMinute(),
				daysOfWeek, AlarmInfo.getRainOffset(), AlarmInfo.getSnowOffset());
	}
	
	private void setCurrentAlarm() {
		Alarm alarm = AlarmInfoAdapter.adaptAlarmInfo();
		
		// set the alarm using AlarmManager API
		if (alarm.getMainAlarmTime().before(Calendar.getInstance())) {
			alarm.getMainAlarmTime().add(Calendar.DATE, 1);
		}	
		
		ReminderManager reminderManager = new ReminderManager(MainActivity.this);
		reminderManager.setReminder(alarm.getId(), alarm.getMainAlarmTime(), true);
		
		if (alarm.getSnowOffset() != 0)
			reminderManager.setReminder(alarm.getId(),  alarm.getSnowtime(), false);
		if (alarm.getRainOffset() != 0)
			reminderManager.setReminder(alarm.getId(), alarm.getRainTime(), false);
	}

	/**
	 * Called when the Alarms button is clicked
	 * 
	 * @param view
	 */
	public void AlarmBtn_OnClick(View view) {
		// store any values for conditions page that may have been changed
		EditText rainText = (EditText) findViewById(R.id.rainOffset);
		EditText snowText = (EditText) findViewById(R.id.snowOffset);
		CheckBox rainSwitch = (CheckBox) findViewById(R.id.rainCheckbox);
		CheckBox snowSwitch = (CheckBox) findViewById(R.id.snowCheckbox);

		// store rain properties
		if (rainSwitch.isChecked()) {
			if (!rainText.getText().toString().matches(StringHelper.EMPTY)) {
				AlarmInfo.setRainSelected(true);
				AlarmInfo.setRainOffset(Integer.parseInt(rainText.getText().toString()));
			} else {
				AlarmInfo.setRainSelected(true);
				AlarmInfo.setRainOffset(0);
			}
		} else {
			AlarmInfo.setRainSelected(false);
			AlarmInfo.setRainOffset(0);
		}

		// store snow properties
		if (snowSwitch.isChecked()) {
			if (!snowText.getText().toString().matches(StringHelper.EMPTY)) {
				AlarmInfo.setSnowSelected(true);
				AlarmInfo.setSnowOffset(Integer.parseInt(snowText.getText().toString()));
			} else {
				AlarmInfo.setSnowSelected(true);
				AlarmInfo.setSnowOffset(0);
			}

		} else {
			AlarmInfo.setSnowSelected(false);
			AlarmInfo.setSnowOffset(0);
		}

		// load createalarm view
		setContentView(R.layout.createalarm);

		// load any properties in AlarmInfo where applicable
		ToggleButton sunBtn = (ToggleButton) findViewById(R.id.sundayToggleButton);
		sunBtn.setChecked(AlarmInfo.getSun());

		ToggleButton monBtn = (ToggleButton) findViewById(R.id.mondayToggleButton);
		monBtn.setChecked(AlarmInfo.getMon());

		ToggleButton tuesBtn = (ToggleButton) findViewById(R.id.tuesdayToggleButton);
		tuesBtn.setChecked(AlarmInfo.getTues());

		ToggleButton wedsBtn = (ToggleButton) findViewById(R.id.wednesdayToggleButton);
		wedsBtn.setChecked(AlarmInfo.getWeds());

		ToggleButton thursBtn = (ToggleButton) findViewById(R.id.thursdayToggleButton);
		thursBtn.setChecked(AlarmInfo.getThurs());

		ToggleButton friBtn = (ToggleButton) findViewById(R.id.fridayToggleButton);
		friBtn.setChecked(AlarmInfo.getFri());

		ToggleButton satBtn = (ToggleButton) findViewById(R.id.saturdayToggleButton);
		satBtn.setChecked(AlarmInfo.getSat());

		TimePicker timePicker = (TimePicker) findViewById(R.id.mainTimePicker);
		timePicker.setCurrentHour(AlarmInfo.getMainAlarmHour());
		timePicker.setCurrentMinute(AlarmInfo.getMainAlarmMinute());

		EditText alarmName = (EditText) findViewById(R.id.alarmNameTxt);
		alarmName.setText(AlarmInfo.getAlarmName());
	}
	
	public void RainCheckBox_OnClick(View view) {
		CheckBox rainCheckBox = (CheckBox) findViewById(R.id.rainCheckbox);
		EditText rainText = (EditText) findViewById(R.id.rainOffset);
		
		if (rainCheckBox.isChecked())
			rainText.setEnabled(true);
		else {
			rainText.setText(StringHelper.EMPTY);
			rainText.setEnabled(false);
		}
	}
	
	public void SnowCheckBox_OnClick(View view) {
		CheckBox snowCheckBox = (CheckBox) findViewById(R.id.snowCheckbox);
		EditText snowText = (EditText) findViewById(R.id.snowOffset);
		
		if (snowCheckBox.isChecked())
			snowText.setEnabled(true);
		else {
			snowText.setText(StringHelper.EMPTY);
			snowText.setEnabled(false);
		}
			
	}
	
	private void CancelButton_OnClick(View view) {
		AlarmInfo.reset();
		setContentView(R.layout.mainwindow);
	}

	private void populateAllAlarmsListView() {
		Cursor cursor = db.fetchAllAlarms();

		AlarmListCursorAdapter cursorAdapter = new AlarmListCursorAdapter(this, cursor);

		// Set adapter for list view
		ListView allAlarms = (ListView) findViewById(R.id.allAlarms);
		allAlarms.setEmptyView(findViewById(R.id.empty_list_item));
		allAlarms.setAdapter(cursorAdapter);

		long pos = AlarmInfo.getSelectedPos();
		if (pos != -1)
			allAlarms.setSelection((int) pos);
		else
			allAlarms.clearChoices();

		// TODO: try this out. This may solve the issue where after you edit one
		// alarm you can't edit again.
		registerListClickCallback();

	}
	
	public void showAlarm() {
		setContentView(R.layout.alarm_activated);
	}

	private void registerListClickCallback() {
		ListView allAlarms = (ListView) findViewById(R.id.allAlarms);

		allAlarms.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
				System.out.println(String.valueOf(id));
				AlarmInfo.setSelectedPos(id);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TextView temp = (TextView) view;
		Toast toast = Toast.makeText(this, temp.getText() + " ID: " + String.valueOf(id), Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db != null)
			db.close();
	}
}
