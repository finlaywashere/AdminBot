package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.Map;

import xyz.finlaym.adminbot.storage.DBInterface;

public class CurrencyConfig {
	/**
	 * The current level (rank kind of) of users in every guild
	 * Key: Guild id as long
	 * Value: A map of user id to level integer
	 */
	private Map<Long,Map<Long,Integer>> levels;
	private DBInterface dbInterface;
	
	public CurrencyConfig(DBInterface dbInterface) {
		this.dbInterface = dbInterface;
		this.levels = new HashMap<Long,Map<Long,Integer>>();
	}
	public void loadCurrency(long gid, long id) throws Exception {
		dbInterface.loadCurrency(gid, id,this);
	}
	public void saveCurrency(long gid, long id) throws Exception {
		dbInterface.saveCurrency(gid, id, this);
	}
	public int getCurrency(long gid, long id) {
		if(!levels.containsKey(gid)) {
			return 0;
		}
		Map<Long,Integer> levels2 = levels.get(gid);
		if(!levels2.containsKey(id))
			return 0;
		return levels2.get(id);
	}
	public void setCurrency(long gid, long id, int level) {
		Map<Long,Integer> levels2 = levels.get(gid);
		if(levels2 == null)
			levels2 = new HashMap<Long,Integer>();
		levels2.put(id, level);
		levels.put(gid, levels2);
	}
}
