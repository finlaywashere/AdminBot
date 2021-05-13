package xyz.finlaym.adminbot.action.message.command.commands.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class SetFlagCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(SetFlagCommand.class);
	
	public SetFlagCommand() {
		super("setflag", "command.setflag", "-setflag <name> <on/off>", "Turns on a feature for this server");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		if(command.length < 3) {
			// Send help menu
			return new CommandResponse("Usage: "+usage+"\nFlag Options: currency",true);
		}
		long bit = 0;
		switch(command[1].toLowerCase()) {
		case "currency":
			bit = ServerConfig.CURRENCY_FLAG;
			break;
		default:
			return new CommandResponse("Usage: "+usage,true);
		}
		ServerConfig sConfig = info.getHandler().getBot().getServerConfig();
		try {
			sConfig.loadConfig(info.getGid());
		} catch (Exception e) {
			logger.error("Failed to load server config in set flag command", e);
			return new CommandResponse("Critical Error: Failed to save flags!",true);
		}
		long oldConfig = sConfig.getFlags(info.getGid());
		long newValue = oldConfig;
		switch(command[2].toLowerCase()) {
		case "on":
			newValue |= bit;
			break;
		case "off":
			newValue &= ~bit;
			break;
		default:
			return new CommandResponse("Usage: "+usage,true);
		}
		sConfig.setFlags(info.getGid(), newValue);
		try {
			sConfig.saveConfig(info.getGid());
		} catch (Exception e) {
			logger.error("Failed to save server config in set flag command", e);
			return new CommandResponse("Critical Error: Failed to save flags!",true);
		}
		
		return new CommandResponse("Successfully set flag!");
	}
	
}
