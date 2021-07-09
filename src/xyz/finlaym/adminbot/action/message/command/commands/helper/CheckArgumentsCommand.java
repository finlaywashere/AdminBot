package xyz.finlaym.adminbot.action.message.command.commands.helper;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.utils.MathUtils;

public class CheckArgumentsCommand extends Command{

	public CheckArgumentsCommand() {
		super("checkarguments", "commands.checkarguments", "-checkarguments <less/equal/greater> <expected argument count> [arguments]", "Causes execution to stop if the argument requirement is not met");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		if(command.length < 3)
			return new CommandResponse("Usage: "+usage,true);
		if(!MathUtils.isInt(command[2]))
			return new CommandResponse("Usage: "+usage,true);
		int expected = Integer.valueOf(command[2]);
		int count = command.length - 3;
		if(command[1].equalsIgnoreCase("less")) {
			if(count < expected)
				return new CommandResponse("");
		}else if(command[1].equalsIgnoreCase("equal")) {
			if(count == expected)
				return new CommandResponse("");
		}else if(command[1].equalsIgnoreCase("greater")) {
			if(count > expected)
				return new CommandResponse("");
		}else {
			return new CommandResponse("Usage: "+usage,true);
		}
		return new CommandResponse("",true);
	}
}
