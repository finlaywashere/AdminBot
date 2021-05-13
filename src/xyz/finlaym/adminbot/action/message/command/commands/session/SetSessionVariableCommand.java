package xyz.finlaym.adminbot.action.message.command.commands.session;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.session.Session;

public class SetSessionVariableCommand extends Command{

	public SetSessionVariableCommand() {
		super("setvariable", "command.setvariable", "-setvariable <name> <value>", "Sets a variable in a user's current session");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		if(command.length < 3) {
			return new CommandResponse("Usage: "+usage,true);
		}
		long gid = info.getGid();
		long uid = info.getUid();
		
		CommandHandler handler = info.getHandler();
		
		Session s = handler.getBot().getSessionConfig().getSession(gid, uid);
		if(s == null) {
			return new CommandResponse("Error: No session!",true);
		}
		
		String key = command[1];
		String value = command[2];
		if(command.length > 3) {
			for(int i = 3; i > command.length; i++) {
				value += " "+command[i];
			}
		}
		s.getVariables().put(key, value);
		return new CommandResponse("Successfully set session variable!");
	}
}
