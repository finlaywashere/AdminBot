package xyz.finlaym.adminbot.action.message.response;

import java.util.ArrayList;
import java.util.List;

public class CustomResponse {
	private String trigger;
	private String response;
	
	public CustomResponse(String trigger, String response) {
		this.trigger = trigger;
		this.response = response;
	}
	public String getTrigger() {
		return trigger;
	}
	public String getResponse() {
		return response;
	}
	@Override
	public String toString() {
		return strip(response)+","+strip(trigger);
	}
	private static String strip(String s) {
		return s.replaceAll(":", "\\colon").replaceAll(",", "\\comma");
	}
	private static String unstrip(String s) {
		return s.replaceAll("\\colon", ":").replaceAll("\\comma", ",");
	}
	public static CustomResponse fromStringSingle(String s) {
		String[] s2 = s.split(",",2);
		CustomResponse r = new CustomResponse(unstrip(s2[1]),unstrip(s2[0]));
		return r;
	}
	public static List<CustomResponse> fromString(String s){
		if(s.isEmpty())
			return null;
		List<CustomResponse> responses = new ArrayList<CustomResponse>();
		for(String s1 : s.split(":")) {
			responses.add(fromStringSingle(s1));
		}
		return responses;
	}
}
