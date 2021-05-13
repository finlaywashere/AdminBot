package xyz.finlaym.adminbot.action.message.command.commands.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class GetFlagsCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(GetFlagsCommand.class);
	
	public GetFlagsCommand() {
		super("getflags", "command.getflags", "-getflags", "Shows the servers enabled features");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		CommandHandler handler = info.getHandler();
		ServerConfig sConfig = handler.getBot().getServerConfig();
		try {
			sConfig.loadConfig(info.getGid());
		} catch (Exception e) {
			logger.error("Failed to load server info in get flags command",e);
			return new CommandResponse("Critical Error: Failed to load server info!",true);
		}
		long flags = sConfig.getFlags(info.getGid());
		String s = "Flag\t\tState\n";
		s += "CURRENCY\t\t"+((flags & ServerConfig.CURRENCY_FLAG) == 1 ? "ON" : "OFF");
		return new CommandResponse(s);
	}
}
