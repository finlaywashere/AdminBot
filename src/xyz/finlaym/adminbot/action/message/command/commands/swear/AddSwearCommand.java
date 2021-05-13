package xyz.finlaym.adminbot.action.message.command.commands.swear;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Guild;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.message.swear.SwearWord;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;

public class AddSwearCommand extends Command {

	private static final Logger logger = LoggerFactory.getLogger(AddSwearCommand.class);
	
	public AddSwearCommand() {
		super("addswear", "command.addswear", "-addswear <swear1> [swear2...]", "Adds a swear to a server's blacklist");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		CommandHandler handler = info.getHandler();
		String[] command = info.getCommand();
		Guild guild = info.getGuild();
		SwearsConfig sConfig = handler.getBot().getSwearsConfig();
		for(int i = 1; i < command.length; i++) {
			String swear = command[i];
			try {
				sConfig.addSwear(SwearWord.fromString(swear.replaceAll("_", " ")),guild.getIdLong());
				sConfig.saveSwears(guild.getIdLong());
			}catch(Exception e) {
				logger.error("Failed to save swear word in add swear command", e);
				return new CommandResponse("Critical Error: Failed to save swear words to database!",true);
			}
		}
		return new CommandResponse("Successfully added swears to DB!");
	}
}
