package com.example.ConditionalAlarmClock;

/**
 * Class used to manage DayOfWeek masks used in this application
 * @author Ian Duckworth
 *
 */
public abstract class Mask {
	
	//constructor exists to prevent instantiation
	private Mask() {}
	
	public static final int SUNDAY = 	1 << 0;	//1
	public static final int MONDAY = 	1 << 1;	//2
	public static final int TUESDAY =	1 << 2;	//4
	public static final int WEDNESDAY = 1 << 3;	//8
	public static final int THURSDAY =	1 << 4;	//16
	public static final int FRIDAY =	1 << 5; //32
	public static final int SATURDAY =	1 << 6;	//64
	public static final int AM = 		1 << 7; //128
	
	/**
	 * Adds a new value to an existing mask.
	 * @param current Mask that you have created already
	 * @param mask an integer representing the value that you want to change.  For example, Mask.SUNDAY
	 * @return the updated mask with the new value added
	 */
	public static int addToMask(int current, int mask) {
		return current | mask;
	}
	
	/**
	 * Checks if a value is present in a mask
	 * @param current Mask that you have created already
	 * @param mask an integer representing the value that you want to check against.  For example, Mask.SUNDAY
	 * @return true if mask is present in current, false if it is not
	 */
	public static boolean isPresent(int current, int mask) {
		return (current & mask) == mask;
	}
}
