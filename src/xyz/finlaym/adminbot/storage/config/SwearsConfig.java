package xyz.finlaym.adminbot.storage.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.swear.SwearWord;
import xyz.finlaym.adminbot.action.swear.SwearWord.ActivationType;
import xyz.finlaym.adminbot.storage.DBInterface;

public class SwearsConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(SwearsConfig.class);
	private DBInterface dbInterface;
	
	private Lock swearLock = new ReentrantLock();
	private Map<Long,List<SwearWord>> swearWords = new HashMap<Long,List<SwearWord>>();
	
	public SwearsConfig(DBInterface dbInterface) {
		this.dbInterface = dbInterface;
	}
	public List<SwearWord> getSwears(long id){
		return swearWords.get(id);
	}
	public void addSwear(SwearWord s, long guildId){
		swearLock.lock();
		if(!swearWords.containsKey(guildId))
			swearWords.put(guildId, new ArrayList<SwearWord>());
		swearWords.get(guildId).add(s);
		swearLock.unlock();
	}
	public void removeSwear(SwearWord s, long guildId) {
		swearLock.lock();
		if(!swearWords.containsKey(guildId))
			return;
		swearWords.get(guildId).remove(s);
		swearLock.unlock();
	}
	public void loadSwears(long guildId) throws Exception {
		swearLock.lock();
		if(swearWords.containsKey(guildId))
			swearWords.put(guildId, new ArrayList<SwearWord>());
		dbInterface.getSwears(guildId, this);
		swearLock.unlock();
	}
	public void saveSwears(long guildId) throws Exception {
		swearLock.lock();
		if(!swearWords.containsKey(guildId))
			return;
		dbInterface.saveSwears(guildId, this);
		swearLock.unlock();
	}
	
	public SwearWord isSwear(long guildId, String text, ActivationType type) {
		if(!swearWords.containsKey(guildId)) {
			try {
				loadSwears(guildId);
			} catch (Exception e) {
				logger.error("Pogger, ewwor werror in isSwear", e.getCause());
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
