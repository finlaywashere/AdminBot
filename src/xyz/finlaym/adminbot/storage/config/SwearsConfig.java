package xyz.finlaym.adminbot.storage.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.swear.SwearWord;
import xyz.finlaym.adminbot.action.message.swear.SwearWord.ActivationType;
import xyz.finlaym.adminbot.storage.DBInterface;

public class SwearsConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(SwearsConfig.class);
	private DBInterface dbInterface;

	/**
	 * A map to store all of the swear words active in every guild
	 * Key: Guild id as long
	 * Value: A list of swear words active for the guild
	 */
	private Map<Long,List<SwearWord>> swearWords = new HashMap<Long,List<SwearWord>>();
	
	public SwearsConfig(DBInterface dbInterface) {
		this.dbInterface = dbInterface;
	}
	public List<SwearWord> getSwears(long id){
		return swearWords.get(id);
	}
	public void addSwear(SwearWord s, long guildId){
		if(!swearWords.containsKey(guildId))
			swearWords.put(guildId, new ArrayList<SwearWord>());
		swearWords.get(guildId).add(s);
	}
	public void removeSwear(SwearWord s, long guildId) {
		if(!swearWords.containsKey(guildId))
			return;
		swearWords.get(guildId).remove(s);
	}
	public void loadSwears(long guildId) throws Exception {
		if(swearWords.containsKey(guildId))
			swearWords.put(guildId, new ArrayList<SwearWord>());
		dbInterface.getSwears(guildId, this);
	}
	public void saveSwears(long guildId) throws Exception {
		if(!swearWords.containsKey(guildId))
			return;
		dbInterface.saveSwears(guildId, this);
	}
	
	public SwearWord isSwear(long guildId, String text, ActivationType type) {
		if(!swearWords.containsKey(guildId)) {
			try {
				loadSwears(guildId);
			} catch (Exception e) {
				logger.error("Poggers, ewwor werror in isSwear", e.getCause());
			}
		}
		List<SwearWord> swears = swearWords.get(guildId);
		if(swears == null)
			return null;
		for (int i = 0; i < swears.size(); i++) {
			SwearWord word = swears.get(i);
			if ((word.getType() == type || type == ActivationType.ANY) && text.contains(word.getWord())) {
				// Oh No!!! Swear word detected!
				return word;
			}
		}
		return null;
	}
}
