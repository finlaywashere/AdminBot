package xyz.finlaym.adminbot.action.message.command.commands.session;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.session.Session;

public class StartSessionCommand extends Command{

	public StartSessionCommand() {
		super("startsession", "command.startsession", "-startsession", "Starts a command session");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		CommandHandler handler = info.getHandler();
		handler.getBot().getSessionConfig().setSession(info.getGid(), info.getUid(), new Session());
		return new CommandResponse("Started command session!");
	}

}
