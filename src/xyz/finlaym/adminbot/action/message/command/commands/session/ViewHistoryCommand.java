package xyz.finlaym.adminbot.action.message.command.commands.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.permission.PermissionDeclaration;
import xyz.finlaym.adminbot.action.session.HistoryElement;
import xyz.finlaym.adminbot.action.session.Session;
import xyz.finlaym.adminbot.utils.MathUtils;

public class ViewHistoryCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(ViewHistoryCommand.class);
	
	public ViewHistoryCommand() {
		super("viewhistory", "command.viewhistory", "-viewhistory [max] [tag or id]", "Shows a user their history or someone else's",
				new PermissionDeclaration("command.viewhistory.others", "Allows a user to view someone else's command history"));
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		CommandHandler handler = info.getHandler();
		int max = 20;
		long gid = info.getGid();
		long uid = info.getUid();
		if(command.length > 1) {
			if(!MathUtils.isInt(command[1])) {
				return new CommandResponse("Error: Max number of history elements must be an integer!",true);
			}
			max = Integer.valueOf(command[1]);
			if(command.length > 2) {
				try {
					if(!handler.getBot().getPermissionsConfig().checkPermission(info.getGuild(), info.getSender(), "command.viewhistory.others")) {
						return new CommandResponse("Error: Insufficient permissions to execute command!",true);
					}else {
						if(info.getMemberMentions().size() > 0) {
							uid = info.getMemberMentions().get(0).getIdLong();
						}else {
							if(!MathUtils.isLong(command[2])) {
								return new CommandResponse("Error: User id must be a number!",true);
							}
							uid = Long.valueOf(command[2]);
						}
					}
				} catch (Exception e) {
					logger.error("Error viewing user's session history",e);
					return new CommandResponse("Critical Error: Failed to check user's permissions!",true);
				}
			}
		}
		Session s = handler.getBot().getSessionConfig().getSession(gid, uid);
		if(s == null) {
			return new CommandResponse("Error: No session!",true);
		}
		String m = "User's command history:\n\n";
		for(int i = max-1; i >= 0; i--) {
			if(i >= s.getHistory().size())
				continue;
			HistoryElement elem = s.getHistory().get(i);
			m += "`"+elem.getMessage()+"`\n";
		}
		return new CommandResponse(m);
	}
}
