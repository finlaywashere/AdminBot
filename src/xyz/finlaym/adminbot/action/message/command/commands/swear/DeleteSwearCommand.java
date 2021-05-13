package xyz.finlaym.adminbot.action.message.command.commands.swear;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Guild;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.message.swear.SwearWord;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;
import xyz.finlaym.adminbot.utils.MathUtils;

public class DeleteSwearCommand extends Command {

	private static final Logger logger = LoggerFactory.getLogger(DeleteSwearCommand.class);
	
	public DeleteSwearCommand() {
		super("deleteswear", "command.deleteswear", "-deleteswear <id>", "Deletes a swear word by id");
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
		
		Guild guild = info.getGuild();
		CommandHandler handler = info.getHandler();
		SwearsConfig sConfig = handler.getBot().getSwearsConfig();
		
		int id = Integer.valueOf(command[1]) - 1;
		List<SwearWord> swears = sConfig.getSwears(guild.getIdLong());
		if(swears == null) {
			try {
				sConfig.loadSwears(guild.getIdLong());
			} catch (Exception e) {
				logger.error("Failed to load server config in delete swear command", e);
				return new CommandResponse("Error loading swears from database!",true);
			}
			swears = sConfig.getSwears(guild.getIdLong());
			if(swears == null || swears.size() == 0) {
				return new CommandResponse("This guild has no blacklisted words!");
			}
		}
		if(id < 0 || id >= swears.size()) {
			return new CommandResponse("Error: Id is less than 1 or greater than the number of responses!\nUse `-listresponses` to find an id to delete",true);
		}
		sConfig.getSwears(guild.getIdLong()).remove(id);
		try {
			sConfig.saveSwears(guild.getIdLong());
		} catch (Exception e) {
			logger.error("Failed to save server config in delete swear command", e);
			return new CommandResponse("Error: Failed to save changes to database!",true);
		}
		return new CommandResponse("Successfully deleted swears from DB!");
	}

}
