package xyz.finlaym.adminbot.utils;

import org.slf4j.Logger;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class LoggerHelper {
	public static void log(Logger logger, Guild guild, TextChannel channel, User user, String message) {
		logger.info("Guild "+guild.getIdLong()+" (\""+guild.getName()+"\"): User "+user.getIdLong()+" (\""+user.getAsTag()+"\") "+message);
	}
	public static void log(Logger logger, String message) {
		logger.info(message);
	}
}
