package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.Map;

import xyz.finlaym.adminbot.storage.DBInterface;

public class ServerConfig {
	private Map<Long,Boolean> levelsEnabled;
	private DBInterface dbInterface;
	
	public ServerConfig(DBInterface dbInterface) {
		this.levelsEnabled = new HashMap<Long,Boolean>();
		this.dbInterface = dbInterface;
	}
	public void saveConfig(long id) throws Exception{
		dbInterface.saveServerConfig(id, this);
	}
	public void loadConfig(long id) throws Exception{
		dbInterface.getServerConfig(id, this);
	}
	public boolean getLevelsEnabled(long id) {
		if(!levelsEnabled.containsKey(id))
			return false;
		boolean levelsEnabledB = levelsEnabled.get(id);
		return levelsEnabledB;
	}
	public void setLevelsEnabled(long id, boolean value) {
		levelsEnabled.put(id, value);
	}
}
