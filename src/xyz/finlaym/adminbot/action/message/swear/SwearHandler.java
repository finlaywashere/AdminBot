package xyz.finlaym.adminbot.action.message.swear;

import xyz.finlaym.adminbot.Bot;
import xyz.finlaym.adminbot.action.swear.SwearWord;
import xyz.finlaym.adminbot.action.swear.SwearWord.ActivationType;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;

public class SwearHandler {
	private Bot bot;

	public SwearHandler(Bot bot) {
		this.bot = bot;
	}
	public boolean swearCheck(String message, long gid) {
		SwearsConfig sConfig = bot.getSwearsConfig();
		message = message.toLowerCase();
		SwearWord word = sConfig.isSwear(gid, message, ActivationType.MESSAGE);
		if(word != null) {
			return true;
		}
		return false;
	}
}
