package com.bilgeadam.week4.babur.homework;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class WeatherHomework {
	// Global variables
	static String title = "Hava Durumu Programı";
	static String county = BAUtils.readString("Lütfen ilçeyi giriniz");
	static String city = BAUtils.readString("Lütfen şehri giriniz");
	static String country = BAUtils.readString("Lütfen ülkeyi giriniz");
	static String[] locationDatas = { county, city, country };
	static Map<String, Double> locationMap = null;
	
	public static void main(String[] args) {
		BAUtils.header(title);
		getCoordinates();
		getDailyWheatherData();
		getEachThreeHourData();
		BAUtils.footer();
	}
	
	public static void getCoordinates() {
		
		String coordinates = BAUtils.getDirectData(locationDatas[0], locationDatas[1], locationDatas[2]);
		locationMap = coordinatsString2Map(coordinates); // Value is set as "Double" to be able to execute
		System.out.println(
				"\tKOORDİNATLAR:\n\t--------------------------------------------------------------------------------");
		System.out.println(
				"\tEnlem: " + locationMap.get("\"lat\"") + "\n" + "\tBoylam: " + locationMap.get("\"lon\"") + "\n");
	}
	
	public static Map<String, Double> coordinatsString2Map(String value) { // [{"name":"Edremit","lat":39.5961,"lon":27.0244,"country":"TR"}]
		Map<String, Double> map = new HashMap<>();
		String[] splittedValues = value.split(",");
		StringTokenizer stringTokenizer = null;
		
		for (String el : splittedValues) {
			stringTokenizer = new StringTokenizer(el, "[]{}");
			String simplifiedEl = stringTokenizer.nextToken();
			if (simplifiedEl.contains("\"lon\"") || simplifiedEl.contains("\"lat\"")) {
				String[] coordinates = simplifiedEl.split(":");
				map.put(coordinates[0], Double.parseDouble(coordinates[1]));
			}
		}
		return map;
	}
	
	public static void getDailyWheatherData() {
		// {"coord":{"lon":27.0244,"lat":39.5961},"weather":[{"id":801,"main":"Clouds","description":"az
		// bulutlu","icon":"02d"}],"base":"stations","main":{"temp":21.86,"feels_like":21.15,"temp_min":21.86,"temp_max":21.86,"pressure":1017,"humidity":40},
		// "visibility":10000,"wind":{"speed":12.86,"deg":50},"clouds":{"all":20},"dt":1633182157,"sys":{"type":1,"id":7015,"country":"TR",
		// "sunrise":1633147730,"sunset":1633190008},"timezone":10800,"id":315985,"name":"Edremit","cod":200}
		System.out.println(
				"\n\n\tGÜNLÜK HAVA RAPORU:\n\t--------------------------------------------------------------------------------");
		
		String str = BAUtils.getWeatherData(locationMap.get("\"lat\""), locationMap.get("\"lon\""));
		
		Map<String, String> map = new HashMap<>();
		int indexOfWeatherStart = str.indexOf("\"description\"");
		int indexOfWeatherEnd = str.indexOf("\"icon\"");
		String strWeather = str.substring(indexOfWeatherStart, indexOfWeatherEnd - 1); // "description":"az bulutlu", -
																						// 1 = "description":"az
																						// bulutlu"
		String[] splittedStrWeather = strWeather.split(":");
		map.put(splittedStrWeather[0], splittedStrWeather[1]);
		
		int indexOfMainStart = str.indexOf("\"main\":{");
		int indexOfMainEnd = str.indexOf("\"humidity\""); // Instead of humidity there was "sea_level" parameter but
															// some places has no sea_level parameter. So i take
															// humidity as end point.
		String strMain = str.substring(indexOfMainStart + 8, indexOfMainEnd); // "main:{" +8 = lastIndexOfStr
		StringTokenizer stringTokenizer = null;
		String[] splittedStr = strMain.split(",");
		
		for (String el : splittedStr) {
			stringTokenizer = new StringTokenizer(el, "[]{}");
			String simplifiedEls = stringTokenizer.nextToken();
			String[] splittedEls = simplifiedEls.split(":");
			map.put(splittedEls[0], splittedEls[1]);
			
		}
		// {"humidity"=52, "temp_min"=14.88, "description"="az bulutlu", "temp"=14.88,
		// "temp_max"=17.35, "feels_like"=13.78, "pressure"=1022}
		System.out.printf(
				"\tBugün için Hava: %s, En düşük sıcaklık, %sC En yüksek sıcaklık %sC, Hissedilen sıcaklık %sC ve Basınç %skPa\n",
				map.get("\"description\""), map.get("\"temp_min\""), map.get("\"temp_max\""), map.get("\"feels_like\""),
				map.get("\"pressure\""));
		
	}
	
	public static void getEachThreeHourData() {
		String str = BAUtils.getForecastData(locationDatas[0], locationDatas[1], locationDatas[2]);
		Map<String, String> map = new HashMap<>();
		String[] splittedStr = str.split("\"dt\"");
		System.out.println(
				"\n\n\t15 SAATLİK HAVA RAPORU:\n\t--------------------------------------------------------------------------------");
		for (int i = 1; i < splittedStr.length; i++) {
			int indexOfWeatherStart = splittedStr[i].indexOf("\"description\"");
			int indexOfWeatherEnd = splittedStr[i].indexOf("\"icon\"");
			String strWeather = splittedStr[i].substring(indexOfWeatherStart, indexOfWeatherEnd - 1);
			// System.out.println(splittedStr[i]);
			String[] splittedStrWeather = strWeather.split(":");
			map.put(splittedStrWeather[0], splittedStrWeather[1]);
			
			int indexOfDateStart = splittedStr[i].indexOf("dt_txt\"");
			String strDate = splittedStr[i].substring(indexOfDateStart, indexOfDateStart + 20);
			String[] splittedStrDate = strDate.split("\":\"");
			map.put(splittedStrDate[0], splittedStrDate[1]);
			
			String strHour = splittedStr[i].substring(indexOfDateStart + 20, indexOfDateStart + 28);
			map.put("hour", strHour);
			
			int indexOfMainStart = splittedStr[i].indexOf("\"main\":{");
			int indexOfMainEnd = splittedStr[i].indexOf("\"temp_kf\"");
			
			String strMain = splittedStr[i].substring(indexOfMainStart + 8, indexOfMainEnd); // "main:{" +8 =
																								// lastIndexOfStr
			StringTokenizer stringTokenizer = null;
			String[] splittedStrMain = strMain.split(",");
			
			// {"temp":17.86,"feels_like":17.14,"temp_min":14.73,"temp_max":17.86,
			// "pressure":1021,"sea_level":1021,"grnd_level":1018,"humidity":55,
			// "temp_kf":3.13}
			for (String el : splittedStrMain) {
				stringTokenizer = new StringTokenizer(el, "[]{}");
				String simplifiedEls = stringTokenizer.nextToken();
				String[] splittedEls = simplifiedEls.split(":");
				map.put(splittedEls[0], splittedEls[1]);
			}
			System.out.printf(
					"\t%s %s ==> Hava %s, En düşük sıcaklık, %sC En yüksek sıcaklık %sC, Hissedilen sıcaklık %sC, Basınç %skPa ve Nem Oranı Yüzde %s \n",
					map.get("dt_txt"), map.get("hour"), map.get("\"description\""), map.get("\"temp_min\""),
					map.get("\"temp_max\""), map.get("\"feels_like\""), map.get("\"pressure\""),
					map.get("\"humidity\""));
		}
	}
	
}
