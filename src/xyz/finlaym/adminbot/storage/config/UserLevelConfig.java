package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import xyz.finlaym.adminbot.storage.DBInterface;

public class UserLevelConfig {
	private Map<Long,Integer> levels;
	private Lock levelsLock = new ReentrantLock();
	private DBInterface dbInterface;
	
	public UserLevelConfig(DBInterface dbInterface) {
		this.dbInterface = dbInterface;
		this.levels = new HashMap<Long,Integer>();
	}
	public void loadLevels(long id) throws Exception {
		levelsLock.lock();
		dbInterface.loadUserLevels(id,this);
		levelsLock.unlock();
	}
	public void saveLevels(long id) throws Exception {
		levelsLock.lock();
		dbInterface.saveUserLevels(id, this);
		levelsLock.unlock();
	}
	public int getUserLevels(long id) {
		levelsLock.lock();
		if(!levels.containsKey(id)) {
			levelsLock.unlock();
			return 0;
		}
		int level = levels.get(id);
		levelsLock.unlock();
		return level;
	}
	public void setUserLevels(long id, int level) {
		levelsLock.lock();
		levels.put(id, level);
		levelsLock.unlock();
	}
}
