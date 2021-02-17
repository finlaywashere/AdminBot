package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.finlaym.adminbot.action.message.response.CustomResponse;
import xyz.finlaym.adminbot.storage.DBInterface;

public class ServerConfig {
	/**
	 * Config for levels (users level up by sending messages)
	 * Key: Guild id as long
	 * Value: True if guild has levels enabled, false otherwise
	 */
	private Map<Long,Boolean> levelsEnabled;
	/**
	 * Config for custom responses (look for pattern and send response)
	 * Key: Guild id as long
	 * Value: A list of all the custom responses on the guild
	 */
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
