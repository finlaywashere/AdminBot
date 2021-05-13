package xyz.finlaym.adminbot.action.message.command.commands.alias;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.alias.Alias;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.utils.MathUtils;

public class DeleteAliasCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(DeleteAliasCommand.class);
	
	public DeleteAliasCommand() {
		super("deletealias", "command.deletealias", "-deletealias <id>", "Deletes a command alias");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		if(command.length != 2 || !MathUtils.isInt(command[1])) {
			return new CommandResponse("Usage: "+usage,true);
		}
		int id = Integer.valueOf(command[1])-1;
		if(id < 0) {
			return new CommandResponse("Alias id cannot be less than 1!",true);
		}
		ServerConfig sConfig = info.getHandler().getBot().getServerConfig();
		List<Alias> aliases = sConfig.getAliases(info.getGid());
		if(aliases == null || aliases.size() == 0) {
			try {
				sConfig.loadConfig(info.getGid());
			} catch (Exception e) {
				logger.error("Failed to load server configuration for guild!",e);
				return new CommandResponse("Critical error: Failed to load server configuration from database!",true);
			}
			aliases = sConfig.getAliases(info.getGid());
		}
		if(id >= aliases.size()) {
			return new CommandResponse("Alias id cannot be greater than "+aliases.size()+"!",true);
		}
		aliases.remove(id);
		sConfig.setAliases(info.getGid(), aliases);
		try {
			sConfig.saveConfig(info.getGid());
		} catch (Exception e) {
			logger.error("Failed to save server configuration for guild", e);
			return new CommandResponse("Critical error: Failed to save server configuration to database!",true);
		}
		return new CommandResponse("Successfully deleted alias from database!");
	}
}
