package xyz.finlaym.adminbot.action.message.command.commands.swear;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.message.swear.SwearWord;

public class ListSwearsCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(ListSwearsCommand.class);
	
	public ListSwearsCommand() {
		super("listswears", "command.listswears", "-listswears", "Displays all of the blacklisted words on this guild");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		CommandHandler handler = info.getHandler();
		String s = "Id\t\tTrigger\t\tType\t\tRole";
		List<SwearWord> swears = handler.getBot().getSwearsConfig().getSwears(info.getGid());
		if(swears == null) {
			try {
				handler.getBot().getSwearsConfig().loadSwears(info.getGid());
			} catch (Exception e) {
				logger.error("Failed to load server config in list swears command", e);
				return new CommandResponse("Error loading swear words from database!",true);
			}
			swears = handler.getBot().getSwearsConfig().getSwears(info.getGid());
			if(swears == null || swears.size() == 0) {
				return new CommandResponse("This guild has no blacklisted words!");
			}
		}
		for(int i = 0; i < swears.size(); i++) {
			SwearWord swear = swears.get(i);
			s += "\n"+(i+1)+"\t\t"+swear.getWord()+"\t\t"+swear.getType()+"\t\t"+swear.getMuteRole();
		}
		return new CommandResponse(s);
	}

}
