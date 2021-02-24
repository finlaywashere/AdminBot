package xyz.finlaym.adminbot.action.message.command.commands;

import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;

public class HelpCommand extends Command{

	public HelpCommand() {
		super("help", "command.help", "-help", "Shows the help menu");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		List<Command> commands = handler.getCommands();
		String help = "Command usage: ";
		for(Command c : commands) {
			help += "\n-"+c.getName()+" : "+c.getDescription()+" : "+c.getUsage();
		}
		channel.sendMessage(help).queue();
		if(silence)
			message.delete().queue();
	}
}
