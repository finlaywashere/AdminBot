package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import xyz.finlaym.adminbot.storage.DBInterface;

public class ServerConfig {
	private Map<Long,Boolean> levelsEnabled;
	private DBInterface dbInterface;
	private Lock levelsLock;
	
	public ServerConfig(DBInterface dbInterface) {
		this.levelsEnabled = new HashMap<Long,Boolean>();
		this.levelsLock = new ReentrantLock();
		this.dbInterface = dbInterface;
	}
	public void saveConfig(long id) throws Exception{
		levelsLock.lock();
		dbInterface.saveServerConfig(id, this);
		levelsLock.unlock();
	}
	public void loadConfig(long id) throws Exception{
		levelsLock.lock();
		dbInterface.getServerConfig(id, this);
		levelsLock.unlock();
	}
	public boolean getLevelsEnabled(long id) {
		levelsLock.lock();
		if(!levelsEnabled.containsKey(id))
			return false;
		boolean levelsEnabledB = levelsEnabled.get(id);
		levelsLock.unlock();
		return levelsEnabledB;
	}
	public void setLevelsEnabled(long id, boolean value) {
		levelsLock.lock();
		levelsEnabled.put(id, value);
		levelsLock.unlock();
	}
}
