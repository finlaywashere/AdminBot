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
		String oldHelp = help;
		for(Command c : commands) {
			help += "\n-"+c.getName()+" : "+c.getDescription()+" : "+c.getUsage();
			String[] split = help.split("&t");
			if(split[split.length-1].length() >= 2000) {
				String diff = help.substring(oldHelp.length());
				help = oldHelp;
				help += "&t";
				help += diff;
			}
			oldHelp = help;
		}
		for(String s : help.split("&t")) {
			channel.sendMessage(s).queue();
		}
		if(silence)
			message.delete().queue();
	}
}
