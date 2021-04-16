package xyz.finlaym.adminbot.action.message.command.commands.admin;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class GetFlagsCommand extends Command{

	public GetFlagsCommand() {
		super("getflags", "command.getflags", "-getflags", "Shows the servers enabled features");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		ServerConfig sConfig = handler.getBot().getServerConfig();
		try {
			sConfig.loadConfig(channel.getGuild().getIdLong());
		} catch (Exception e1) {
			e1.printStackTrace();
			channel.sendMessage("Critical Error: Failed to save flags!").queue();
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
