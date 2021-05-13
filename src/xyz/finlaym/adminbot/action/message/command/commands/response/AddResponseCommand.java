package xyz.finlaym.adminbot.action.message.command.commands.response;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.message.response.CustomResponse;

public class AddResponseCommand extends Command {

	private static final Logger logger = LoggerFactory.getLogger(AddResponseCommand.class);
	
	public AddResponseCommand() {
		super("addresponse", "command.addresponse", "-addresponse <trigger regex> <response>", "Sends a response every time a message matches a pattern, can substiture $u and $c for user mention and channel mention");
	}
	
	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		if(command.length < 3) {
			return new CommandResponse(usage,true);
		}
		String trigger = command[1].replaceAll("_", " ");
		String response = "";
		for(int i = 2; i < command.length; i++) {
			response += command[i] + " ";
		}
		CommandHandler handler = info.getHandler();
		List<CustomResponse> currResponses = handler.getBot().getServerConfig().getResponses(info.getGid());
		if(currResponses == null)
			currResponses = new ArrayList<CustomResponse>();
		currResponses.add(CustomResponse.fromStringSingle(response.replaceAll(",", "&comma")+","+trigger));
		handler.getBot().getServerConfig().setResponses(info.getGid(), currResponses);
		try {
			handler.getBot().getServerConfig().saveConfig(info.getGid());
		} catch (Exception e) {
			logger.error("Failed to save server config in add response command", e);
			return new CommandResponse("Critical Error: Failed to save responses!",true);
		}
		return new CommandResponse("Successfully added custom response to database!");
	}
}
