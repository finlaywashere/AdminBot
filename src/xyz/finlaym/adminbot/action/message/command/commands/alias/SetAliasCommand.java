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

public class SetAliasCommand extends Command{
	
	private static final Logger logger = LoggerFactory.getLogger(SetAliasCommand.class);

	public SetAliasCommand() {
		super("setalias", "command.setalias", "-setalias <command name> <new command name>", "Sets a recursively resolved command alias");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		if(command.length != 3) {
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		long gid = channel.getGuild().getIdLong();
		ServerConfig sConfig = handler.getBot().getServerConfig();
		List<Alias> aliases = sConfig.getAliases(gid);
		if(aliases == null || aliases.size() == 0) {
			try {
				sConfig.loadConfig(gid);
			} catch (Exception e) {
				logger.error("Failed to load server configuration for guild", e);
				channel.sendMessage("Critical database error: Failed to load server configuration!").queue();
				return;
			}
			aliases = sConfig.getAliases(gid);
		}
		Alias a = new Alias(command[1], command[2]);
		aliases.add(a);
		sConfig.setAliases(gid, aliases);
		try {
			sConfig.saveConfig(gid);
		} catch (Exception e) {
			logger.error("Failed to save server configuration for guild", e);
			channel.sendMessage("Critical database error: Failed to save server configuration!").queue();
			return;
		}
		if(!silence)
			channel.sendMessage("Successfully added alias").queue();
	}
}
