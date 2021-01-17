package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.Map;

import xyz.finlaym.adminbot.storage.DBInterface;

public class UserLevelConfig {
	private Map<Long,Map<Long,Integer>> levels;
	private DBInterface dbInterface;
	
	public UserLevelConfig(DBInterface dbInterface) {
		this.dbInterface = dbInterface;
		this.levels = new HashMap<Long,Map<Long,Integer>>();
	}
	public void loadLevels(long gid, long id) throws Exception {
		dbInterface.loadUserLevels(gid, id,this);
	}
	public void saveLevels(long gid, long id) throws Exception {
		dbInterface.saveUserLevels(gid, id, this);
	}
	public int getUserLevels(long gid, long id) {
		if(!levels.containsKey(gid)) {
			return 0;
		}
		Map<Long,Integer> levels2 = levels.get(gid);
		if(!levels2.containsKey(id))
			return 0;
		return levels2.get(id);
	}
	public void setUserLevels(long gid, long id, int level) {
		Map<Long,Integer> levels2 = levels.get(gid);
		if(levels2 == null)
			levels2 = new HashMap<Long,Integer>();
		levels2.put(id, level);
		levels.put(gid, levels2);
	}
}
