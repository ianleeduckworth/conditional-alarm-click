package com.example.ConditionalAlarmClock;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AlarmListCursorAdapter extends CursorAdapter {
	public AlarmListCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor, 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.alarm_list_item, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Alarm alarm = Alarm.alarmFactory(cursor);
		
		//set alarm name
		TextView nameOfAlarm = (TextView) view.findViewById(R.id.nameOfAlarm);
		nameOfAlarm.setText(alarm.getName());
		
		//set alarm days of week
		ToggleButton sun = (ToggleButton) view.findViewById(R.id.toggleButton_Sun);
		sun.setChecked(alarm.getSun());
		
		ToggleButton mon = (ToggleButton) view.findViewById(R.id.toggleButton_Mon);
		mon.setChecked(alarm.getMon());
		
		ToggleButton tues = (ToggleButton) view.findViewById(R.id.toggleButton_Tues);
		tues.setChecked(alarm.getTues());
		
		ToggleButton weds = (ToggleButton) view.findViewById(R.id.toggleButton_Wed);
		weds.setChecked(alarm.getWeds());
		
		ToggleButton thurs = (ToggleButton) view.findViewById(R.id.toggleButton_Thu);
		thurs.setChecked(alarm.getThurs());
		
		ToggleButton fri = (ToggleButton) view.findViewById(R.id.toggleButton_Fri);
		fri.setChecked(alarm.getFri());
		
		ToggleButton sat = (ToggleButton) view.findViewById(R.id.toggleButton_Sat);
		sat.setChecked(alarm.getSat());
		
		//set alarm time
		TextView timeOfAlarm = (TextView) view.findViewById(R.id.timeOfAlarm);
		
		Calendar time = alarm.getMainAlarmTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
		String formattedTime = dateFormat.format(time.getTime());
		String appendage = alarm != null && alarm.isAm() ? " AM" : " PM";
		timeOfAlarm.setText(formattedTime + appendage);
		
		//set colors of rain and snow indicators
		TextView isRain = (TextView) view.findViewById(R.id.isRain);
		if (alarm.getRainOffset() > 0) {
			isRain.setTextColor(Color.GREEN);
		} else {
			isRain.setTextColor(Color.GRAY);
		}
		
		TextView isSnow = (TextView) view.findViewById(R.id.isSnow);
		if (alarm.getSnowOffset() > 0) {
			isSnow.setTextColor(Color.GREEN);
		} else {
			isSnow.setTextColor(Color.GRAY);
		}
		
		
	}
}
