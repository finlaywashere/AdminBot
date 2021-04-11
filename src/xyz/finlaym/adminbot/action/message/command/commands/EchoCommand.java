package xyz.finlaym.adminbot.action.message.command.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;

public class EchoCommand extends Command{

	public EchoCommand() {
		super("echo", "command.echo", "-echo [text]", "Makes the bot echo text!");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		String text = "";
		for(int i = 1; i < command.length; i++) {
			text += command[i] + " ";
		}
		channel.sendMessage(text).queue();
		if(silence)
			message.delete().queue();
	}
}