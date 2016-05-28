package com.example.osc2016nagoyateam3;

public class Houikaku {

	private static final double earthR = 6378.137; // km

	// @see http://keisan.casio.jp/exec/system/1257670779
	// 0 : North
	// 90 : East
	// 180 : Soth
	// 270 : West
	public static double getHouikakuBtw2(GeoPos pos1, GeoPos pos2) {
		final double delta = getDelta(pos1, pos2);
		final double h = 90 - Math.atan2(Math.sin(delta),  Math.cos(pos1.lat)*Math.tan(pos2.lat) - Math.sin(pos1.lat)*Math.cos(delta));
		return h;
	}
	public static double getDistanceBtw2(GeoPos pos1, GeoPos pos2) {
		final double delta = getDelta(pos1, pos2);
		final double d = earthR * Math.acos(Math.sin(pos1.lat)*Math.sin(pos2.lat) + Math.cos(pos1.lat)*Math.cos(pos2.lat)*Math.cos(delta));
		return d;
	}
	private static double getDelta(GeoPos pos1, GeoPos pos2) {
		final double delta = pos2.lon - pos1.lon;
		return delta;
	}
}
