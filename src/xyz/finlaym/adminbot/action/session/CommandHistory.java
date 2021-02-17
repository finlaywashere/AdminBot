package xyz.finlaym.adminbot.action.session;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {
	private List<HistoryElement> history;
	public CommandHistory() {
		this.history = new ArrayList<HistoryElement>();
	}
	public HistoryElement get(int index) {
		return history.get(history.size()-index-1);
	}
	public int size() {
		return history.size();
	}
	public void add(HistoryElement element) {
		this.history.add(element);
	}
}
