package xyz.finlaym.adminbot.action.timer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.storage.config.TimedEventConfig;

public class TimedEventManager extends Thread{
	
	private static final Logger logger = LoggerFactory.getLogger(TimedEventManager.class);
	
	private TimedEventConfig eConfig;
	private List<TimedEventCallback> callbacks = new ArrayList<TimedEventCallback>();
	public TimedEventManager(TimedEventConfig eConfig) {
		this.eConfig = eConfig;
		start();
	}
	@Override
	public void run() {
		while(true) {
			// Poll events
			List<TimedEvent> events = eConfig.getOverdueEvents();
			for(TimedEvent e : events) {
				runEvent(e);
			}
			
			try {
				// Sleep for 10s
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void addEvent(TimedEvent e) {
		this.eConfig.addEvent(e);
	}
	private void runEvent(TimedEvent e) {
		if(callbacks.size() <= e.getType()) {
			logger.error("Error: Found timed event of unknown type!!! type:"+e.getType()+" gid:"+e.getGid()+" uid:"+e.getUid());
			return;
		}
		callbacks.get((int) e.getType()).execute(e);
	}
	public void registerEventType(long type, TimedEventCallback callback) {
		this.callbacks.set((int) type, callback);
	}
}
