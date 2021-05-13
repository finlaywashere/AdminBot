package xyz.finlaym.adminbot.action.message.command.commands.alias;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.alias.Alias;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class SetAliasCommand extends Command{
	
	private static final Logger logger = LoggerFactory.getLogger(SetAliasCommand.class);

	public SetAliasCommand() {
		super("setalias", "command.setalias", "-setalias <command name> <new command name>", "Sets a recursively resolved command alias");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		if(command.length != 3) {
			return new CommandResponse("Usage: "+usage,true);
		}
		long gid = info.getGid();
		ServerConfig sConfig = info.getHandler().getBot().getServerConfig();
		List<Alias> aliases = sConfig.getAliases(gid);
		if(aliases == null || aliases.size() == 0) {
			try {
				sConfig.loadConfig(gid);
			} catch (Exception e) {
				logger.error("Failed to load server configuration for guild", e);
				return new CommandResponse("Critical database error: Failed to load server configuration!",true);
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
			return new CommandResponse("Critical database error: Failed to save server configuration!",true);
		}
		return new CommandResponse("Successfully added alias");
	}
}
