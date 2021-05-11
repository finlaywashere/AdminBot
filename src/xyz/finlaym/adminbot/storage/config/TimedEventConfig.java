package xyz.finlaym.adminbot.storage.config;

import java.util.ArrayList;
import java.util.List;

import xyz.finlaym.adminbot.action.timer.TimedEvent;
import xyz.finlaym.adminbot.storage.DBInterface;

public class TimedEventConfig{
	
	private List<TimedEvent> events = new ArrayList<TimedEvent>();
	
	private DBInterface dbInterface;

	public TimedEventConfig(DBInterface dbInterface) {
		this.dbInterface = dbInterface;
		loadEvents();
	}
	
	public List<TimedEvent> getOverdueEvents(){
		List<TimedEvent> ret = new ArrayList<TimedEvent>();
		
		long curr = System.currentTimeMillis();
		
		List<TimedEvent> newRepeating = new ArrayList<TimedEvent>();
		List<TimedEvent> toRemove = new ArrayList<TimedEvent>();
		for(TimedEvent e : events) {
			if(e.getRunDate() <= curr) {
				// This event should be run
				ret.add(e);
				if(e.getRepeatCount() != 0) {
					// Construct new event
					long newCount = e.getRepeatCount() != -1 ? e.getRepeatCount() - 1 : e.getRepeatCount();
					TimedEvent newEvent = new TimedEvent(e.getUid(), e.getGid(), e.getRunDate()+e.getRepeatInterval(), e.getRepeatInterval(), newCount, e.getType(), e.getExtra());
					newRepeating.add(newEvent);
				}
			}
			toRemove.add(e);
		}
		
		for(TimedEvent e : toRemove) {
			events.remove(e);
			removeEvent(e);
		}
		
		return ret;
	}
	public void removeEvent(TimedEvent e) {
		dbInterface.removeEvent(this, e);
	}
	public void addEvent(TimedEvent e) {
		dbInterface.addEvent(this, e);
	}
	public void loadEvents() {
		dbInterface.loadEvents(this);
	}

	public List<TimedEvent> getEvents() {
		return events;
	}
	public void setEvents(List<TimedEvent> events) {
		this.events = events;
	}
}
