package xyz.finlaym.adminbot.action.message.command.commands.swear;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.swear.SwearWord;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;
import xyz.finlaym.adminbot.utils.MathUtils;

public class DeleteSwearCommand extends Command {

	private static final Logger logger = LoggerFactory.getLogger(DeleteSwearCommand.class);
	
	public DeleteSwearCommand() {
		super("deleteswear", "command.deleteswear", "-deleteswear <id>", "Deletes a swear word by id");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		if(command.length < 2) {
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		if(!MathUtils.isInt(command[1])) {
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		
		Guild guild = channel.getGuild();
		SwearsConfig sConfig = handler.getBot().getSwearsConfig();
		
		int id = Integer.valueOf(command[1]) - 1;
		List<SwearWord> swears = sConfig.getSwears(guild.getIdLong());
		if(swears == null) {
			try {
				sConfig.loadSwears(guild.getIdLong());
			} catch (Exception e) {
				logger.error("Failed to load server config in delete swear command", e);
				channel.sendMessage("Error loading swears from database!").queue();
				return;
			}
			swears = sConfig.getSwears(guild.getIdLong());
			if(swears == null) {
				channel.sendMessage("This guild has no blacklisted words!").queue();
				return;
			}
		}
		if(id < 0 || id >= swears.size()) {
			channel.sendMessage("Error: Id is less than 1 or greater than the number of responses!\nUse `-listresponses` to find an id to delete").queue();
			return;
		}
		sConfig.getSwears(guild.getIdLong()).remove(id);
		try {
			sConfig.saveSwears(guild.getIdLong());
		} catch (Exception e) {
			logger.error("Failed to save server config in delete swear command", e);
			channel.sendMessage("Error: Failed to save changes to database!").queue();
			return;
		}
		
		if(!silence)
			channel.sendMessage("Successfully deleted swears from DB!").queue();
		if(silence)
			message.delete().queue();
	}

}
