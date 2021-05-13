package xyz.finlaym.adminbot.action.message.command.commands.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class SetLoggingChannelCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(SetLoggingChannelCommand.class);
	
	public SetLoggingChannelCommand() {
		super("setloggingchannel", "command.setloggingchannel", "-setloggingchannel <tag channel>", "Sets the server's logging channel");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		List<TextChannel> mentions = info.getChannelMentions();
		if(mentions.size() != 1) {
			return new CommandResponse("Usage: "+usage,true);
		}
		TextChannel logChannel = mentions.get(0);
		long id = logChannel.getIdLong();
		ServerConfig sConfig = info.getHandler().getBot().getServerConfig();
		sConfig.setLoggingChannel(info.getGid(), id);
		try {
			sConfig.saveConfig(info.getGid());
		} catch (Exception e) {
			logger.error("Failed to save server config in set logging channel command", e);
			return new CommandResponse("Critical Error: Failed to save flags!",true);
		}
		return new CommandResponse("Successfully set logging channel!",true);
	}
}
