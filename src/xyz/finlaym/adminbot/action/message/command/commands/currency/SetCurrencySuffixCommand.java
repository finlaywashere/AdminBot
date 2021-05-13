package xyz.finlaym.adminbot.action.message.command.commands.currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class SetCurrencySuffixCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(SetCurrencySuffixCommand.class);
	
	public SetCurrencySuffixCommand() {
		super("setcurrencysuffix", "command.setcurrencysuffix", "-setcurrencysuffix <$,etc>", "Sets what currency is used by this guild", ServerConfig.CURRENCY_FLAG);
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		if(command.length < 2) {
			return new CommandResponse("Usage: "+usage,true);
		}
		String suffix = "";
		for(int i = 1; i < command.length; i++) {
			if(i == 1) {
				suffix += command[i];
			}else {
				suffix += " "+command[i];
			}
		}
		ServerConfig sConfig = info.getHandler().getBot().getServerConfig();
		long gid = info.getGid();
		sConfig.setCurrencySuffix(gid, suffix);
		try {
			sConfig.saveConfig(gid);
		} catch (Exception e) {
			logger.error("Failed to save server config in set currency suffix command", e);
			return new CommandResponse("Critical Error: Failed to save currency suffix to database!",true);
		}
		return new CommandResponse("Successfully set currency suffix to \""+suffix+"\"");
	}
}
