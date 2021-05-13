package xyz.finlaym.adminbot.action.message.command.commands.helper;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.message.command.CommandState;
import xyz.finlaym.adminbot.utils.MathUtils;

public class SetStateCommand extends Command{

	public SetStateCommand() {
		super("setstate", "command.setstate", "-setstate <silenced/channel> <true/false/channel tag>", "Sets the internal state for the next command(s)");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		if(command.length != 3) {
			return new CommandResponse("Usage: "+usage,true);
		}
		CommandState state = info.getState();
		
		switch(command[1].toLowerCase()) {
		case "silenced":
			if(!MathUtils.isBoolean(command[2]))
				return new CommandResponse("Usage: "+usage,true);
			state.setSilenced(Boolean.valueOf(command[2]));
			break;
		case "channel":
			if(info.getChannelMentions().size() != 1)
				return new CommandResponse("Usage: "+usage,true);
			state.setOutputChannel(info.getChannelMentions().get(0));
			break;
		default:
			return new CommandResponse("Usage: "+usage,true);
		}
		return new CommandResponse("Successfully set command state!", false, false, state);
	}

}
