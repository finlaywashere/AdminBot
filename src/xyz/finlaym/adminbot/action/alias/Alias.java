package xyz.finlaym.adminbot.action.alias;

import java.util.ArrayList;
import java.util.List;

public class Alias {
	
	private String originalValue;
	private String newValue;
	
	public Alias(String originalValue, String newValue) {
		this.originalValue = originalValue;
		this.newValue = newValue;
	}
	public String getOriginalValue() {
		return originalValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public static List<Alias> valueOf(String s){
		List<Alias> ret = new ArrayList<Alias>();
		if(s == null || s.length() == 0)
			return ret;
		String[] split = s.split(":");
		for(String s1 : split) {
			String[] split2 = s1.split(",");
			Alias a = new Alias(split2[0],split2[1]);
			ret.add(a);
		}
		return ret;
	}
	@Override
	public String toString() {
		return originalValue + "," + newValue;
	}
}
