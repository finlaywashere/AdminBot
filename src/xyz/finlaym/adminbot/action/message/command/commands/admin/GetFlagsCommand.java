package xyz.finlaym.adminbot.action.message.command.commands.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class GetFlagsCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(GetFlagsCommand.class);
	
	public GetFlagsCommand() {
		super("getflags", "command.getflags", "-getflags", "Shows the servers enabled features");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		ServerConfig sConfig = handler.getBot().getServerConfig();
		try {
			sConfig.loadConfig(channel.getGuild().getIdLong());
		} catch (Exception e) {
			logger.error("Failed to load server info in get flags command",e);
			channel.sendMessage("Critical Error: Failed to load server info!").queue();
			return;
		}
		long flags = sConfig.getFlags(channel.getGuild().getIdLong());
		String s = "Flag\t\tState\n";
		s += "CURRENCY\t\t"+((flags & ServerConfig.CURRENCY_FLAG) == 1 ? "ON" : "OFF");
		channel.sendMessage(s).queue();
		
		if(silence)
			message.delete().queue();
	}
}
