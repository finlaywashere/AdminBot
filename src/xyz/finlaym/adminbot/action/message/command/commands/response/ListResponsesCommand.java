package xyz.finlaym.adminbot.action.message.command.commands.response;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.message.response.CustomResponse;

public class ListResponsesCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(ListResponsesCommand.class);
	
	public ListResponsesCommand() {
		super("listresponses", "command.listresponses", "-listresponses", "Lists all the bot responses in this guild");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		CommandHandler handler = info.getHandler();
		List<CustomResponse> responses = handler.getBot().getServerConfig().getResponses(info.getGid());
		if(responses == null) {
			try {
				handler.getBot().getServerConfig().loadConfig(info.getGid());
			} catch (Exception e) {
				logger.error("Failed to load server config in list responses command", e);
				return new CommandResponse("Error: Failed to load database!",true);
			}
			responses = handler.getBot().getServerConfig().getResponses(info.getGid());
			if(responses == null || responses.size() == 0) {
				return new CommandResponse("This guild has no custom responses!");
			}
		}
		String s = "Custom Responses:\nId\tTrigger\t\tResponse\n";
		for(int i = 0; i < responses.size(); i++) {
			CustomResponse r = responses.get(i);
			s += "\n"+(i+1)+"\t"+r.getTrigger()+"\t\t"+r.getResponse().replaceAll("&comma", ",");
		}
		return new CommandResponse(s);
	}
}
