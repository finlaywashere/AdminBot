package xyz.finlaym.adminbot.action.message.command.commands.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;

public class ReloadCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(ReloadCommand.class);
	
	public ReloadCommand() {
		super("reload", "command.reload", "-reload", "Reloads the configuration for a guild");
		
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		SwearsConfig sConfig = info.getHandler().getBot().getSwearsConfig();
		try {
			sConfig.loadSwears(info.getGid());
		} catch (Exception e) {
			logger.error("Error reloading server info",e);
			return new CommandResponse("Failed to reload server info",true);
		}
		
		return new CommandResponse("Reloaded server info!");
	}

}
