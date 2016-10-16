package com.example.ConditionalAlarmClock;

public abstract class ConvertHelper {

	//class should not be instantiated
	private ConvertHelper() {}
    
    public static int convertToDaysOfWeek() {
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
		
		convertToAlarmInfo(daysOfWeek);

		return daysOfWeek;
    }
    
    //Takes a dayOfWeek mask retrieved from the database and converts it into AlarmInfo data for each day
    //of the week
    public static void convertToAlarmInfo(int mask) {
    	if (Mask.isPresent(mask, Mask.SUNDAY))
    		System.out.println("Alarm will ring on Sunday.");		//line is for test only.  Should actually update AlarmInfo
    	if (Mask.isPresent(mask, Mask.MONDAY))
    		System.out.println("Alarm will ring on Monday.");		//line is for test only.  Should actually update AlarmInfo
    	if (Mask.isPresent(mask, Mask.TUESDAY))
    		System.out.println("Alarm will ring on Tuesday.");		//line is for test only.  Should actually update AlarmInfo
    	if (Mask.isPresent(mask, Mask.WEDNESDAY))
    		System.out.println("Alarm will ring on Wednesday.");	//line is for test only.  Should actually update AlarmInfo
    	if (Mask.isPresent(mask, Mask.THURSDAY))
    		System.out.println("Alarm will ring on Thursday.");		//line is for test only.  Should actually update AlarmInfo
    	if (Mask.isPresent(mask, Mask.FRIDAY))
    		System.out.println("Alarm will ring on Friday.");		//line is for test only.  Should actually update AlarmInfo
    	if (Mask.isPresent(mask, Mask.SATURDAY))
    		System.out.println("Alarm will ring on Saturday.");		//line is for test only.  Should actually update AlarmInfo
    }
}
