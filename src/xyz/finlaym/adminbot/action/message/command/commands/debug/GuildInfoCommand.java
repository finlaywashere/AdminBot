package xyz.finlaym.adminbot.action.message.command.commands.debug;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;

public class GuildInfoCommand extends Command{

	public GuildInfoCommand() {
		super("ginfo", "command.guildinfo", "-ginfo", "Shows information about a guild");
		
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		channel.sendMessage("This guild's id is "+channel.getGuild().getIdLong()).queue();
	}

}
