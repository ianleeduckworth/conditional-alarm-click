package com.example.ConditionalAlarmClock;

//Takes an instance of Alarm and applies it to AlarmInfo.  Acts a ViewModel
public abstract class AlarmInfoAdapter {
	
	public static void adaptAlarm (Alarm alarm) {
		AlarmInfo.setAlarmName(alarm.getName());
		AlarmInfo.setAm(alarm.isAm());
		
		AlarmInfo.setSun(alarm.getSun());
		AlarmInfo.setMon(alarm.getMon());
		AlarmInfo.setTues(alarm.getTues());
		AlarmInfo.setWeds(alarm.getWeds());
		AlarmInfo.setThurs(alarm.getThurs());
		AlarmInfo.setFri(alarm.getFri());
		AlarmInfo.setSat(alarm.getSat());
		
		int rainOffset = alarm.getRainOffset();
		int snowOffset = alarm.getSnowOffset();
		
		if (rainOffset == 0)
			AlarmInfo.setRainSelected(false);
		else
			AlarmInfo.setRainSelected(true);
		
		if (snowOffset == 0)
			AlarmInfo.setRainSelected(false);
		else
			AlarmInfo.setRainSelected(true);
		
		AlarmInfo.setRainOffset(rainOffset);
		AlarmInfo.setSnowOffset(snowOffset);
		
		AlarmInfo.setMainAlarmHour(alarm.getMainAlarmHour());
		AlarmInfo.setMainAlarmMinute(alarm.getMainAlarmMinute());
	}
	
	public static Alarm adaptAlarmInfo() {
		if (AlarmInfo.getSelectedPos() != -1)
			return new Alarm(
					AlarmInfo.getAlarmName(),
					convertDaysOfWeek(),
					AlarmInfo.getSnowOffset(),
					AlarmInfo.getRainOffset(),
					AlarmInfo.getMainAlarmHour(),
					AlarmInfo.getMainAlarmMinute(),
					AlarmInfo.getSelectedPos());
		
		return new Alarm(
				AlarmInfo.getAlarmName(),
				convertDaysOfWeek(),
				AlarmInfo.getSnowOffset(),
				AlarmInfo.getRainOffset(),
				AlarmInfo.getMainAlarmHour(),
				AlarmInfo.getMainAlarmMinute());
	}
	
	// Checks static AlarmInfo class and converts it into a dayOfWeek bitmask
	private static int convertDaysOfWeek() {
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
	
}
