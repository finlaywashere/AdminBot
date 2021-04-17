package xyz.finlaym.adminbot.action.message.command.commands.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class SetFlagCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(SetFlagCommand.class);
	
	public SetFlagCommand() {
		super("setflag", "command.setflag", "-setflag <name> <on/off>", "Turns on a feature for this server");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		if(command.length < 3) {
			// Send help menu
			channel.sendMessage("Usage: "+usage+"\nFlag Options: currency").queue();
			return;
		}
		long bit = 0;
		switch(command[1].toLowerCase()) {
		case "currency":
			bit = ServerConfig.CURRENCY_FLAG;
			break;
		default:
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		ServerConfig sConfig = handler.getBot().getServerConfig();
		try {
			sConfig.loadConfig(channel.getGuild().getIdLong());
		} catch (Exception e) {
			logger.error("Failed to load server config in set flag command", e);
			channel.sendMessage("Critical Error: Failed to save flags!").queue();
			return;
		}
		long oldConfig = sConfig.getFlags(channel.getGuild().getIdLong());
		long newValue = oldConfig;
		switch(command[2].toLowerCase()) {
		case "on":
			newValue |= bit;
			break;
		case "off":
			newValue &= ~bit;
			break;
		default:
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		sConfig.setFlags(channel.getGuild().getIdLong(), newValue);
		try {
			sConfig.saveConfig(channel.getGuild().getIdLong());
		} catch (Exception e) {
			logger.error("Failed to save server config in set flag command", e);
			channel.sendMessage("Critical Error: Failed to save flags!").queue();
			return;
		}
		
		if(!silence)
			channel.sendMessage("Successfully set flag!").queue();
		if(silence)
			message.delete().queue();
	}
	
}
