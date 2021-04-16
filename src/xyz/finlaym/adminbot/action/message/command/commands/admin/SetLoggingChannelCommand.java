package xyz.finlaym.adminbot.action.message.command.commands.admin;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class SetLoggingChannelCommand extends Command{

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
			e.printStackTrace();
			channel.sendMessage("Critical Error: Failed to save flags!").queue();
			return;
		}
		if(!silence)
			channel.sendMessage("Successfully set logging channel!").queue();
		if(silence)
			message.delete().queue();
	}
}
