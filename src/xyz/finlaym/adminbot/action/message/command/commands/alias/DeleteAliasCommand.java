package xyz.finlaym.adminbot.action.message.command.commands.alias;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.alias.Alias;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.utils.MathUtils;

public class DeleteAliasCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(DeleteAliasCommand.class);
	
	public DeleteAliasCommand() {
		super("deletealias", "command.deletealias", "-deletealias <id>", "Deletes a command alias");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		if(command.length != 2 || !MathUtils.isInt(command[1])) {
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		int id = Integer.valueOf(command[1])-1;
		if(id < 0) {
			channel.sendMessage("Alias id cannot be less than 1!").queue();
			return;
		}
		long gid = channel.getGuild().getIdLong();
		ServerConfig sConfig = handler.getBot().getServerConfig();
		List<Alias> aliases = sConfig.getAliases(gid);
		if(aliases == null || aliases.size() == 0) {
			try {
				sConfig.loadConfig(gid);
			} catch (Exception e) {
				logger.error("Failed to load server configuration for guild!",e);
				channel.sendMessage("Critical error: Failed to load server configuration from database!").queue();
				return;
			}
			aliases = sConfig.getAliases(gid);
		}
		if(id >= aliases.size()) {
			channel.sendMessage("Alias id cannot be greater than "+aliases.size()+"!").queue();
			return;
		}
		aliases.remove(id);
		sConfig.setAliases(gid, aliases);
		try {
			sConfig.saveConfig(gid);
		} catch (Exception e) {
			logger.error("Failed to save server configuration for guild", e);
			channel.sendMessage("Critical error: Failed to save server configuration to database!").queue();
			return;
		}
		if(!silence)
			channel.sendMessage("Successfully deleted alias from database!").queue();
	}
}
