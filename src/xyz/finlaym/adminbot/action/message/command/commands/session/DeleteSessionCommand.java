package xyz.finlaym.adminbot.action.message.command.commands.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.permission.PermissionDeclaration;
import xyz.finlaym.adminbot.utils.MathUtils;

public class DeleteSessionCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(DeleteSessionCommand.class);
	
	public DeleteSessionCommand() {
		super("deletesession", "command.deletesession", "-deletesession [tag or id]", "Deletes either your own or someone else's command session", 
				new PermissionDeclaration("command.deletesession.others", "Allows you to delete other people's command sessions"));
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		CommandHandler handler = info.getHandler();
		if(info.getMemberMentions().size() > 0 && command.length > 1) {
			long id;
			if(info.getMemberMentions().size() > 0)
				id = info.getMemberMentions().get(0).getIdLong();
			else {
				if(!MathUtils.isLong(command[1])) {
					return new CommandResponse("Error: User id must be a number!",true);
				}
				id = Long.valueOf(command[1]);
			}
			try {
				if(handler.getBot().getPermissionsConfig().checkPermission(info.getGuild(), info.getSender(), "command.deletesession.others")) {
					handler.getBot().getSessionConfig().setSession(info.getGid(), id, null);
				}else {
					return new CommandResponse("Error: Insufficient permissions to execute command!",true);
				}
			} catch (Exception e) {
				logger.error("Error deleting session",e);
				return new CommandResponse("Critical Error: Failed to check user's permissions!",true);
			}
		}else {
			handler.getBot().getSessionConfig().setSession(info.getGid(), info.getUid(), null);
		}
		return new CommandResponse("Successfully deleted session!");
	}
}
