package xyz.finlaym.adminbot.action.message.command.commands;

import java.util.List;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;

public class HelpCommand extends Command{

	public HelpCommand() {
		super("help", "command.help", "-help", "Shows the help menu");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		CommandHandler handler = info.getHandler();
		List<Command> commands = handler.getCommands();
		String help = "Command usage: ";
		for(Command c : commands) {
			help += "\n-"+c.getName()+" : "+c.getDescription()+" : "+c.getUsage();
		}
		return new CommandResponse(help);
	}
}
