package com.example.ConditionalAlarmClock;

public abstract class WeatherMapper {
	private WeatherMapper() {}
	
	public static int getWeatherMap(String apiWeather) {
		if (	   apiWeather.contains("Light Drizzle")
				|| apiWeather.contains("Heavy Drizzle")
				|| apiWeather.contains("Light Rain")
				|| apiWeather.contains("Heavy Rain")
				|| apiWeather.contains("Mist")
				|| apiWeather.contains("Fog")
				|| apiWeather.contains("Squalls")
				|| apiWeather.contains("Spray")
				|| apiWeather.contains("Precipitation")) {
			return MyWeather.RAIN;
		}
		
		if (	   apiWeather.contains("Snow")
				|| apiWeather.contains("Ice")
				|| apiWeather.contains("Hail")
				|| apiWeather.contains("Freezing")) {		
			return MyWeather.SNOW;
		}
		
		return MyWeather.CLEAR;
	}
}


