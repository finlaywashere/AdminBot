package xyz.finlaym.adminbot.action.session;

import java.util.HashMap;
import java.util.Map;

public class Session {
	private Map<String,String> variables;
	private CommandHistory history;
	
	public Session() {
		variables = new HashMap<String,String>();
		history = new CommandHistory();
	}

	public CommandHistory getHistory() {
		return history;
	}
	public void setHistory(CommandHistory history) {
		this.history = history;
	}
	public Map<String, String> getVariables() {
		return variables;
	}
}
