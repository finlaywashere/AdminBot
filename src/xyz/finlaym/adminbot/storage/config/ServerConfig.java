package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.finlaym.adminbot.action.message.response.CustomResponse;
import xyz.finlaym.adminbot.storage.DBInterface;

public class ServerConfig {
	private Map<Long,Boolean> levelsEnabled;
	private Map<Long,List<CustomResponse>> responses;
	private DBInterface dbInterface;
	
	public ServerConfig(DBInterface dbInterface) {
		this.levelsEnabled = new HashMap<Long,Boolean>();
		this.responses = new HashMap<Long,List<CustomResponse>>();
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
	public List<CustomResponse> getResponses(long gid){
		return responses.get(gid);
	}
	public void setResponses(long gid, List<CustomResponse> response) {
		responses.put(gid, response);
	}
}
