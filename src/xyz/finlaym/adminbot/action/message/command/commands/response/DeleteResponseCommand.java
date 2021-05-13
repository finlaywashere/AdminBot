package xyz.finlaym.adminbot.action.message.command.commands.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.utils.MathUtils;

public class DeleteResponseCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(DeleteResponseCommand.class);
	
	public DeleteResponseCommand() {
		super("deleteresponse","command.deleteresponse","-deleteresponse <id>","Deletes a trigger + response from the bot");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		if(command.length < 2) {
			return new CommandResponse("Usage: "+usage,true);
		}
		if(!MathUtils.isInt(command[1])) {
			return new CommandResponse("Usage: "+usage,true);
		}
		int id = Integer.valueOf(command[1])-1;
		long gid = info.getGid();
		ServerConfig sConfig = info.getHandler().getBot().getServerConfig();
		if(sConfig.getResponses(gid) == null) {
			try {
				sConfig.loadConfig(gid);
			} catch (Exception e) {
				logger.error("Failed to load server config in delete response command",e);
				return new CommandResponse("Error: Failed to load database!",true);
			}
			if(sConfig.getResponses(gid) == null || sConfig.getResponses(gid).size() == 0) {
				return new CommandResponse("This guild has no custom responses!");
			}
		}
		if(id < 0 || id >= sConfig.getResponses(gid).size()) {
			return new CommandResponse("Error: Id is less than 1 or greater than the number of responses!\nUse `-listresponses` to find an id to delete",true);
		}
		sConfig.getResponses(gid).remove(id);
		try {
			sConfig.saveConfig(gid);
		} catch (Exception e) {
			logger.error("Failed to save server config in delete response command",e);
			return new CommandResponse("Error: Failed to save changes to database!",true);
		}
		return new CommandResponse("Successfully removed response!");
	}
}
