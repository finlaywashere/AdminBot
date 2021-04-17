package xyz.finlaym.adminbot.action.message.command.commands.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;

public class ReloadCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(ReloadCommand.class);
	
	public ReloadCommand() {
		super("reload", "command.reload", "-reload", "Reloads the configuration for a guild");
		
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		SwearsConfig sConfig = handler.getBot().getSwearsConfig();
		try {
			sConfig.loadSwears(channel.getGuild().getIdLong());
		} catch (Exception e) {
			logger.error("Error reloading server info",e);
			channel.sendMessage("Failed to reload server info").queue();
			return;
		}
		if(!silence)
			channel.sendMessage("Reloaded server info!").queue();
		if(silence)
			message.delete().queue();
	}

}
