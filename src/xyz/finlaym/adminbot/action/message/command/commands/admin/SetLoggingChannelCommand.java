package xyz.finlaym.adminbot.action.message.command.commands.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class SetLoggingChannelCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(SetLoggingChannelCommand.class);
	
	public SetLoggingChannelCommand() {
		super("setloggingchannel", "command.setloggingchannel", "-setloggingchannel <tag channel>", "Sets the server's logging channel");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		if(message.getMentionedChannels().size() != 1) {
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		TextChannel logChannel = message.getMentionedChannels().get(0);
		long id = logChannel.getIdLong();
		ServerConfig sConfig = handler.getBot().getServerConfig();
		sConfig.setLoggingChannel(channel.getGuild().getIdLong(), id);
		try {
			sConfig.saveConfig(channel.getGuild().getIdLong());
		} catch (Exception e) {
			logger.error("Failed to save server config in set logging channel command", e);
			channel.sendMessage("Critical Error: Failed to save flags!").queue();
			return;
		}
		if(!silence)
			channel.sendMessage("Successfully set logging channel!").queue();
		if(silence)
			message.delete().queue();
	}
}
