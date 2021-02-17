package xyz.finlaym.adminbot.action.message.command.commands.session;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.session.Session;

public class StartSessionCommand extends Command{

	public StartSessionCommand() {
		super("startsession", "command.startsession", "-startsession", "Starts a command session");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		handler.getBot().getSessionConfig().setSession(channel.getGuild().getIdLong(), member.getIdLong(), new Session());
		channel.sendMessage("Started command session!").queue();
	}

}
