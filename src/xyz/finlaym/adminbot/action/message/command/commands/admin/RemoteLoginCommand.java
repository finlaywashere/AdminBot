package xyz.finlaym.adminbot.action.message.command.commands.admin;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;

public class RemoteLoginCommand extends Command{

	public RemoteLoginCommand() {
		super("login", "command.remotelogin", "-login <guild id> <channel id>", "Logs in to a remote server");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		//TODO: Add remote login stuff
		return null;
	}

}
