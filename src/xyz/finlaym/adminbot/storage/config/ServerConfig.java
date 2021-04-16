package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.Bot;
import xyz.finlaym.adminbot.action.message.response.CustomResponse;
import xyz.finlaym.adminbot.storage.DBInterface;

public class ServerConfig {
	
	public static final int CURRENCY_FLAG = 1 << 0;
	
	/**
	 * Config for levels (users level up by sending messages)
	 * Key: Guild id as long
	 * Value: True if guild has levels enabled, false otherwise
	 */
	private Map<Long,Long> flags;
	/**
	 * Config for custom responses (look for pattern and send response)
	 * Key: Guild id as long
	 * Value: A list of all the custom responses on the guild
	 */
	private Map<Long,List<CustomResponse>> responses;
	
	private Map<Long,TextChannel> loggingChannels;
	
	private DBInterface dbInterface;
	private Bot bot;
	
	public ServerConfig(DBInterface dbInterface, Bot bot) {
		this.flags = new HashMap<Long,Long>();
		this.responses = new HashMap<Long,List<CustomResponse>>();
		this.loggingChannels = new HashMap<Long,TextChannel>();
		this.dbInterface = dbInterface;
		this.bot = bot;
	}
	public void saveConfig(long id) throws Exception{
		dbInterface.saveServerConfig(id, this);
	}
	public void loadConfig(long id) throws Exception{
		dbInterface.getServerConfig(id, this);
	}
	public long getFlags(long id) {
		if(!flags.containsKey(id))
			return 0;
		return flags.get(id);
	}
	public void setFlags(long id, long value) {
		flags.put(id, value);
	}
	public List<CustomResponse> getResponses(long gid){
		return responses.get(gid);
	}
	public void setResponses(long gid, List<CustomResponse> response) {
		responses.put(gid, response);
	}
	public TextChannel getLoggingChannel(long gid) {
		return loggingChannels.get(gid);
	}
	public void setLoggingChannel(long gid, long id) {
		if(id != 0)
			loggingChannels.put(gid, bot.getJDA().getTextChannelById(id));
	}
	
}
