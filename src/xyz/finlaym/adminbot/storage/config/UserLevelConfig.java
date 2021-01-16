package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.Map;

import xyz.finlaym.adminbot.storage.DBInterface;

public class UserLevelConfig {
	private Map<Long,Integer> levels;
	private DBInterface dbInterface;
	
	public UserLevelConfig(DBInterface dbInterface) {
		this.dbInterface = dbInterface;
		this.levels = new HashMap<Long,Integer>();
	}
	public void loadLevels(long id) throws Exception {
		dbInterface.loadUserLevels(id,this);
	}
	public void saveLevels(long id) throws Exception {
		dbInterface.saveUserLevels(id, this);
	}
	public int getUserLevels(long id) {
		if(!levels.containsKey(id)) {
			return 0;
		}
		int level = levels.get(id);
		return level;
	}
	public void setUserLevels(long id, int level) {
		levels.put(id, level);
	}
}
