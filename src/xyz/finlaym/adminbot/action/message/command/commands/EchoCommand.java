package xyz.finlaym.adminbot.action.message.command.commands;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;

public class EchoCommand extends Command{

	public EchoCommand() {
		super("echo", "command.echo", "-echo [text]", "Makes the bot echo text!");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		String text = "";
		for(int i = 1; i < command.length; i++) {
			text += command[i] + " ";
		}
		return new CommandResponse(text);
	}
}
