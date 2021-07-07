package xyz.finlaym.adminbot.action.script;

import java.util.ArrayList;
import java.util.List;

public class Script {
	private List<String> commands;
	private String name;
	
	public Script(String s, String name) {
		this.commands = new ArrayList<String>();
		this.name = name;
		for(String s1 : s.split("\n")) {
			if(s1.trim().length() != 0)
				this.commands.add(s1);
		}
	}
	public List<String> getCommands() {
		return commands;
	}
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		String s = "";
		for(String c : commands) {
			s += "\n" + c;
		}
		if(s.length() > 0)
			s = s.substring(1);
		return s;
	}
}
