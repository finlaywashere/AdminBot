package xyz.finlaym.adminbot.action.message.level;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.Bot;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.storage.config.UserLevelConfig;

public class LevelHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(LevelHandler.class);

	private Map<Long,Integer> currMessageCount = new HashMap<Long,Integer>();
	private Bot bot;
	
	public LevelHandler(Bot bot) {
		this.bot = bot;
	}

	public Bot getBot() {
		return bot;
	}
	private int computeLevelUpLevels(int currLevel) {
		if(currLevel == 1)
			return 5;
		int required = 5;
		for(int i = 1; i <= currLevel; i++) {
			required += i;
			if(required > 100)
				return 100;
		}
		
		return required;
	}
	public void countMessages(long gid, long id, TextChannel channel, String mention) {
		ServerConfig seConfig = bot.getServerConfig();
		if(!seConfig.getLevelsEnabled(id))
			return;
		if(!currMessageCount.containsKey(id)) {
			currMessageCount.put(id, 1);
			return;
		}
		UserLevelConfig uConfig = bot.getUserLevelConfig();
		int messageCount = currMessageCount.get(id);
		messageCount++;
		int level = uConfig.getUserLevels(gid, id);
		if(messageCount >= computeLevelUpLevels(level)) {
			messageCount = 0;
			uConfig.setUserLevels(gid, id, level+1);
			try {
				uConfig.saveLevels(gid, id);
			} catch (Exception e) {
				logger.error("UwU program did an oopsie woopsie when it twiedd to swave the fwile", e.getCause());
			}
			channel.sendMessage("Congratulations "+mention+" for leveling up to level "+(level+1)+"!").queue();
		}
		currMessageCount.put(id, messageCount);
	}
}
