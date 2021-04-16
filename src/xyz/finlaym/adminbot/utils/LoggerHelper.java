package xyz.finlaym.adminbot.utils;

import org.slf4j.Logger;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import xyz.finlaym.adminbot.storage.DBInterface;

public class LoggerHelper {
	
	public static void log(Logger logger, Guild guild, TextChannel loggingChannel, User user, String message, DBInterface db) {
		logger.info("Guild "+guild.getIdLong()+" (\""+guild.getName()+"\"): User "+user.getIdLong()+" (\""+user.getAsTag()+"\") "+message);
		db.logAccess(guild.getIdLong(), user.getIdLong(), message);
		if(loggingChannel != null)
			loggingChannel.sendMessage("User "+user.getAsTag()+": "+message).queue();
	}
	public static void log(Logger logger, String message) {
		logger.info(message);
	}
	public static void logError(Logger logger, Guild guild, Throwable cause, String message, DBInterface db) {
		long gid = 0;
		if(guild != null)
			gid = guild.getIdLong();
		logger.error("Error: Guild "+guild.getIdLong()+" (\""+guild.getName()+"\"): "+message);
		db.logError(gid, cause, message);
	}
}
