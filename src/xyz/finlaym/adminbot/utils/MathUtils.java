package xyz.finlaym.adminbot.utils;

public class MathUtils {
	public static boolean isLong(String s) {
		try {
			@SuppressWarnings("unused")
			long l = Long.valueOf(s);
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}
	public static boolean isInt(String s) {
		try {
			@SuppressWarnings("unused")
			int i = Integer.valueOf(s);
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}
}
