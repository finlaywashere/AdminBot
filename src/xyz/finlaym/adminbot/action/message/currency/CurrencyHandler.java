package xyz.finlaym.adminbot.action.message.currency;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.Bot;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.storage.config.CurrencyConfig;

public class CurrencyHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(CurrencyHandler.class);

	private Map<Long,Integer> currMessageCount = new HashMap<Long,Integer>();
	private Bot bot;
	
	public CurrencyHandler(Bot bot) {
		this.bot = bot;
	}

	public Bot getBot() {
		return bot;
	}
	
	public void countMessages(long gid, long id, TextChannel channel, String mention) {
		ServerConfig seConfig = bot.getServerConfig();
		if((seConfig.getFlags(id) & ServerConfig.CURRENCY_FLAG) == 1)
			return;
		if(!currMessageCount.containsKey(id)) {
			currMessageCount.put(id, 1);
			return;
		}
		CurrencyConfig cConfig = bot.getCurrencyConfig();
		int messageCount = currMessageCount.get(id);
		messageCount++;
		int level = cConfig.getCurrency(gid, id);
		if(messageCount >= 10) {
			messageCount = 0;
			cConfig.setCurrency(gid, id, level+1);
			try {
				cConfig.saveCurrency(gid, id);
			} catch (Exception e) {
				logger.error("UwU program did an oopsie woopsie when it twiedd to swave the fwile", e.getCause());
			}
		}
		currMessageCount.put(id, messageCount);
	}
}
